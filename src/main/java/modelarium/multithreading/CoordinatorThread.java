package modelarium.multithreading;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.*;

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

    /** Controller that manages the request and response queues for inter-thread communication */
    private final RequestResponseController requestResponseController;

    private final Clock sharedClock;

    /** Global agent set of the model */
    private final MutableAgentSet predefinedGlobalAgentSet;

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
                             RequestResponseController requestResponseController,
                             Clock sharedClock
    ) {
        this(name, config, environment, requestResponseController, sharedClock, null);
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
                             RequestResponseController requestResponseController,
                             Clock sharedClock,
                             MutableAgentSet globalAgentSet
    ) {
        this.threadName = name;
        this.config = config;
        this.environment = environment;
        this.requestResponseController = requestResponseController;
        this.sharedClock = sharedClock;
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
        MutableAgentSet globalAgentSet;

        globalAgentSet = Objects.requireNonNullElseGet(predefinedGlobalAgentSet, MutableAgentSet::new);

        requestHandlerMap.put(RequestType.ALL_WORKERS_FINISH_TICK,
                new CoordinatorRequestHandler.AllWorkersFinishTick(threadName, config, requestResponseController, globalAgentSet, environment, sharedClock));
        requestHandlerMap.put(RequestType.ALL_WORKERS_UPDATE_COORDINATOR,
                new CoordinatorRequestHandler.AllWorkersUpdateCoordinator(threadName, config, requestResponseController, globalAgentSet, environment, sharedClock));
        requestHandlerMap.put(RequestType.AGENT_ACCESS,
                new CoordinatorRequestHandler.AgentAccess(threadName, config, requestResponseController, globalAgentSet, environment, sharedClock));
        requestHandlerMap.put(RequestType.UPDATE_COORDINATOR_AGENTS,
                new CoordinatorRequestHandler.UpdateCoordinatorAgents(threadName, config, requestResponseController, globalAgentSet, environment, sharedClock));
        requestHandlerMap.put(RequestType.FILTERED_AGENTS_ACCESS,
                new CoordinatorRequestHandler.FilteredAgentsAccess(threadName, config, requestResponseController, globalAgentSet, environment, sharedClock));
        requestHandlerMap.put(RequestType.ENVIRONMENT_ATTRIBUTES_ACCESS,
                new CoordinatorRequestHandler.EnvironmentAttributesAccess(threadName, config, requestResponseController, globalAgentSet, environment, sharedClock));
    }

    private void notifyRequesterOfError(Request request, Throwable cause) {
        String requester = request.getRequester();
        if (requester == null || "SYSTEM".equals(requester))
            return;
        try {
            requestResponseController.getResponseQueue(requester)
                    .put(new Response(threadName, requester, ResponseType.ERROR, cause));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
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
            Request request;
            try {
                request = requestResponseController.getRequestQueue().take();
            } catch (InterruptedException e) {
                // Interrupted while waiting for a request - honour shutdown intent
                if (isRunning)
                    Thread.currentThread().interrupt();
                break;
            }

            if (request.getRequestType() == RequestType.SHUTDOWN) {
                isRunning = false;
                continue;
            }

            CoordinatorRequestHandler handler = requestHandlerMap.get(request.getRequestType());
            if (handler == null) {
                notifyRequesterOfError(request, new IllegalStateException("No handler registered for request type: "
                        + request.getRequestType()));
                continue;
            }

            try {
                handler.handleRequest(request);
            } catch (InterruptedException e) {
                // Handler was interrupted - coordinator is shutting down
                Thread.currentThread().interrupt();
                notifyRequesterOfError(request, e);
                break;
            } catch (Throwable t) {
                // Handler failed - notify the requester so it doesn't block forever
                notifyRequesterOfError(request, t);
            }
        }
    }
}
