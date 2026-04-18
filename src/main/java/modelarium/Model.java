package modelarium;

import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.exceptions.ModelRunException;
import modelarium.multithreading.CoordinatorHandle;
import modelarium.multithreading.CoordinatorThread;
import modelarium.multithreading.WorkerThread;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.results.Results;
import modelarium.results.ResultsForAgents;
import modelarium.results.ResultsForEnvironment;
import modelarium.results.immutable.ImmutableResults;

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

    private Results results = null;

    /**
     * Constructs a new model instance with the specified settings.
     *
     * @param config the settings to use for model initialisation and execution
     */
    public Model(Config config) {
        this.config = config;
    }

    private List<AgentSet> generateAgentsForEachCoreAsList() {
        List<AgentSet> agentsForEachCore = config.agentGenerator().getAgentsForEachCore(config);

        for (AgentSet agentSet : agentsForEachCore)
            agentSet.setLogDatabaseFactory(config.runLogDatabaseFactory());

        return agentsForEachCore;
    }

    private Environment generateEnvironment() {
        Environment environment = config.environmentGenerator().generateEnvironment(config);
        environment.setLogDatabaseFactory(config.runLogDatabaseFactory());
        return environment;
    }

    private void setupResultsContainer(List<AgentSet> agentsForEachCore) {
        results.setAgentNames(agentsForEachCore);
        results.setAgentResults(new ResultsForAgents(new AgentSet()));
    }

    private Clock makeClockIfSynced() {
        if (config.areThreadsSynced())
            return new Clock(config.tickCount());

        return null;
    }

    private void createAndSetEnvironmentContext(
            Environment environment,
            RequestResponseController requestResponseController,
            Clock sharedClock
    ) {
        Clock clock;

        clock = Objects.requireNonNullElseGet(sharedClock, () -> new Clock(config.tickCount()));

        environment.createContext(
                new AgentSet(),
                config,
                new ContextCache(),
                clock,
                new RequestResponseInterface(environment.name(), config, requestResponseController),
                null
        );
    }

    private CoordinatorHandle launchCoordinator(
            Environment environment,
            RequestResponseController requestResponseController
    ) {
        CoordinatorThread coordinator = new CoordinatorThread(
                String.valueOf(config.threadCount()),
                config,
                environment,
                requestResponseController
        );

        Thread coordinatorThread = new Thread(coordinator);
        coordinatorThread.start();

        return new CoordinatorHandle(coordinatorThread, coordinator);
    }

    private void launchWorkers(
            List<AgentSet> agentsForEachCore,
            Environment environment,
            RequestResponseController requestResponseController,
            Clock sharedClock
    ) {
        ExecutorService executorService = Executors.newFixedThreadPool(config.threadCount());
        List<Future<Results>> futures = new ArrayList<>();

        // Launch worker threads
        for (int threadIndex = 0; threadIndex < config.threadCount(); threadIndex++) {
            // Create an agent set for the current core
            AgentSet threadAgentSet = new AgentSet(true);
            AgentSet perThreadAgentSet = agentsForEachCore.get(threadIndex);

            // Make sure agent set is not null
            if (perThreadAgentSet == null)
                perThreadAgentSet = new AgentSet(true);

            // Add the pre-assigned agent set for this core
            threadAgentSet.add(perThreadAgentSet);

            // Create and submit the worker task
            Callable<Results> worker = new WorkerThread<>(
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
            for (Future<Results> future : futures) {
                try {
                    Results resultsForThread = future.get();
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
        results = new Results();

        List<AgentSet> agentsForEachCore = generateAgentsForEachCoreAsList();
        Environment environment = generateEnvironment();

        setupResultsContainer(agentsForEachCore);

        Clock sharedClock = makeClockIfSynced();

        RequestResponseController requestResponseController = new RequestResponseController(config);
        createAndSetEnvironmentContext(environment, requestResponseController, sharedClock);

        CoordinatorHandle coordinatorHandle = null;
        if (config.areThreadsSynced())
            coordinatorHandle = launchCoordinator(environment, requestResponseController);

        try {
            launchWorkers(agentsForEachCore, environment, requestResponseController, sharedClock);
        } finally {
            if (coordinatorHandle != null)
                stopCoordinator(coordinatorHandle);
        }

        if (config.areThreadsSynced())
            stopCoordinator(coordinatorHandle);

        results.setEnvironmentResults(new ResultsForEnvironment(environment));
    }

    public ImmutableResults getResults() {
        if (results == null)
            throw new IllegalStateException("Results cannot be accessed before a model run has been completed");

        return results.getAsImmutable();
    }
}
