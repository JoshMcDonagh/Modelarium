package modelarium.multithreading.requestresponse;

import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.environments.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

/**
 * Abstract base class for handling requests sent to the coordinator thread in a synchronised model.
 *
 * <p>Each request type is mapped to a specific implementation of this handler. The {@link #initialise}
 * method sets up this mapping, and {@link #handleCoordinatorRequest(Request)} dispatches requests accordingly.
 *
 * <p>All handler subclasses must implement {@link #handleRequest(Request)}.
 */
public abstract class CoordinatorRequestHandler {

    /** Static mapping from request type to its associated handler */
    private static Map<RequestType, CoordinatorRequestHandler> requestHandlerMap;

    /**
     * Initialises the handler map for the coordinator, assigning an instance of each
     * request type's handler.
     *
     * @param threadName the coordinator thread's name
     * @param settings the global model settings
     * @param responseQueue the shared response queue
     * @param globalAgentSet the global agent set known to the coordinator
     * @param environment the shared environment
     */
    public static void initialise(String threadName,
                                  Config settings,
                                  BlockingQueue<Response> responseQueue,
                                  AgentSet globalAgentSet,
                                  Environment environment,
                                  EnvironmentContext environmentContext) {
        requestHandlerMap = new HashMap<>();
        requestHandlerMap.put(RequestType.ALL_WORKERS_FINISH_TICK,
                new AllWorkersFinishTick(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext));
        requestHandlerMap.put(RequestType.ALL_WORKERS_UPDATE_COORDINATOR,
                new AllWorkersUpdateCoordinator(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext));
        requestHandlerMap.put(RequestType.AGENT_ACCESS,
                new AgentAccess(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext));
        requestHandlerMap.put(RequestType.UPDATE_COORDINATOR_AGENTS,
                new UpdateCoordinatorAgents(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext));
        requestHandlerMap.put(RequestType.FILTERED_AGENTS_ACCESS,
                new FilteredAgentsAccess(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext));
        requestHandlerMap.put(RequestType.ENVIRONMENT_ATTRIBUTES_ACCESS,
                new EnvironmentAttributesAccess(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext));
    }

    /**
     * Handles a coordinator request by dispatching it to the appropriate handler.
     *
     * @param request the incoming request from a worker
     */
    public static void handleCoordinatorRequest(Request request) throws InterruptedException {
        CoordinatorRequestHandler handler = requestHandlerMap.get(request.getRequestType());
        if (handler == null)
            throw new IllegalStateException("No coordinator handler registered for request type: " + request.getRequestType());
        handler.handleRequest(request);
    }

    // Instance fields common to all handlers
    private final String threadName;
    private final Config config;
    private final BlockingQueue<Response> responseQueue;
    private final AgentSet globalAgentSet;
    private final Environment environment;
    private final EnvironmentContext environmentContext;

    private List<String> workersWaiting = new ArrayList<>();

    public CoordinatorRequestHandler(String threadName,
                                     Config config,
                                     BlockingQueue<Response> responseQueue,
                                     AgentSet globalAgentSet,
                                     Environment environment,
                                     EnvironmentContext environmentContext) {
        this.threadName = threadName;
        this.config = config;
        this.responseQueue = responseQueue;
        this.globalAgentSet = globalAgentSet;
        this.environment = environment;
        this.environmentContext = environmentContext;
    }

    /** @return the coordinator thread name */
    protected String getThreadName() {
        return threadName;
    }

    /** @return the global model settings */
    protected Config getConfig() {
        return config;
    }

    /** @return the queue to which coordinator responses are written */
    protected BlockingQueue<Response> getResponseQueue() {
        return responseQueue;
    }

    /** @return the current global set of all agents */
    protected AgentSet getGlobalAgentSet() {
        return globalAgentSet;
    }

    /** @return the global environment */
    protected Environment getEnvironment() {
        return environment;
    }

    protected EnvironmentContext getEnvironmentContext() {
        return environmentContext;
    }

    /** @return the list of workers currently waiting for a synchronisation barrier */
    protected List<String> getWorkersWaiting() {
        return workersWaiting;
    }

    /** Replaces the list of waiting workers (typically to reset it) */
    protected void setWorkersWaiting(List<String> workersWaiting) {
        this.workersWaiting = workersWaiting;
    }

    /**
     * Handles an incoming request from a worker. Must be implemented by subclasses.
     *
     * @param request the request to handle
     */
    public abstract void handleRequest(Request request) throws InterruptedException;

    // === Specific request handler implementations ===

    /**
     * Handles synchronisation for when all workers finish a tick.
     */
    public static class AllWorkersFinishTick extends CoordinatorRequestHandler {
        public AllWorkersFinishTick(String threadName, Config settings, BlockingQueue<Response> responseQueue, AgentSet globalAgentSet, Environment environment, EnvironmentContext environmentContext) {
            super(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext);
        }

        @Override
        public void handleRequest(Request request) throws InterruptedException {
            getWorkersWaiting().add(request.getRequester());
            if (getWorkersWaiting().size() == getConfig().threadCount()) {
                for (String worker : getWorkersWaiting())
                    getResponseQueue().put(new Response(getThreadName(), worker, ResponseType.ALL_WORKERS_FINISH_TICK, null));
                setWorkersWaiting(new ArrayList<>());
            }
        }
    }

    /**
     * Handles synchronisation for when all workers have updated the coordinator.
     */
    public static class AllWorkersUpdateCoordinator extends CoordinatorRequestHandler {
        public AllWorkersUpdateCoordinator(String threadName, Config settings, BlockingQueue<Response> responseQueue, AgentSet globalAgentSet, Environment environment, EnvironmentContext environmentContext) {
            super(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext);
        }

        @Override
        public void handleRequest(Request request) throws InterruptedException {
            getWorkersWaiting().add(request.getRequester());
            if (getWorkersWaiting().size() == getConfig().threadCount()) {
                getEnvironment().run();

                EnvironmentContext environmentContext = getEnvironmentContext();
                environmentContext.getClock().triggerTick();

                for (String worker : getWorkersWaiting())
                    getResponseQueue().put(new Response(getThreadName(), worker, ResponseType.ALL_WORKERS_UPDATE_COORDINATOR, null));

                setWorkersWaiting(new ArrayList<>());
            }

        }
    }

    /**
     * Provides access to an individual agent by name.
     */
    public static class AgentAccess extends CoordinatorRequestHandler {
        public AgentAccess(String threadName, Config settings, BlockingQueue<Response> responseQueue, AgentSet globalAgentSet, Environment environment, EnvironmentContext environmentContext) {
            super(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext);
        }

        @Override
        public void handleRequest(Request request) throws InterruptedException {
            Agent agent = getGlobalAgentSet().get((String) request.getPayload());
            getResponseQueue().put(new Response(getThreadName(), request.getRequester(), ResponseType.AGENT_ACCESS, agent));
        }
    }

    /**
     * Updates the global agent set with new agent states received from workers.
     */
    public static class UpdateCoordinatorAgents extends CoordinatorRequestHandler {
        public UpdateCoordinatorAgents(String threadName, Config settings, BlockingQueue<Response> responseQueue, AgentSet globalAgentSet, Environment environment, EnvironmentContext environmentContext) {
            super(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext);
        }

        @Override
        public void handleRequest(Request request) {
            Object payload = request.getPayload();
            if (!(payload instanceof AgentSet)) {
                throw new IllegalArgumentException(
                        "UPDATE_COORDINATOR_AGENTS payload must be an AgentSet (got: " +
                                (payload == null ? "null" : payload.getClass().getName()) +
                                ") from requester: " + request.getRequester()
                );
            }
            getGlobalAgentSet().update((AgentSet) payload);
        }
    }

    /**
     * Provides access to a filtered subset of the global agent set.
     */
    public static class FilteredAgentsAccess extends CoordinatorRequestHandler {
        public FilteredAgentsAccess(String threadName, Config settings, BlockingQueue<Response> responseQueue, AgentSet globalAgentSet, Environment environment, EnvironmentContext environmentContext) {
            super(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleRequest(Request request) throws InterruptedException {
            Object payload = request.getPayload();
            if (!(payload instanceof Predicate<?>)) {
                throw new IllegalArgumentException(
                        "FILTERED_AGENTS_ACCESS payload must be a Predicate<Agent> (got: " +
                                (payload == null ? "null" : payload.getClass().getName()) + ")"
                );
            }
            Predicate<Agent> filter = (Predicate<Agent>) payload;
            AgentSet filtered = getGlobalAgentSet().getFilteredAgents(filter);
            getResponseQueue().put(new Response(getThreadName(), request.getRequester(), ResponseType.FILTERED_AGENTS_ACCESS, filtered));
        }
    }

    /**
     * Provides access to the current environment state.
     */
    public static class EnvironmentAttributesAccess extends CoordinatorRequestHandler {
        public EnvironmentAttributesAccess(String threadName, Config settings, BlockingQueue<Response> responseQueue, AgentSet globalAgentSet, Environment environment, EnvironmentContext environmentContext) {
            super(threadName, settings, responseQueue, globalAgentSet, environment, environmentContext);
        }

        @Override
        public void handleRequest(Request request) throws InterruptedException {
            getResponseQueue().put(new Response(getThreadName(), request.getRequester(), ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS, getEnvironment()));
        }
    }
}
