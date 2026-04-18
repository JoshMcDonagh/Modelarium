package modelarium.multithreading.requestresponse;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.environments.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

public abstract class CoordinatorRequestHandler {
    private final String threadName;
    private final Config config;
    private final RequestResponseController requestResponseController;
    private final AgentSet globalAgentSet;
    private final Environment environment;
    private final Clock sharedClock;

    private List<String> workersWaiting = new ArrayList<>();

    public CoordinatorRequestHandler(String threadName,
                                     Config config,
                                     RequestResponseController requestResponseController,
                                     AgentSet globalAgentSet,
                                     Environment environment,
                                     Clock sharedClock
    ) {
        this.threadName = threadName;
        this.config = config;
        this.requestResponseController = requestResponseController;
        this.globalAgentSet = globalAgentSet;
        this.environment = environment;
        this.sharedClock = sharedClock;
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
    protected BlockingQueue<Response> getResponseQueue(String destinationName) {
        return requestResponseController.getResponseQueue(destinationName);
    }

    /** @return the current global set of all agents */
    protected AgentSet getGlobalAgentSet() {
        return globalAgentSet;
    }

    /** @return the global environment */
    protected Environment getEnvironment() {
        return environment;
    }

    protected Clock getSharedClock() {
        return sharedClock;
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
        public AllWorkersFinishTick(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, Clock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        @Override
        public void handleRequest(Request request) throws InterruptedException {
            getWorkersWaiting().add(request.getRequester());
            if (getWorkersWaiting().size() == getConfig().threadCount()) {
                if (getConfig().areThreadsSynced())
                    getSharedClock().triggerTick();

                for (String worker : getWorkersWaiting())
                    getResponseQueue(worker).put(new Response(getThreadName(), worker, ResponseType.ALL_WORKERS_FINISH_TICK, null));
                setWorkersWaiting(new ArrayList<>());
            }
        }
    }

    /**
     * Handles synchronisation for when all workers have updated the coordinator.
     */
    public static class AllWorkersUpdateCoordinator extends CoordinatorRequestHandler {
        public AllWorkersUpdateCoordinator(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, Clock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        @Override
        public void handleRequest(Request request) throws InterruptedException {
            getWorkersWaiting().add(request.getRequester());
            if (getWorkersWaiting().size() == getConfig().threadCount()) {
                getEnvironment().run();

                getEnvironment().context().getClock().triggerTick();

                for (String worker : getWorkersWaiting())
                    getResponseQueue(worker).put(new Response(getThreadName(), worker, ResponseType.ALL_WORKERS_UPDATE_COORDINATOR, null));

                setWorkersWaiting(new ArrayList<>());
            }

        }
    }

    /**
     * Provides access to an individual agent by name.
     */
    public static class AgentAccess extends CoordinatorRequestHandler {
        public AgentAccess(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, Clock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        @Override
        public void handleRequest(Request request) throws InterruptedException {
            Agent agent = getGlobalAgentSet().get((String) request.getPayload());
            getResponseQueue(request.getRequester()).put(new Response(getThreadName(), request.getRequester(), ResponseType.AGENT_ACCESS, agent));
        }
    }

    /**
     * Updates the global agent set with new agent states received from workers.
     */
    public static class UpdateCoordinatorAgents extends CoordinatorRequestHandler {
        public UpdateCoordinatorAgents(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, Clock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
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
        public FilteredAgentsAccess(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, Clock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
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
            getResponseQueue(request.getRequester()).put(new Response(getThreadName(), request.getRequester(), ResponseType.FILTERED_AGENTS_ACCESS, filtered));
        }
    }

    /**
     * Provides access to the current environment state.
     */
    public static class EnvironmentAttributesAccess extends CoordinatorRequestHandler {
        public EnvironmentAttributesAccess(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, Clock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        @Override
        public void handleRequest(Request request) throws InterruptedException {
            getResponseQueue(request.getRequester()).put(new Response(getThreadName(), request.getRequester(), ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS, getEnvironment()));
        }
    }
}
