package modelarium;

import modelarium.agents.Agent;
import modelarium.agents.AgentSet;
import modelarium.attributes.results.databases.AttributeSetRunLogDatabaseFactory;
import modelarium.environments.Environment;
import modelarium.multithreading.CoordinatorThread;
import modelarium.multithreading.WorkerThread;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.multithreading.utils.WorkerCache;
import modelarium.results.AgentResults;
import modelarium.results.EnvironmentResults;
import modelarium.results.Results;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Main class for executing an agent-based model using multithreaded execution.
 *
 * <p>This class is responsible for configuring the environment, distributing agents across worker threads,
 * running the simulation (synchronously or asynchronously), and collecting results.
 */
public class Model {

    /** Configuration settings for this model run */
    private final ModelSettings settings;

    /**
     * Constructs a new model instance with the specified settings.
     *
     * @param settings the settings to use for model initialisation and execution
     */
    public Model(ModelSettings settings) {
        this.settings = settings;
    }

    /**
     * Runs the agent-based model according to the configured settings.
     *
     * @return a {@link Results} object containing accumulated simulation data
     * @throws NoSuchMethodException if the results class has no default constructor
     * @throws InvocationTargetException if constructor invocation fails
     * @throws InstantiationException if instantiating the results class fails
     * @throws IllegalAccessException if the constructor is not accessible
     */
    public Results run() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        if (settings.getAreAttributeSetResultsStoredOnDisk())
            AttributeSetRunLogDatabaseFactory.setDatabaseToDiskBased();
        else
            AttributeSetRunLogDatabaseFactory.setDatabaseToMemoryBased();

        // Distribute agents among cores
        List<AgentSet> agentsForEachCore = settings.getAgentGenerator().getAgentsForEachCore(settings);

        // Generate the simulation environment
        Environment environment = settings.getEnvironmentGenerator().generateEnvironment(settings);

        // Instantiate results container
        Results results = settings.getResults();
        results.setAgentNames(agentsForEachCore);
        results.setAgentResults(new AgentResults(new AgentSet()));

        // Prepare the environment
        environment.setup();

        // Set up multithreaded execution
        ExecutorService executorService = Executors.newFixedThreadPool(settings.getNumOfCores());
        List<Future<Results>> futures = new ArrayList<>();

        // Shared controller for inter-thread communication
        RequestResponseController requestResponseController = new RequestResponseController(settings);

        Thread coordinatorThread = null;
        CoordinatorThread coordinator = null;

        // Set up accessor for the environment model element
        EntityAccessor environmentEntityAccessor = new EntityAccessor(
                environment,
                new AgentSet(),
                settings,
                new WorkerCache(settings.getDoAgentStoresHoldAgentCopies()),
                new RequestResponseInterface(environment.name(), settings, requestResponseController),
                environment
        );

        environment.setModelElementAccessor(environmentEntityAccessor);

        // Launch central coordinator if synchronisation is required
        if (settings.getAreProcessesSynced()) {
            coordinator = new CoordinatorThread(
                    String.valueOf(settings.getNumOfCores()),
                    settings,
                    environment,
                    requestResponseController
            );
            coordinatorThread = new Thread(coordinator);
            coordinatorThread.start();
        }

        // Launch worker threads
        for (int coreIndex = 0; coreIndex < settings.getNumOfCores(); coreIndex++) {
            // Optional local cache for worker thread
            WorkerCache cache = null;
            if (settings.getIsCacheUsed())
                cache = new WorkerCache(settings.getDoAgentStoresHoldAgentCopies());

            // Create an agent set for the current core
            AgentSet coreAgentSet = new AgentSet(settings.getDoAgentStoresHoldAgentCopies());
            AgentSet perCore = agentsForEachCore.get(coreIndex);

            // Make sure agent set is not null
            if (perCore == null)
                perCore = new AgentSet(settings.getDoAgentStoresHoldAgentCopies());

            // Add the pre-assigned agent set for this core
            coreAgentSet.add(perCore);

            // Prepare agents for this core and assign them accessors
            for (Agent agent : coreAgentSet) {
                Environment localEnvironment = environment.clone();
                EntityAccessor agentEntityAccessor = new EntityAccessor(
                        agent,
                        coreAgentSet,
                        settings,
                        cache,
                        new RequestResponseInterface(agent.name(), settings, requestResponseController),
                        localEnvironment
                );
                agent.setModelElementAccessor(agentEntityAccessor);
            }

            // Create and submit the worker task
            Callable<Results> worker = new WorkerThread<>(
                    String.valueOf(coreIndex),
                    settings,
                    requestResponseController,
                    coreAgentSet
            );
            futures.add(executorService.submit(worker));
        }

        // Collect results from each worker thread
        try {
            for (Future<Results> future : futures) {
                Results coreResult = future.get();
                results.mergeWithBeforeAccumulation(coreResult);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Consider logging or propagating
        } finally {
            executorService.shutdown();
        }

        // Gracefully stop the coordinator thread if it was used
        if (settings.getAreProcessesSynced() && coordinatorThread != null) {
            coordinator.shutdown();
            try {
                coordinatorThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Post-processing of results
        results.setEnvironmentResults(new EnvironmentResults(environment));
        results.accumulateAgentAttributeData();
        results.processEnvironmentAttributeData();
        results.seal(); // Finalise results

        return results;
    }
}
