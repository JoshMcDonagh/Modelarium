package modelarium.multithreading.requestresponse;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.immutable.ReadOnlyAgentSet;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.ReadOnlyEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

/**
 * Abstract class for handling a single type of request sent to the co-ordinator by worker threads.
 *
 * <p>This class provides its subclasses with access to the shared simulation state the co-ordinator manages (the
 * global agent set, the environment, and the shared clock) along with the response queues used to reply to workers.
 * Each subclass implements the handling of one {@link RequestType}.
 */
public abstract class CoordinatorRequestHandler {

    /** The name of the co-ordinator thread this handler belongs to */
    private final String threadName;

    /** Global simulation configuration */
    private final Config config;

    /** Controller that manages the request and response queues for inter-thread communication */
    private final RequestResponseController requestResponseController;

    /** The global set of all agents in the model */
    private final AgentSet globalAgentSet;

    /** The environment shared across all workers */
    private final Environment environment;

    /** The clock shared with the workers to synchronise the passing of ticks */
    private final MutableClock sharedClock;

    /** The names of the workers currently waiting at this handler's synchronisation barrier */
    private List<String> workersWaiting = new ArrayList<>();

    /**
     * Constructs a new request handler with the shared simulation state it needs to handle requests.
     *
     * @param threadName the name of the co-ordinator thread this handler belongs to
     * @param config global model settings
     * @param requestResponseController the controller managing request/response queues
     * @param globalAgentSet the global set of all agents in the model
     * @param environment the environment shared across all workers
     * @param sharedClock the clock used to synchronise the entities and cores in the model
     */
    public CoordinatorRequestHandler(String threadName,
                                     Config config,
                                     RequestResponseController requestResponseController,
                                     AgentSet globalAgentSet,
                                     Environment environment,
                                     MutableClock sharedClock
    ) {
        this.threadName = threadName;
        this.config = config;
        this.requestResponseController = requestResponseController;
        this.globalAgentSet = globalAgentSet;
        this.environment = environment;
        this.sharedClock = sharedClock;
    }

    /**
     * Returns the name of the co-ordinator thread this handler belongs to.
     *
     * @return the coordinator thread name
     */
    protected String getThreadName() {
        return threadName;
    }

    /**
     * Returns the model's configuration settings.
     *
     * @return the global model settings
     */
    protected Config getConfig() {
        return config;
    }

    /**
     * Returns the response queue for the named destination.
     *
     * @param destinationName the name of the thread or model element the responses are for
     * @return the queue to which coordinator responses are written
     */
    protected BlockingQueue<Response> getResponseQueue(String destinationName) {
        return requestResponseController.getResponseQueue(destinationName);
    }

    /**
     * Returns the global agent set the co-ordinator manages.
     *
     * @return the current global set of all agents
     */
    protected AgentSet getGlobalAgentSet() {
        return globalAgentSet;
    }

    /**
     * Returns the environment shared across all workers.
     *
     * @return the global environment
     */
    protected Environment getEnvironment() {
        return environment;
    }

    /**
     * Returns the clock shared between the co-ordinator and the workers.
     *
     * @return the shared {@link MutableClock} instance
     */
    protected MutableClock getSharedClock() {
        return sharedClock;
    }

    /**
     * Returns the workers currently waiting at this handler's synchronisation barrier.
     *
     * @return the list of workers currently waiting for a synchronisation barrier
     */
    protected List<String> getWorkersWaiting() {
        return workersWaiting;
    }

    /**
     * Replaces the list of waiting workers (typically to reset it).
     *
     * @param workersWaiting the new list of waiting workers
     */
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

        /**
         * Constructs a new handler for the {@link RequestType#ALL_WORKERS_FINISH_TICK} request type.
         *
         * @param threadName the name of the co-ordinator thread this handler belongs to
         * @param settings global model settings
         * @param requestResponseController the controller managing request/response queues
         * @param globalAgentSet the global set of all agents in the model
         * @param environment the environment shared across all workers
         * @param sharedClock the clock used to synchronise the entities and cores in the model
         */
        public AllWorkersFinishTick(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, MutableClock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        /**
         * Records the requesting worker as waiting at the end-of-tick barrier, and once every worker has arrived,
         * triggers the passing of the shared clock's tick and releases all waiting workers.
         *
         * @param request the request to handle
         */
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

        /**
         * Constructs a new handler for the {@link RequestType#ALL_WORKERS_UPDATE_COORDINATOR} request type.
         *
         * @param threadName the name of the co-ordinator thread this handler belongs to
         * @param settings global model settings
         * @param requestResponseController the controller managing request/response queues
         * @param globalAgentSet the global set of all agents in the model
         * @param environment the environment shared across all workers
         * @param sharedClock the clock used to synchronise the entities and cores in the model
         */
        public AllWorkersUpdateCoordinator(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, MutableClock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        private AgentSet agentsKilledByEnvironment(AgentSet original, AgentSet modified) {
            return original.getFilteredAgents(
                    agent -> modified.doesAgentExist(agent.name()) && !agent.isDead() && modified.get(agent.name()).isDead()
            );
        }

        /**
         * Records the requesting worker as waiting at the post-update barrier, and once every worker has arrived,
         * runs the environment for the tick and releases all waiting workers.
         *
         * @param request the request to handle
         */
        @Override
        public void handleRequest(Request request) throws InterruptedException {
            getWorkersWaiting().add(request.getRequester());
            if (getWorkersWaiting().size() == getConfig().threadCount()) {
                getEnvironment().run();
                getGlobalAgentSet().update(agentsKilledByEnvironment(getGlobalAgentSet(), getEnvironment().context().getLocalAgentSet()), true);
                getEnvironment().context().getLocalAgentSet().update(getGlobalAgentSet(), true);

                for (String worker : getWorkersWaiting())
                    getResponseQueue(worker).put(new Response(getThreadName(), worker, ResponseType.ALL_WORKERS_UPDATE_COORDINATOR, null));

                setWorkersWaiting(new ArrayList<>());
            }

        }
    }

    /**
     * Provides access to the entire model's current population size.
     */
    public static class CurrentPopulationSizeFromCoordinatorAccess extends CoordinatorRequestHandler {
        /**
         * Constructs a new handler for the {@link RequestType#CURRENT_POPULATION_SIZE_ACCESS} request type.
         *
         * @param threadName the name of the co-ordinator thread this handler belongs to
         * @param settings global model settings
         * @param requestResponseController the controller managing request/response queues
         * @param globalAgentSet the global set of all agents in the model
         * @param environment the environment shared across all workers
         * @param sharedClock the clock used to synchronise the entities and cores in the model
         */
        public CurrentPopulationSizeFromCoordinatorAccess(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, MutableClock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        /**
         * Retrieves the current population size from the global agent set and sends it back to the requester.
         *
         * @param request the request to handle
         */
        @Override
        public void handleRequest(Request request) throws InterruptedException {
            getResponseQueue(request.getRequester()).put(new Response(getThreadName(), request.getRequester(), ResponseType.CURRENT_POPULATION_SIZE_ACCESS, getGlobalAgentSet().size()));
        }
    }

    /**
     * Provides access to an individual agent by name.
     */
    public static class AgentAccess extends CoordinatorRequestHandler {

        /**
         * Constructs a new handler for the {@link RequestType#AGENT_ACCESS} request type.
         *
         * @param threadName the name of the co-ordinator thread this handler belongs to
         * @param settings global model settings
         * @param requestResponseController the controller managing request/response queues
         * @param globalAgentSet the global set of all agents in the model
         * @param environment the environment shared across all workers
         * @param sharedClock the clock used to synchronise the entities and cores in the model
         */
        public AgentAccess(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, MutableClock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        /**
         * Retrieves the agent named by the request's payload from the global agent set and sends it back to the
         * requester.
         *
         * @param request the request to handle
         */
        @Override
        public void handleRequest(Request request) throws InterruptedException {
            Object payload = request.getPayload();

            if (!(payload instanceof String)) {
                throw new IllegalArgumentException("AGENT_ACCESS payload must be a string (got: "
                        + (payload == null ? "null" : payload.getClass().getName()) +
                        ") from requester: " + request.getRequester()
                );
            }

            ReadOnlyAgent agent = new ReadOnlyAgent(getGlobalAgentSet().get((String) payload));
            getResponseQueue(request.getRequester()).put(new Response(getThreadName(), request.getRequester(), ResponseType.AGENT_ACCESS, agent));
        }
    }

    /**
     * Updates the global agent set with new agent states received from workers.
     */
    public static class UpdateCoordinatorAgents extends CoordinatorRequestHandler {

        /**
         * Constructs a new handler for the {@link RequestType#UPDATE_COORDINATOR_AGENTS} request type.
         *
         * @param threadName the name of the co-ordinator thread this handler belongs to
         * @param settings global model settings
         * @param requestResponseController the controller managing request/response queues
         * @param globalAgentSet the global set of all agents in the model
         * @param environment the environment shared across all workers
         * @param sharedClock the clock used to synchronise the entities and cores in the model
         */
        public UpdateCoordinatorAgents(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, MutableClock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        /**
         * Merges the agent set carried by the request's payload into the global agent set.
         *
         * @param request the request to handle
         */
        @Override
        public void handleRequest(Request request) {
            Object payload = request.getPayload();
            if (!(payload instanceof AgentSet)) {
                throw new IllegalArgumentException("UPDATE_COORDINATOR_AGENTS payload must be an AgentSet (got: "
                        + (payload == null ? "null" : payload.getClass().getName())
                        + ") from requester: " + request.getRequester()
                );
            }
            getGlobalAgentSet().update((AgentSet) payload, true);
        }
    }

    /**
     * Provides access to the global agent set.
     */
    public static class GlobalAgentSetAccess extends CoordinatorRequestHandler {

        /**
         * Constructs a new handler for the {@link RequestType#AGENT_SET_ACCESS} request type.
         *
         * @param threadName the name of the co-ordinator thread this handler belongs to
         * @param settings global model settings
         * @param requestResponseController the controller managing request/response queues
         * @param globalAgentSet the global set of all agents in the model
         * @param environment the environment shared across all workers
         * @param sharedClock the clock used to synchronise the entities and cores in the model
         */
        public GlobalAgentSetAccess(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, MutableClock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        /**
         * Gets the global agent set and sends it to the requester.
         *
         * @param request the request to handle
         */
        @Override
        public void handleRequest(Request request) throws InterruptedException {
            getResponseQueue(request.getRequester()).put(new Response(
                    getThreadName(),
                    request.getRequester(),
                    ResponseType.AGENT_SET_ACCESS,
                    getGlobalAgentSet().getAsImmutable()));
        }
    }

    /**
     * Provides access to the current environment state.
     */
    public static class EnvironmentAttributesAccess extends CoordinatorRequestHandler {

        /**
         * Constructs a new handler for the {@link RequestType#ENVIRONMENT_ATTRIBUTES_ACCESS} request type.
         *
         * @param threadName the name of the co-ordinator thread this handler belongs to
         * @param settings global model settings
         * @param requestResponseController the controller managing request/response queues
         * @param globalAgentSet the global set of all agents in the model
         * @param environment the environment shared across all workers
         * @param sharedClock the clock used to synchronise the entities and cores in the model
         */
        public EnvironmentAttributesAccess(String threadName, Config settings, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, MutableClock sharedClock) {
            super(threadName, settings, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        /**
         * Sends the current environment back to the requester.
         *
         * @param request the request to handle
         */
        @Override
        public void handleRequest(Request request) throws InterruptedException {
            getResponseQueue(request.getRequester()).put(new Response(
                    getThreadName(),
                    request.getRequester(),
                    ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS,
                    new ReadOnlyEnvironment(getEnvironment())));
        }
    }

    public static class KillAgent extends CoordinatorRequestHandler {

        /**
         * Constructs a new request handler with the shared simulation state it needs to handle requests.
         *
         * @param threadName                the name of the co-ordinator thread this handler belongs to
         * @param config                    global model settings
         * @param requestResponseController the controller managing request/response queues
         * @param globalAgentSet            the global set of all agents in the model
         * @param environment               the environment shared across all workers
         * @param sharedClock               the clock used to synchronise the entities and cores in the model
         */
        public KillAgent(String threadName, Config config, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, MutableClock sharedClock) {
            super(threadName, config, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        @Override
        public void handleRequest(Request request) throws InterruptedException {
            Object payload = request.getPayload();
            if (!(payload instanceof String)) {
                throw new IllegalArgumentException("KILL_AGENT payload must be a String (got: "
                        + (payload == null ? "null" : payload.getClass().getName())
                        + ") from requester: " + request.getRequester()
                );
            }

            getGlobalAgentSet().get((String) payload).kill();
        }
    }

    public static class KillAgents extends CoordinatorRequestHandler {

        /**
         * Constructs a new request handler with the shared simulation state it needs to handle requests.
         *
         * @param threadName                the name of the co-ordinator thread this handler belongs to
         * @param config                    global model settings
         * @param requestResponseController the controller managing request/response queues
         * @param globalAgentSet            the global set of all agents in the model
         * @param environment               the environment shared across all workers
         * @param sharedClock               the clock used to synchronise the entities and cores in the model
         */
        public KillAgents(String threadName, Config config, RequestResponseController requestResponseController, AgentSet globalAgentSet, Environment environment, MutableClock sharedClock) {
            super(threadName, config, requestResponseController, globalAgentSet, environment, sharedClock);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleRequest(Request request) throws InterruptedException {
            Object payload = request.getPayload();
            if (!(payload instanceof List<?> list && list.stream().allMatch(String.class::isInstance))) {
                throw new IllegalArgumentException("KILL_AGENT payload must be a List of Strings (got: "
                        + (payload == null ? "null" : payload.getClass().getName())
                        + ") from requester: " + request.getRequester()
                );
            }

            for (String agentName : (List<String>) payload)
                getGlobalAgentSet().get(agentName).kill();
        }
    }
}
