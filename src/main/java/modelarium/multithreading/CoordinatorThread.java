package modelarium.multithreading;

import modelarium.Clock;
import modelarium.contexts.Context;
import modelarium.Config;
import modelarium.agents.sets.AgentSet;
import modelarium.environments.Environment;
import modelarium.multithreading.requestresponse.*;
import modelarium.multithreading.utils.WorkerCache;

/**
 * Coordinator thread responsible for managing synchronised access to shared simulation state
 * between multiple worker threads in a parallel simulation.
 *
 * <p>This class listens to the request queue and uses {@link CoordinatorRequestHandler}
 * to respond to agent/environment-related queries or updates.
 */
public class CoordinatorThread implements Runnable {

    /** A label or ID for this thread (used for logging or debugging) */
    private final String threadName;

    /** Global simulation configuration */
    private final Config settings;

    /** The environment shared across all workers */
    private final Environment environment;

    /** Controller that manages the request and response queues for inter-thread communication */
    private final RequestResponseController requestResponseController;

    /** Global agent set of the model */
    private final AgentSet predefinedGlobalAgentSet;

    /** Flag to control the running state of the thread */
    private volatile boolean isRunning = true;

    private final Clock coordinatorClock;


    /**
     * Constructs the coordinator thread with required references.
     *
     * @param name the thread name or ID
     * @param settings global model settings
     * @param environment the shared simulation environment
     * @param requestResponseController the controller managing request/response queues
     */
    public CoordinatorThread(String name,
                             Config settings,
                             Environment environment,
                             RequestResponseController requestResponseController) {
        this(name, settings, environment, requestResponseController, null);
    }

    /**
     * Constructs the coordinator thread with required references.
     *
     * @param name the thread name or ID
     * @param settings global model settings
     * @param environment the shared simulation environment
     * @param requestResponseController the controller managing request/response queues
     * @param globalAgentSet the global agent set for the whole model
     */
    public CoordinatorThread(String name,
                             Config settings,
                             Environment environment,
                             RequestResponseController requestResponseController,
                             AgentSet globalAgentSet) {
        this.threadName = name;
        this.settings = settings;
        this.environment = environment;
        this.requestResponseController = requestResponseController;
        this.predefinedGlobalAgentSet = globalAgentSet;

        if (this.environment.getModelElementAccessor() == null) {
            this.environment.setModelElementAccessor(
                    new Context(
                            this.environment,                        // modelElement
                            new AgentSet(),                     // empty global set for env
                            settings,                           // settings
                            new WorkerCache(settings.getDoAgentStoresHoldAgentCopies()),
                            new RequestResponseInterface(environment.name(), settings, requestResponseController),
                            environment                         // localEnvironment
                    )
            );
        }

        Context context = this.environment.getModelElementAccessor();
        if (context == null) {
            context = new Context(
                    this.environment,
                    new AgentSet(),
                    settings,
                    new WorkerCache(settings.getDoAgentStoresHoldAgentCopies()),
                    new RequestResponseInterface(environment.name(), settings, requestResponseController),
                    environment
            );

            try {
                this.environment.setModelElementAccessor(context);
            } catch (Throwable ignore) {}
        }

        this.coordinatorClock = new Clock(settings.getNumOfTicksToRun(), settings.getNumOfWarmUpTicks());
        context.setClock(this.coordinatorClock);
    }

    /**
     * Signals the coordinator thread to stop processing and terminate.
     */
    public void shutdown() {
        isRunning = false;
        requestResponseController.getRequestQueue().offer(new Request("SYSTEM", threadName, RequestType.SHUTDOWN, null));
    }

    /**
     * Main execution loop for the coordinator thread.
     *
     * <p>Continuously listens for requests from worker threads and processes them.
     */
    @Override
    public void run() {
        AgentSet globalAgentSet;

        if (predefinedGlobalAgentSet == null)
            globalAgentSet = new AgentSet();
        else
            globalAgentSet = predefinedGlobalAgentSet;

        // Initialise the request handler with access to the global agent state and environment
        CoordinatorRequestHandler.initialise(
                threadName,
                settings,
                requestResponseController.getResponseQueue(),
                globalAgentSet,
                environment
        );

        // Continuously poll for and handle incoming requests from workers
        while (isRunning || !requestResponseController.getRequestQueue().isEmpty()) {
            try {
                Request request = requestResponseController.getRequestQueue().take(); // blocks
                if (request.getRequestType() == RequestType.SHUTDOWN) {
                    isRunning = false;
                    continue;
                }
                CoordinatorRequestHandler.handleCoordinatorRequest(request);
            } catch (InterruptedException e) {
                if (!isRunning)
                    break;
                Thread.currentThread().interrupt();
            }
        }
    }
}
