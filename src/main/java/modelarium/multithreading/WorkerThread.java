package modelarium.multithreading;

import modelarium.Config;
import modelarium.clock.SimulationClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.results.mutable.MutableResults;
import modelarium.results.mutable.MutableResultsForAgents;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Represents a single worker thread responsible for simulating one subset of agents
 * across the configured number of ticks.
 *
 * <p>Each {@code WorkerThread} operates on its own {@link AgentSet}, may use a local
 * {@link ContextCache} for caching, and optionally communicates with a coordinator via
 * {@link RequestResponseInterface} if synchronisation is enabled.
 */
public class WorkerThread implements Callable<MutableResults> {

    /** The name or ID assigned to this worker thread (usually based on core index) */
    private final String threadName;

    /** Global simulation settings shared across threads */
    private final Config config;

    /** Interface to coordinate requests and responses across workers (if sync enabled) */
    private final RequestResponseController requestResponseController;

    private final Environment environment;

    /** The original set of agents this worker is responsible for simulating */
    private final AgentSet agentsInThread;

    private final SimulationClock sharedClock;

    /** A duplicate of the agent set to allow for safe merging during synchronisation */
    private final AgentSet updatedAgents;

    /**
     * Constructs a new worker thread to simulate a subset of agents.
     *
     * @param threadName the thread's name (typically its numeric ID as a string)
     * @param config the simulation settings
     * @param requestResponseController the controller for cross-thread coordination
     * @param agentsInThread the agents assigned to this thread
     */
    public WorkerThread(String threadName,
                        Config config,
                        RequestResponseController requestResponseController,
                        Environment environment,
                        AgentSet agentsInThread,
                        SimulationClock sharedClock
    ) {
        this.threadName = Objects.requireNonNull(threadName, "threadName");
        this.config = Objects.requireNonNull(config, "settings");
        this.requestResponseController = Objects.requireNonNull(requestResponseController, "requestResponseController");
        this.environment = environment;
        this.agentsInThread = Objects.requireNonNull(agentsInThread, "agents");
        this.sharedClock = sharedClock;
        this.updatedAgents = this.agentsInThread.duplicate();
    }

    /**
     * Executes the simulation loop for this worker.
     *
     * <p>This includes calling the scheduler each tick, synchronising with the coordinator
     * if needed, and collecting agent results after the simulation ends.
     *
     * @return a {@link MutableResults} object containing final agent-level outputs
     */
    @Override
    public MutableResults call() throws InterruptedException {
        SimulationClock clock = Objects.requireNonNullElseGet(sharedClock, () -> new SimulationClock(config.tickCount()));
        ContextCache cache = new ContextCache();

        for (Agent agent : agentsInThread) {
            Environment localEnvironment = null;

            if (config.areThreadsSynced())
                localEnvironment = environment.clone();

            agent.createContext(
                    agentsInThread,
                    config,
                    cache,
                    clock,
                    requestResponseController.getInterface(agent.name()),
                    localEnvironment
            );
        }

        RequestResponseInterface requestResponseInterface = requestResponseController.getInterface(threadName);

        // Initial broadcast of agent state to coordinator
        if (config.areThreadsSynced())
            requestResponseInterface.updateCoordinatorAgents(agentsInThread);

        // Simulation main loop
        while (!clock.isFinished()) {
            config.scheduler().runTick(agentsInThread);

            if (config.areThreadsSynced()) {
                requestResponseInterface.waitUntilAllWorkersFinishTick();
                agentsInThread.add(updatedAgents); // Merge agent updates
                requestResponseInterface.updateCoordinatorAgents(agentsInThread);
                requestResponseInterface.waitUntilAllWorkersUpdateCoordinator();
            } else {
                clock.triggerTick();
            }

            cache.clear();
        }

        // Final setup and result collection
        MutableResultsForAgents agentsResults = new MutableResultsForAgents(agentsInThread);
        MutableResults results = new MutableResults();
        results.setAgentNames(agentsInThread);
        results.setAgentResults(agentsResults);

        return results;
    }
}
