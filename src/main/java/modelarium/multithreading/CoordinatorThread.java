package modelarium.multithreading;

import modelarium.Config;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.CoordinatorRequestHandler;
import modelarium.multithreading.requestresponse.Request;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private final Config config;

    /** The environment shared across all workers */
    private final Environment environment;

    private final EnvironmentContext environmentContext;

    /** Controller that manages the request and response queues for inter-thread communication */
    private final RequestResponseController requestResponseController;

    /** Global agent set of the model */
    private final AgentSet predefinedGlobalAgentSet;

    private final Map<RequestType, CoordinatorRequestHandler> requestHandlerMap = new HashMap<>();

    /** Flag to control the running state of the thread */
    private volatile boolean isRunning = true;


    /**
     * Constructs the coordinator thread with required references.
     *
     * @param name the thread name or ID
     * @param config global model settings
     * @param environment the shared simulation environment
     * @param requestResponseController the controller managing request/response queues
     */
    public CoordinatorThread(String name,
                             Config config,
                             Environment environment,
                             EnvironmentContext environmentContext,
                             RequestResponseController requestResponseController) {
        this(name, config, environment, environmentContext, requestResponseController, null);
    }

    /**
     * Constructs the coordinator thread with required references.
     *
     * @param name the thread name or ID
     * @param config global model settings
     * @param environment the shared simulation environment
     * @param requestResponseController the controller managing request/response queues
     * @param globalAgentSet the global agent set for the whole model
     */
    public CoordinatorThread(String name,
                             Config config,
                             Environment environment,
                             EnvironmentContext environmentContext,
                             RequestResponseController requestResponseController,
                             AgentSet globalAgentSet) {
        this.threadName = name;
        this.config = config;
        this.environment = environment;
        this.environmentContext = environmentContext;
        this.requestResponseController = requestResponseController;
        this.predefinedGlobalAgentSet = globalAgentSet;
    }

    /**
     * Signals the coordinator thread to stop processing and terminate.
     */
    public void shutdown() {
        isRunning = false;
        requestResponseController.getRequestQueue().offer(new Request("SYSTEM", threadName, RequestType.SHUTDOWN, null));
    }

    private void initialiseHandlers() {
        AgentSet globalAgentSet;

        globalAgentSet = Objects.requireNonNullElseGet(predefinedGlobalAgentSet, AgentSet::new);

        requestHandlerMap.put(RequestType.ALL_WORKERS_FINISH_TICK,
                new CoordinatorRequestHandler.AllWorkersFinishTick(threadName, config, requestResponseController.getResponseQueue(threadName), globalAgentSet, environment, environmentContext));
        requestHandlerMap.put(RequestType.ALL_WORKERS_UPDATE_COORDINATOR,
                new CoordinatorRequestHandler.AllWorkersUpdateCoordinator(threadName, config, requestResponseController.getResponseQueue(threadName), globalAgentSet, environment, environmentContext));
        requestHandlerMap.put(RequestType.AGENT_ACCESS,
                new CoordinatorRequestHandler.AgentAccess(threadName, config, requestResponseController.getResponseQueue(threadName), globalAgentSet, environment, environmentContext));
        requestHandlerMap.put(RequestType.UPDATE_COORDINATOR_AGENTS,
                new CoordinatorRequestHandler.UpdateCoordinatorAgents(threadName, config, requestResponseController.getResponseQueue(threadName), globalAgentSet, environment, environmentContext));
        requestHandlerMap.put(RequestType.FILTERED_AGENTS_ACCESS,
                new CoordinatorRequestHandler.FilteredAgentsAccess(threadName, config, requestResponseController.getResponseQueue(threadName), globalAgentSet, environment, environmentContext));
        requestHandlerMap.put(RequestType.ENVIRONMENT_ATTRIBUTES_ACCESS,
                new CoordinatorRequestHandler.EnvironmentAttributesAccess(threadName, config, requestResponseController.getResponseQueue(threadName), globalAgentSet, environment, environmentContext));
    }

    /**
     * Main execution loop for the coordinator thread.
     *
     * <p>Continuously listens for requests from worker threads and processes them.
     */
    @Override
    public void run() {
        initialiseHandlers();

        // Continuously poll for and handle incoming requests from workers
        while (isRunning || !requestResponseController.getRequestQueue().isEmpty()) {
            try {
                Request request = requestResponseController.getRequestQueue().take(); // blocks
                if (request.getRequestType() == RequestType.SHUTDOWN) {
                    isRunning = false;
                    continue;
                }
                requestHandlerMap.get(request.getRequestType()).handleRequest(request);
            } catch (InterruptedException e) {
                if (!isRunning)
                    break;
                Thread.currentThread().interrupt();
            }
        }
    }
}
