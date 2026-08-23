package modelarium.multithreading;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.*;
import modelarium.utils.Cloners;

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

    /** The clock shared with the workers to synchronise the passing of ticks */
    private final MutableClock sharedClock;

    /** Global agent set of the model */
    private final AgentSet predefinedGlobalAgentSet;

    /** Map indexed by agent names containing the thread names containing the corresponding agent **/
    private final Map<String, String> agentThreadMap;

    /** Maps each request type to the handler responsible for processing it */
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
     * @param sharedClock the clock used to keep a track of time passing in the model
     */
    public CoordinatorThread(String name,
                             Config config,
                             Environment environment,
                             RequestResponseController requestResponseController,
                             MutableClock sharedClock
    ) {
        this(name, config, environment, requestResponseController, sharedClock, null, null);
    }

    /**
     * Constructs the coordinator thread with required references.
     *
     * @param name the thread name or ID
     * @param config global model settings
     * @param environment the shared simulation environment
     * @param requestResponseController the controller managing request/response queues
     * @param sharedClock the clock used to keep a track of time passing in the model
     * @param predefinedGlobalAgentSet the pre-defined agent set instance to use as the global agent set
     */
    public CoordinatorThread(String name,
                             Config config,
                             Environment environment,
                             RequestResponseController requestResponseController,
                             MutableClock sharedClock,
                             AgentSet predefinedGlobalAgentSet,
                             Map<String, String> agentThreadMap
    ) {
        this.threadName = name;
        this.config = config;
        this.environment = environment;
        this.requestResponseController = requestResponseController;
        this.sharedClock = sharedClock;
        this.predefinedGlobalAgentSet = predefinedGlobalAgentSet;
        this.agentThreadMap = agentThreadMap;
    }

    /**
     * Signals the coordinator thread to stop processing and terminate.
     */
    public void shutdown() {
        isRunning = false;
        requestResponseController.getRequestQueue().offer(new Request("SYSTEM", threadName, RequestType.SHUTDOWN, null));
    }

    /**
     * Creates and registers a {@link CoordinatorRequestHandler} for each request type this co-ordinator can handle.
     *
     * <p>All handlers share the same global agent set, which is either the predefined set given during construction
     * or a newly created empty set.
     */
    private void initialiseHandlers() {
        AgentSet globalAgentSet;

        globalAgentSet = Objects.requireNonNullElseGet(predefinedGlobalAgentSet, AgentSet::new);

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
        requestHandlerMap.put(RequestType.KILL_AGENT,
                new CoordinatorRequestHandler.KillAgent(threadName, config, requestResponseController, globalAgentSet, environment, sharedClock));
        requestHandlerMap.put(RequestType.KILL_AGENTS,
                new CoordinatorRequestHandler.KillAgents(threadName, config, requestResponseController, globalAgentSet, environment, sharedClock));
    }

    /**
     * Sends an error response carrying the given cause back to the requester, so that the requester does not block
     * forever waiting for a reply.
     *
     * @param request the request whose handling failed
     * @param cause the failure to report back to the requester
     */
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
