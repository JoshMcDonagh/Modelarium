package modelarium;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.multithreading.CoordinatorThread;
import modelarium.multithreading.WorkerThread;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.results.Results;
import modelarium.results.ResultsForAgents;
import modelarium.results.ResultsForEnvironment;

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
    private final Config config;

    private final Results results = new Results();

    /**
     * Constructs a new model instance with the specified settings.
     *
     * @param config the settings to use for model initialisation and execution
     */
    public Model(Config config) {
        this.config = config;
    }

    /**
     * Runs the agent-based model according to the configured settings.
     *
     * @throws NoSuchMethodException if the results class has no default constructor
     * @throws InvocationTargetException if constructor invocation fails
     * @throws InstantiationException if instantiating the results class fails
     * @throws IllegalAccessException if the constructor is not accessible
     */
    public void run() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        // Distribute agents among cores
        List<AgentSet> agentsForEachCore = config.agentGenerator().getAgentsForEachCore(config);

        for (AgentSet agentSet : agentsForEachCore) {
            for (Agent agent : agentSet) {
                int attributeSetCount = agent.attributeSetCount();
                for (int i = 0; i < attributeSetCount; i++)
                    agent.getAttributeSet(i).setLogDatabase(config.runLogDatabaseFactory());
            }
        }

        // Generate the simulation environment
        Environment environment = config.environmentGenerator().generateEnvironment(config);

        int attributeSetCount = environment.attributeSetCount();
        for (int i = 0; i < attributeSetCount; i++)
            environment.getAttributeSet(i).setLogDatabase(config.runLogDatabaseFactory());

        // Instantiate results container
        results.setAgentNames(agentsForEachCore);
        results.setAgentResults(new ResultsForAgents(new AgentSet()));

        // Set up multithreaded execution
        ExecutorService executorService = Executors.newFixedThreadPool(config.threadCount());
        List<Future<Results>> futures = new ArrayList<>();

        // Shared controller for inter-thread communication
        RequestResponseController requestResponseController = new RequestResponseController(config);

        Thread coordinatorThread = null;
        CoordinatorThread coordinator = null;

        // Set up accessor for the environment model element
        EnvironmentContext environmentContext = new EnvironmentContext(
                environment,
                new AgentSet(),
                config,
                new ContextCache(),
                new Clock(config.tickCount()),
                new RequestResponseInterface(environment.name(), config, requestResponseController)
        );

        environment.setContext(environmentContext);

        // Launch central coordinator if synchronisation is required
        if (config.areThreadsSynced()) {
            coordinator = new CoordinatorThread(
                    String.valueOf(config.threadCount()),
                    config,
                    environment,
                    environmentContext,
                    requestResponseController
            );
            coordinatorThread = new Thread(coordinator);
            coordinatorThread.start();
        }

        // Launch worker threads
        for (int threadIndex = 0; threadIndex < config.threadCount(); threadIndex++) {
            ContextCache cache = new ContextCache();
            Clock clock = new Clock(config.tickCount());

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
                    threadAgentSet
            );

            futures.add(executorService.submit(worker));
        }

        // Collect results from each worker thread
        try {
            for (Future<Results> future : futures) {
                Results resultsForThread = future.get();
                results.mergeWith(resultsForThread);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        // Gracefully stop the coordinator thread if it was used
        if (config.areThreadsSynced()) {
            coordinator.shutdown();
            try {
                coordinatorThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Post-processing of results
        results.setEnvironmentResults(new ResultsForEnvironment(environment));
        results.seal(); // Finalise results
    }

    public Results getResults() {
        return results;
    }
}
