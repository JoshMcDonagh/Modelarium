package modelarium.multithreading.requestresponse;

import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.environments.Environment;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Provides a per-thread interface for sending requests to and receiving responses from
 * the simulation coordinator in a synchronised agent-based model.
 *
 * <p>This abstraction helps worker threads interact with the coordinator using
 * higher-level methods instead of directly manipulating queues.
 */
public class RequestResponseInterface {

    /** The name of the current thread or model element using this interface */
    private final String name;

    /** Whether the simulation is running in synchronised (coordinated) mode */
    private final boolean areProcessesSynced;

    private final Duration coordinatorTimeout;

    /** Shared queue for outgoing requests */
    private final BlockingQueue<Request> requestQueue;

    /** Shared queue for incoming responses */
    private final BlockingQueue<Response> responseQueue;

    /**
     * Constructs a new interface bound to the given model element or thread.
     *
     * @param name the name of this component (typically the thread or agent name)
     * @param config the shared model config
     * @param requestResponseController the controller that provides the queues
     */
    public RequestResponseInterface(String name, Config config, RequestResponseController requestResponseController) {
        this.name = name;
        this.areProcessesSynced = config.areThreadsSynced();
        this.coordinatorTimeout = Duration.ofSeconds(config.threadTimeoutSeconds());
        this.requestQueue = requestResponseController.getRequestQueue();
        this.responseQueue = requestResponseController.getResponseQueue(name);
    }

    private static CoordinatorTimeoutException makeCoordinatorTimeoutException(
            RequestType requestType,
            ResponseType expectedType,
            String requester
    ) {
        return new CoordinatorTimeoutException("Timed out waiting for " + expectedType + " response to " + requestType
                + " request from '" + requester + "'");
    }

    private Object sendAndAwait(Request request, ResponseType expectedType) throws InterruptedException {
        requestQueue.put(request);

        String expectedDestination = request.getRequester();
        RequestType originalType = request.getRequestType();
        long deadlineNanos = System.nanoTime() + coordinatorTimeout.toNanos();

        while (true) {
            long remainingNanos = deadlineNanos - System.nanoTime();
            if (remainingNanos <= 0)
                throw makeCoordinatorTimeoutException(originalType, expectedType, expectedDestination);

            Response response = responseQueue.poll(remainingNanos, TimeUnit.NANOSECONDS);
            if (response == null)
                throw makeCoordinatorTimeoutException(originalType, expectedType, expectedDestination);

            if (!Objects.equals(response.getDestination(), expectedDestination)) {
                responseQueue.put(response);
                continue;
            }

            if (response.getResponseType() == ResponseType.ERROR) {
                Throwable cause = response.getPayload() instanceof Throwable t ? t : null;
                throw new CoordinatorErrorException("Coordinator reported an error while handling " + originalType
                        + " request from '" + expectedDestination + "'", cause);
            }

            if (response.getResponseType() == expectedType)
                return response.getPayload();

            // An unrelated response type for this destination - requeue defensively
            responseQueue.put(response);
        }
    }

    private void awaitBarrier(RequestType requestType, ResponseType responseType) throws InterruptedException {
        if (!areProcessesSynced)
            return;

        sendAndAwait(new Request(name, null, requestType, null), responseType);
    }

    /**
     * Waits until all workers have completed their current simulation tick.
     */
    public void waitUntilAllWorkersFinishTick() throws InterruptedException {
        awaitBarrier(RequestType.ALL_WORKERS_FINISH_TICK, ResponseType.ALL_WORKERS_FINISH_TICK);
    }

    /**
     * Waits until all workers have updated the coordinator with their agent data.
     */
    public void waitUntilAllWorkersUpdateCoordinator() throws InterruptedException {
        awaitBarrier(RequestType.ALL_WORKERS_UPDATE_COORDINATOR, ResponseType.ALL_WORKERS_UPDATE_COORDINATOR);
    }

    /**
     * Requests a specific agent from the coordinator.
     *
     * @param requesterAgentName the name of the requesting agent
     * @param targetAgentName the name of the agent to retrieve
     * @return the {@link Agent} instance returned by the coordinator
     */
    public Agent getAgentFromCoordinator(String requesterAgentName, String targetAgentName) throws InterruptedException {
        requestQueue.put(new Request(requesterAgentName, targetAgentName, RequestType.AGENT_ACCESS, null));
        while (true) {
            Response response = responseQueue.take();
            if (Objects.equals(response.getResponseType(), ResponseType.AGENT_ACCESS) && Objects.equals(response.getDestination(), requesterAgentName))
                return (Agent) response.getPayload();
            responseQueue.put(response);
        }
    }

    /**
     * Requests a filtered subset of agents from the coordinator.
     *
     * @param requesterAgentName the name of the requester
     * @param agentFilter a predicate to apply to the global agent set
     * @return an {@link AgentSet} containing matching agents
     */
    public AgentSet getFilteredAgentsFromCoordinator(String requesterAgentName, Predicate<Agent> agentFilter) throws InterruptedException {
        requestQueue.put(new Request(requesterAgentName, null, RequestType.FILTERED_AGENTS_ACCESS, agentFilter));

        while (true) {
            Response response = responseQueue.take();
            if (Objects.equals(response.getResponseType(), ResponseType.FILTERED_AGENTS_ACCESS) && Objects.equals(response.getDestination(), requesterAgentName))
                return (AgentSet) response.getPayload();
            responseQueue.put(response);
        }
    }

    /**
     * Requests the current environment state from the coordinator.
     *
     * @param requesterAgentName the requesting agent's name
     * @return the current {@link Environment} instance
     */
    public Environment getEnvironmentFromCoordinator(String requesterAgentName) throws InterruptedException {
        requestQueue.put(new Request(requesterAgentName, null, RequestType.ENVIRONMENT_ATTRIBUTES_ACCESS, null));

        while (true) {
            Response response = responseQueue.take();
            if (Objects.equals(response.getResponseType(), ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS) && Objects.equals(response.getDestination(), requesterAgentName))
                return (Environment) response.getPayload();
            responseQueue.put(response);
        }
    }

    /**
     * Sends an update to the coordinator with the current agent set for this thread.
     *
     * @param agentSet the updated set of agents
     */
    public void updateCoordinatorAgents(AgentSet agentSet) throws InterruptedException {
        Objects.requireNonNull(agentSet, "agentSet");
        requestQueue.put(new Request(name, null, RequestType.UPDATE_COORDINATOR_AGENTS, agentSet));
    }
}
