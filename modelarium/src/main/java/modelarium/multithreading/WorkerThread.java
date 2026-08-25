package modelarium.multithreading;

import modelarium.Config;
import modelarium.clock.ReadOnlyClock;
import modelarium.clock.Clock;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.Environment;
import modelarium.entities.readonly.ReadOnlyEnvironment;
import modelarium.exceptions.AgentNotFoundException;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.results.Results;
import modelarium.results.ResultsForAgents;
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
    private final Clock sharedClock;

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
                        Clock sharedClock,
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
        Clock clock = Objects.requireNonNullElseGet(sharedClock, () -> new Clock(config.tickCount()));
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

        ReadOnlyClock immutableClock = new ReadOnlyClock(clock);
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

            // Collect every population mutation requested during this tick.
            AgentSet agentsToAdd = new AgentSet();
            List<String> agentsToKill = new ArrayList<>();

            for (Agent agent : agentsInThread) {
                agentsToAdd.add(agent.getAddedAgents());
                agentsToKill.addAll(agent.getKilledAgentNames());

                agent.clearPendingAgentChanges();
            }

            // Apply additions to the actual worker state.
            agentsInThread.update(agentsToAdd, false);

            // Newly installed agents need contexts before they can run on the next tick.
            for (Agent addedAgent : agentsToAdd) {
                Agent installedAgent = agentsInThread.get(addedAgent.name());

                installedAgent.createContext(
                        visibleAgents,
                        config,
                        cache,
                        clock,
                        requestResponseController,
                        localEnvironment,
                        randomGenerator
                );
            }

            // Local kills can be committed to this worker now.
            List<String> remoteAgentsToKill = new ArrayList<>();

            for (String agentName : agentsToKill) {
                if (agentsInThread.doesAgentExist(agentName)) {
                    agentsInThread.get(agentName).kill();
                } else if (config.areThreadsSynced()) {
                    remoteAgentsToKill.add(agentName);
                } else {
                    // This should normally have been caught by
                    // SimulationContext.killAgent().
                    throw new AgentNotFoundException(
                            "Agent '" + agentName
                                    + "' could not be found in worker '"
                                    + threadName + "'"
                    );
                }
            }

            if (config.areThreadsSynced()) {
                // Important: do not mutate the coordinator until EVERY worker has finished reading the old global
                // state.
                requestResponseInterface.waitUntilAllWorkersFinishTick();

                // Now we are safely in the coordinator-update phase.
                if (!remoteAgentsToKill.isEmpty())
                    requestResponseInterface.killCoordinatorAgents(
                            remoteAgentsToKill
                    );

                requestResponseInterface.updateCoordinatorAgents(
                        agentsInThread
                );

                // This barrier is only released after all worker updates have been processed and the environment has run.
                requestResponseInterface
                        .waitUntilAllWorkersUpdateCoordinator();

                // All tick-specific cached reads are now stale.
                cache.clear();

                // Retrieve the FINAL global state for the completed tick.
                ReadOnlyAgentSet resolvedGlobalAgents =
                        requestResponseInterface
                                .getGlobalAgentSetFromCoordinator(
                                        threadName
                                );

                // Propagate coordinator-side deaths back to the worker that actually owns each agent.
                for (Agent localAgent : agentsInThread) {
                    if (
                            resolvedGlobalAgents.doesAgentExist(
                                    localAgent.name()
                            )
                                    && resolvedGlobalAgents
                                    .get(localAgent.name())
                                    .isDead()
                                    && !localAgent.isDead()
                    ) {
                        localAgent.kill();
                    }
                }

                // This global set is already the state that should be visible during the NEXT tick, so seed the cache with it.
                cache.addGlobalAgentSet(resolvedGlobalAgents);
            } else {
                clock.triggerTick();

                // Previous-tick cached reads are no longer valid.
                cache.clear();
            }

            // Finally refresh the worker-local snapshot.
            visibleAgents.update(agentsInThread, true);
        }

        // Final setup and result collection
        ResultsForAgents agentsResults = new ResultsForAgents(agentsInThread);
        Results results = new Results();
        results.setAgentNames(agentsInThread);
        results.setAgentResults(agentsResults);

        return results;
    }
}
