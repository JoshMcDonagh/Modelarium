package modelarium.multithreading;

import modelarium.Config;
import modelarium.clock.ImmutableClock;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.ReadOnlyEnvironment;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.results.mutable.Results;
import modelarium.results.mutable.ResultsForAgents;
import modelarium.utils.Cloners;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.random.RandomGenerator;

/**
 * Represents a single worker thread responsible for simulating one subset of agents
 * across the configured number of ticks.
 *
 * <p>Each {@code WorkerThread} operates on its own {@link AgentSet}, may use a local
 * {@link ContextCache} for caching, and optionally communicates with a coordinator via
 * {@link RequestResponseInterface} if synchronisation is enabled.
 */
public class WorkerThread implements Callable<Results> {

    /** The name or ID assigned to this worker thread (usually based on core index) */
    private final String threadName;

    /** Global simulation settings shared across threads */
    private final Config config;

    /** Interface to coordinate requests and responses across workers (if sync enabled) */
    private final RequestResponseController requestResponseController;

    /** The model's environment, which this worker clones locally when threads are synchronised */
    private final Environment environment;

    /** The original set of agents this worker is responsible for simulating */
    private final AgentSet agentsInThread;

    /** The clock shared with the co-ordinator and other workers, or null if threads are not synchronised */
    private final MutableClock sharedClock;

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
                        Environment environment,
                        AgentSet agentsInThread,
                        MutableClock sharedClock,
                        RandomGenerator randomGenerator
    ) {
        this.threadName = Objects.requireNonNull(threadName, "threadName");
        this.config = Objects.requireNonNull(config, "config");
        this.requestResponseController = Objects.requireNonNull(requestResponseController, "requestResponseController");
        this.environment = environment;
        this.agentsInThread = Objects.requireNonNull(agentsInThread, "agents");
        this.sharedClock = sharedClock;
        this.randomGenerator = Objects.requireNonNull(randomGenerator, "randomGenerator");
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
        MutableClock clock = Objects.requireNonNullElseGet(sharedClock, () -> new MutableClock(config.tickCount()));
        ContextCache cache = new ContextCache();

        AgentSet visibleAgents = Cloners.standard().deepClone(agentsInThread);

        Environment localEnvironment = null;
        if (config.areThreadsSynced())
            localEnvironment = Cloners.standard().deepClone(environment);

        for (Agent agent : agentsInThread) {
            agent.createContext(
                    visibleAgents,
                    config,
                    cache,
                    clock,
                    requestResponseController,
                    localEnvironment,
                    randomGenerator
            );
        }

        ImmutableClock immutableClock = new ImmutableClock(clock);
        ReadOnlyEnvironment immutableEnvironment = new ReadOnlyEnvironment(environment);

        RequestResponseInterface requestResponseInterface = requestResponseController.getInterface(threadName);

        // Simulation main loop
        while (!clock.isFinished()) {
            config.scheduler().runTick(
                    threadName,
                    immutableClock,
                    immutableEnvironment,
                    agentsInThread,
                    randomGenerator
            );

            AgentSet agentsToAdd = new AgentSet();
            List<String> agentsToKill = new ArrayList<>();

            for (Agent agent : agentsInThread) {
                agentsToAdd.add(agent.getAddedAgents());
                agentsToKill.addAll(agent.getKilledAgentNames());
            }

            agentsInThread.update(agentsToAdd, true);
            for (String agentName : agentsToKill)
                agentsInThread.get(agentName).kill();

            visibleAgents.update(agentsInThread, true);

            if (config.areThreadsSynced()) {
                requestResponseInterface.waitUntilAllWorkersFinishTick();
                requestResponseInterface.updateCoordinatorAgents(agentsInThread);
                requestResponseInterface.waitUntilAllWorkersUpdateCoordinator();
            } else {
                clock.triggerTick();
            }

            cache.clear();
        }

        // Final setup and result collection
        ResultsForAgents agentsResults = new ResultsForAgents(agentsInThread);
        Results results = new Results();
        results.setAgentNames(agentsInThread);
        results.setAgentResults(agentsResults);

        return results;
    }
}
