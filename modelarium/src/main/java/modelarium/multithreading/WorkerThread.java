package modelarium.multithreading;

import modelarium.Config;
import modelarium.clock.ImmutableClock;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.entities.environments.ImmutableEnvironment;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.results.mutable.MutableResults;
import modelarium.results.mutable.MutableResultsForAgents;
import modelarium.utils.Cloners;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.random.RandomGenerator;

/**
 * Represents a single worker thread responsible for simulating one subset of agents
 * across the configured number of ticks.
 *
 * <p>Each {@code WorkerThread} operates on its own {@link MutableAgentSet}, may use a local
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

    /** The model's environment, which this worker clones locally when threads are synchronised */
    private final MutableEnvironment environment;

    /** The original set of agents this worker is responsible for simulating */
    private final MutableAgentSet agentsInThread;

    /** The clock shared with the co-ordinator and other workers, or null if threads are not synchronised */
    private final MutableClock sharedClock;

    /** A duplicate of the agent set to allow for safe merging during synchronisation */
    private final MutableAgentSet updatedAgents;

    /** The splittable random generator this worker and its agents can use */
    private final RandomGenerator randomGenerator;

    /**
     * Constructs a new worker thread to simulate a subset of agents.
     *
     * @param threadName the thread's name (typically its numeric ID as a string)
     * @param config the simulation settings
     * @param requestResponseController the controller for cross-thread coordination
     * @param environment the model's environment
     * @param agentsInThread the agents assigned to this thread
     * @param sharedClock the clock used to synchronise the entities and cores in the model
     * @param randomGenerator the splittable random generator this worker and its agents can use
     */
    public WorkerThread(String threadName,
                        Config config,
                        RequestResponseController requestResponseController,
                        MutableEnvironment environment,
                        MutableAgentSet agentsInThread,
                        MutableClock sharedClock,
                        RandomGenerator randomGenerator
    ) {
        this.threadName = Objects.requireNonNull(threadName, "threadName");
        this.config = Objects.requireNonNull(config, "config");
        this.requestResponseController = Objects.requireNonNull(requestResponseController, "requestResponseController");
        this.environment = environment;
        this.agentsInThread = Objects.requireNonNull(agentsInThread, "agents");
        this.sharedClock = sharedClock;
        this.updatedAgents = this.agentsInThread.duplicate();
        this.randomGenerator = Objects.requireNonNull(randomGenerator, "randomGenerator");
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
        MutableClock clock = Objects.requireNonNullElseGet(sharedClock, () -> new MutableClock(config.tickCount()));
        ContextCache cache = new ContextCache();

        for (MutableAgent agent : agentsInThread) {
            MutableEnvironment localEnvironment = null;

            if (config.areThreadsSynced())
                localEnvironment = Cloners.standard().deepClone(environment);

            agent.createContext(
                    agentsInThread,
                    config,
                    cache,
                    clock,
                    requestResponseController,
                    localEnvironment,
                    randomGenerator
            );
        }

        ImmutableClock immutableClock = new ImmutableClock(clock);
        ImmutableEnvironment immutableEnvironment = new ImmutableEnvironment(environment);

        RequestResponseInterface requestResponseInterface = requestResponseController.getInterface(threadName);

        // Initial broadcast of agent state to coordinator
        if (config.areThreadsSynced())
            requestResponseInterface.updateCoordinatorAgents(agentsInThread);

        // Simulation main loop
        while (!clock.isFinished()) {
            config.scheduler().runTick(
                    threadName,
                    immutableClock,
                    immutableEnvironment,
                    agentsInThread,
                    randomGenerator
            );

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
