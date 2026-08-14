package modelarium;

import modelarium.clock.MutableClock;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.exceptions.ModelRunException;
import modelarium.multithreading.CoordinatorHandle;
import modelarium.multithreading.CoordinatorThread;
import modelarium.multithreading.WorkerThread;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.results.immutable.ImmutableResults;
import modelarium.results.mutable.MutableResults;
import modelarium.results.mutable.MutableResultsForAgents;
import modelarium.results.mutable.MutableResultsForEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.concurrent.*;
import java.util.random.RandomGenerator;

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

    /**
     * Generates sets of agents each thread will use and provides each set with the log's database factory.
     *
     * @return a list of {@link MutableAgentSet} objects, one per core
     */
    private List<MutableAgentSet> generateAgentsForEachCoreAsList(RandomGenerator randomGenerator) {
        List<MutableAgentSet> agentsForEachCore = config.agentGenerator().getAgentsForEachCore(config, randomGenerator);

        for (MutableAgentSet agentSet : agentsForEachCore)
            agentSet.setLogDatabaseFactory(config.runLogDatabaseFactory());

        return agentsForEachCore;
    }

    /**
     * Generates the {@link MutableEnvironment} that the model will use.
     *
     * @return a new {@link MutableEnvironment} instance
     */
    private MutableEnvironment generateEnvironment(RandomGenerator randomGenerator) {
        MutableEnvironment environment = config.environmentGenerator().generateEnvironment(config, randomGenerator);
        environment.setLogDatabaseFactory(config.runLogDatabaseFactory());
        return environment;
    }

    /**
     * Provides the model's results container with all the agents in the model.
     *
     * @param agentsForEachCore the list of agent sets for each core
     */
    private void setupResultsContainer(List<MutableAgentSet> agentsForEachCore) {
        results.setAgentNames(agentsForEachCore);
        results.setAgentResults(new MutableResultsForAgents(new MutableAgentSet()));
    }

    /**
     * If the model uses synchronised threads, create a mutable clock to maintain synchronisation.
     *
     * @return a new {@link MutableClock} instance
     */
    private MutableClock makeClockIfSynced() {
        if (config.areThreadsSynced())
            return new MutableClock(config.tickCount());

        return null;
    }

    /**
     * Creates a context for the environment to make use of in behaviour and interactions.
     *
     * @param environment the environment to create and set the context to
     * @param requestResponseController the request/response controller the context will need for inter-entity
     *                                  interaction
     * @param sharedClock the clock used to synchronise entities in the model
     * @param randomGenerator the splittable random generator the environment can use
     */
    private void createAndSetEnvironmentContext(
            MutableEnvironment environment,
            RequestResponseController requestResponseController,
            MutableClock sharedClock,
            SplittableRandom randomGenerator
    ) {
        MutableClock clock;

        clock = Objects.requireNonNullElseGet(sharedClock, () -> new MutableClock(config.tickCount()));

        environment.createContext(
                new MutableAgentSet(),
                config,
                new ContextCache(),
                clock,
                requestResponseController,
                null,
                randomGenerator.split()
        );
    }

    /**
     * Creates and starts the model's co-ordinator thread using {@link CoordinatorThread}.
     *
     * @param environment the environment instance the model will use
     * @param requestResponseController the request/response controller the co-ordinator will use to handle requests and
     *                                  responses to/from the worker cores
     * @param sharedClock the clock used to synchronise the entities and cores in the model
     * @return a new {@link CoordinatorHandle} instance for the coordinator thread
     */
    private CoordinatorHandle launchCoordinator(
            MutableEnvironment environment,
            RequestResponseController requestResponseController,
            MutableClock sharedClock
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

    /**
     * Creates and starts the model's worker threads using {@link WorkerThread}.
     *
     * @param agentsForEachCore the agents for each worker core given as a list of agent sets
     * @param environment the model's environment
     * @param requestResponseController the request/response controller the workers will use to handle requests and
     *                                  responses to/from the co-ordinator core
     * @param sharedClock the clock used to synchronise the entities and cores in the model
     * @param randomGenerator the splittable random generator agents can use
     */
    private void launchWorkers(
            List<MutableAgentSet> agentsForEachCore,
            MutableEnvironment environment,
            RequestResponseController requestResponseController,
            MutableClock sharedClock,
            SplittableRandom randomGenerator
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
                    sharedClock,
                    randomGenerator.split()
            );

            futures.add(executorService.submit(worker));
        }

        // Collect results from each worker thread
        try {
            for (Future<MutableResults> future : futures) {
                try {
                    MutableResults resultsForThread = future.get();
                    results.mergeAgentsWith(resultsForThread);
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

    /**
     * Stops the co-ordinator thread using the given {@link CoordinatorHandle} instance.
     *
     * @param coordinatorHandle the coordinator handle instance containing the co-ordinator thread instance and the
     *                          thread itself
     */
    private void stopCoordinator(CoordinatorHandle coordinatorHandle) {
        coordinatorHandle.coordinator().shutdown();
        try {
            coordinatorHandle.coordinatorThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Runs the model using the configurations given during construction.
     */
    public void run() {
        // Create a splittable random generator using a seed given by the model's config that entities in the model can
        // use
        SplittableRandom randomGenerator = new SplittableRandom(config.seed());

        // Create new results container to store model results
        results = new MutableResults();

        // Generate entities
        List<MutableAgentSet> agentsForEachCore = generateAgentsForEachCoreAsList(randomGenerator);
        MutableEnvironment environment = generateEnvironment(randomGenerator);

        // Updates the results container with the agents in the model
        setupResultsContainer(agentsForEachCore);

        // If the model threads are synchronised, make a clock
        MutableClock sharedClock = makeClockIfSynced();

        // Create a request/response controller worker threads can use to make requests/response to/from the coordinator
        // a vice versa
        RequestResponseController requestResponseController = new RequestResponseController(config);

        // Create a context the environment can use and set it to the environment
        createAndSetEnvironmentContext(environment, requestResponseController, sharedClock, randomGenerator);

        // If threads are synchronised, create the coordinator thread
        CoordinatorHandle coordinatorHandle = null;
        if (config.areThreadsSynced())
            coordinatorHandle = launchCoordinator(environment, requestResponseController, sharedClock);

        try {
            // Create the worker threads
            launchWorkers(agentsForEachCore, environment, requestResponseController, sharedClock, randomGenerator);
        } finally {
            // If the threads are synchronised, stop the coordinator thread
            if (coordinatorHandle != null)
                stopCoordinator(coordinatorHandle);
        }

        // Provide the results container with the environment's results
        results.setEnvironmentResults(new MutableResultsForEnvironment(environment));
    }

    /**
     * Returns the results of the most recent model run.
     *
     * @return a new {@link ImmutableResults} instance
     */
    public ImmutableResults getResults() {
        if (results == null)
            throw new IllegalStateException("Results cannot be accessed before a model run has been completed");

        return results.getAsImmutable();
    }
}
