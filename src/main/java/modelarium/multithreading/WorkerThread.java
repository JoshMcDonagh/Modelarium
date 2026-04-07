package modelarium.multithreading;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.results.Results;
import modelarium.results.ResultsForAgents;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Represents a single worker thread responsible for simulating one subset of agents
 * across the configured number of ticks.
 *
 * <p>Each {@code WorkerThread} operates on its own {@link AgentSet}, may use a local
 * {@link ContextCache} for caching, and optionally communicates with a coordinator via
 * {@link RequestResponseInterface} if synchronisation is enabled.
 *
 * @param <T> the type of {@link Results} this worker will return
 */
public class WorkerThread<T extends Results> implements Callable<Results> {

    /** The name or ID assigned to this worker thread (usually based on core index) */
    private final String threadName;

    /** Global simulation settings shared across threads */
    private final Config config;

    /** Interface to coordinate requests and responses across workers (if sync enabled) */
    private final RequestResponseController requestResponseController;

    private final Environment environment;

    /** The original set of agents this worker is responsible for simulating */
    private final AgentSet agentsInThread;

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
                        AgentSet agentsInThread) {
        this.threadName = Objects.requireNonNull(threadName, "threadName");
        this.config = Objects.requireNonNull(config, "settings");
        this.requestResponseController = Objects.requireNonNull(requestResponseController, "requestResponseController");
        this.environment = environment;
        this.agentsInThread = Objects.requireNonNull(agentsInThread, "agents");
        this.updatedAgents = this.agentsInThread.duplicate();
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
    public Results call() throws InterruptedException {
        Clock clock = new Clock(config.tickCount());
        ContextCache cache = new ContextCache();

        for (Agent agent : agentsInThread) {
            Environment localEnvironment = null;

            if (config.areThreadsSynced())
                localEnvironment = environment.clone();

            AgentContext agentContext = new AgentContext(
                    agent,
                    agentsInThread,
                    config,
                    cache,
                    clock,
                    new RequestResponseInterface(agent.name(), config, requestResponseController),
                    localEnvironment
            );

            agent.setContext(agentContext);
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
            }

            cache.clear();
            clock.triggerTick();
        }

        // Final setup and result collection
        ResultsForAgents agentsResults = new ResultsForAgents(agentsInThread);
        Results results = new Results();
        results.setAgentNames(agentsInThread);
        results.setAgentResults(agentsResults);

        return results;
    }
}
