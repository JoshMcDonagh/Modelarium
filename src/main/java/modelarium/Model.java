package modelarium;

import modelarium.clock.SimulationClock;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.exceptions.ModelRunException;
import modelarium.multithreading.CoordinatorHandle;
import modelarium.multithreading.CoordinatorThread;
import modelarium.multithreading.WorkerThread;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.results.immutable.ImmutableResults;
import modelarium.results.mutable.MutableResults;
import modelarium.results.mutable.MutableResultsForAgents;
import modelarium.results.mutable.MutableResultsForEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Main class for executing an agent-based model using multithreaded execution.
 *
 * <p>This class is responsible for configuring the environment, distributing agents across worker threads,
 * running the simulation (synchronously or asynchronously), and collecting results.
 */
public class Model {

    /** Configuration settings for this model run */
    private final Config config;

    private MutableResults results = null;

    /**
     * Constructs a new model instance with the specified settings.
     *
     * @param config the settings to use for model initialisation and execution
     */
    public Model(Config config) {
        this.config = config;
    }

    private List<MutableAgentSet> generateAgentsForEachCoreAsList() {
        List<MutableAgentSet> agentsForEachCore = config.agentGenerator().getAgentsForEachCore(config);

        for (MutableAgentSet agentSet : agentsForEachCore)
            agentSet.setLogDatabaseFactory(config.runLogDatabaseFactory());

        return agentsForEachCore;
    }

    private Environment generateEnvironment() {
        Environment environment = config.environmentGenerator().generateEnvironment(config);
        environment.setLogDatabaseFactory(config.runLogDatabaseFactory());
        return environment;
    }

    private void setupResultsContainer(List<MutableAgentSet> agentsForEachCore) {
        results.setAgentNames(agentsForEachCore);
        results.setAgentResults(new MutableResultsForAgents(new MutableAgentSet()));
    }

    private SimulationClock makeClockIfSynced() {
        if (config.areThreadsSynced())
            return new SimulationClock(config.tickCount());

        return null;
    }

    private void createAndSetEnvironmentContext(
            Environment environment,
            RequestResponseController requestResponseController,
            SimulationClock sharedClock
    ) {
        SimulationClock clock;

        clock = Objects.requireNonNullElseGet(sharedClock, () -> new SimulationClock(config.tickCount()));

        environment.createContext(
                new MutableAgentSet(),
                config,
                new ContextCache(),
                clock,
                new RequestResponseInterface(environment.name(), config, requestResponseController),
                null
        );
    }

    private CoordinatorHandle launchCoordinator(
            Environment environment,
            RequestResponseController requestResponseController,
            SimulationClock sharedClock
    ) {
        CoordinatorThread coordinator = new CoordinatorThread(
                String.valueOf(config.threadCount()),
                config,
                environment,
                requestResponseController,
                sharedClock
        );

        Thread coordinatorThread = new Thread(coordinator);
        coordinatorThread.start();

        return new CoordinatorHandle(coordinatorThread, coordinator);
    }

    private void launchWorkers(
            List<MutableAgentSet> agentsForEachCore,
            Environment environment,
            RequestResponseController requestResponseController,
            SimulationClock sharedClock
    ) {
        ExecutorService executorService = Executors.newFixedThreadPool(config.threadCount());
        List<Future<MutableResults>> futures = new ArrayList<>();

        // Launch worker threads
        for (int threadIndex = 0; threadIndex < config.threadCount(); threadIndex++) {
            // Create an agent set for the current core
            MutableAgentSet threadAgentSet = new MutableAgentSet();
            MutableAgentSet perThreadAgentSet = agentsForEachCore.get(threadIndex);

            // Make sure agent set is not null
            if (perThreadAgentSet == null)
                perThreadAgentSet = new MutableAgentSet();

            // Add the pre-assigned agent set for this core
            threadAgentSet.add(perThreadAgentSet);

            // Create and submit the worker task
            Callable<MutableResults> worker = new WorkerThread(
                    String.valueOf(threadIndex),
                    config,
                    requestResponseController,
                    environment,
                    threadAgentSet,
                    sharedClock
            );

            futures.add(executorService.submit(worker));
        }

        // Collect results from each worker thread
        try {
            for (Future<MutableResults> future : futures) {
                try {
                    MutableResults resultsForThread = future.get();
                    results.mergeWith(resultsForThread);
                } catch (ExecutionException e) {
                    // A worker threw. Cancel the rest and propagate.
                    futures.forEach(f -> f.cancel(true));
                    throw new ModelRunException("Worker thread failed during simulation", e.getCause());
                } catch (InterruptedException e) {
                    futures.forEach(f -> f.cancel(true));
                    Thread.currentThread().interrupt();
                    throw new ModelRunException("Interrupted while waiting for worker results", e);
                }
            }
        } finally {
            executorService.shutdown();
        }
    }

    private void stopCoordinator(CoordinatorHandle coordinatorHandle) {
        coordinatorHandle.coordinator().shutdown();
        try {
            coordinatorHandle.coordinatorThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void run() {
        results = new MutableResults();

        List<MutableAgentSet> agentsForEachCore = generateAgentsForEachCoreAsList();
        Environment environment = generateEnvironment();

        setupResultsContainer(agentsForEachCore);

        SimulationClock sharedClock = makeClockIfSynced();

        RequestResponseController requestResponseController = new RequestResponseController(config);
        createAndSetEnvironmentContext(environment, requestResponseController, sharedClock);

        CoordinatorHandle coordinatorHandle = null;
        if (config.areThreadsSynced())
            coordinatorHandle = launchCoordinator(environment, requestResponseController, sharedClock);

        try {
            launchWorkers(agentsForEachCore, environment, requestResponseController, sharedClock);
        } finally {
            if (coordinatorHandle != null)
                stopCoordinator(coordinatorHandle);
        }

        results.setEnvironmentResults(new MutableResultsForEnvironment(environment));
    }

    public ImmutableResults getResults() {
        if (results == null)
            throw new IllegalStateException("Results cannot be accessed before a model run has been completed");

        return results.getAsImmutable();
    }
}
