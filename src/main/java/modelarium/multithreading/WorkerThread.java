package modelarium.multithreading;

import modelarium.ModelClock;
import modelarium.ModelConfig;
import modelarium.agents.Agent;
import modelarium.agents.sets.AgentSet;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.multithreading.utils.WorkerCache;
import modelarium.results.AgentResults;
import modelarium.results.Results;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Represents a single worker thread responsible for simulating one subset of agents
 * across the configured number of ticks.
 *
 * <p>Each {@code WorkerThread} operates on its own {@link AgentSet}, may use a local
 * {@link WorkerCache} for caching, and optionally communicates with a coordinator via
 * {@link RequestResponseInterface} if synchronisation is enabled.
 *
 * @param <T> the type of {@link Results} this worker will return
 */
public class WorkerThread<T extends Results> implements Callable<Results> {

    /** The name or ID assigned to this worker thread (usually based on core index) */
    private final String threadName;

    /** Global simulation settings shared across threads */
    private final ModelConfig settings;

    /** Interface to coordinate requests and responses across workers (if sync enabled) */
    private final RequestResponseController requestResponseController;

    /** The original set of agents this worker is responsible for simulating */
    private final AgentSet agents;

    /** A duplicate of the agent set to allow for safe merging during synchronisation */
    private final AgentSet updatedAgents;

    /**
     * Constructs a new worker thread to simulate a subset of agents.
     *
     * @param threadName the thread's name (typically its numeric ID as a string)
     * @param settings the simulation settings
     * @param requestResponseController the controller for cross-thread coordination
     * @param agents the agents assigned to this thread
     */
    public WorkerThread(String threadName,
                        ModelConfig settings,
                        RequestResponseController requestResponseController,
                        AgentSet agents) {
        this.threadName = Objects.requireNonNull(threadName, "threadName");
        this.settings = Objects.requireNonNull(settings, "settings");
        this.requestResponseController = Objects.requireNonNull(requestResponseController, "requestResponseController");
        this.agents = Objects.requireNonNull(agents, "agents");
        this.updatedAgents = this.agents.duplicate();
    }

    /**
     * Executes the simulation loop for this worker.
     *
     * <p>This includes calling the scheduler each tick, synchronising with the coordinator
     * if needed, and collecting agent results after the simulation ends.
     *
     * @return a {@link Results} object containing final agent-level outputs
     */
    @Override
    public Results call() throws InterruptedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ModelClock modelClock = new ModelClock(settings.getNumOfTicksToRun(), settings.getNumOfWarmUpTicks());
        for (Agent agent : agents)
            agent.getModelElementAccessor().setModelClock(modelClock);

        WorkerCache cache = settings.getIsCacheUsed()
                ? new WorkerCache(settings.getDoAgentStoresHoldAgentCopies())
                : null;

        RequestResponseInterface requestResponseInterface = requestResponseController.getInterface(threadName);

        // Initial broadcast of agent state to coordinator
        if (settings.getAreProcessesSynced())
            requestResponseInterface.updateCoordinatorAgents(agents);

        agents.setup();

        // Simulation main loop
        while (modelClock.isRunning()) {
            settings.getModelScheduler().runTick(agents);

            if (settings.getAreProcessesSynced()) {
                requestResponseInterface.waitUntilAllWorkersFinishTick();
                agents.add(updatedAgents); // Merge agent updates
                requestResponseInterface.updateCoordinatorAgents(agents);
                requestResponseInterface.waitUntilAllWorkersUpdateCoordinator();
            }

            if (cache != null)
                cache.clear();

            modelClock.triggerTick();
        }

        // Final setup and result collection
        AgentResults agentResults = new AgentResults(agents);
        Results results = settings.getResults();
        results.setAgentNames(agents);
        results.setAgentResults(agentResults);

        return results;
    }
}
