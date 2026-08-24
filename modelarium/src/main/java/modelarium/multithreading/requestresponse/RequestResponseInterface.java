package modelarium.multithreading.requestresponse;

import modelarium.Config;
import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.immutable.ReadOnlyAgentSet;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.environments.ReadOnlyEnvironment;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;

import java.time.Duration;
import java.util.List;
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

    /** The maximum duration to wait for a response from the co-ordinator before timing out */
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
        this.coordinatorTimeout = config.threadTimeout();
        this.requestQueue = requestResponseController.getRequestQueue();
        this.responseQueue = requestResponseController.getResponseQueue(name);
    }

    /**
     * Creates a {@link CoordinatorTimeoutException} describing the request and expected response that timed out.
     *
     * @param requestType the type of the request that was sent
     * @param expectedType the type of the response that was expected
     * @param requester the name of the requester that was waiting for the response
     * @return a new {@link CoordinatorTimeoutException} instance
     */
    private static CoordinatorTimeoutException makeCoordinatorTimeoutException(
            RequestType requestType,
            ResponseType expectedType,
            String requester
    ) {
        return new CoordinatorTimeoutException("Timed out waiting for " + expectedType + " response to " + requestType
                + " request from '" + requester + "'");
    }

    /**
     * Sends a request to the co-ordinator and waits for a response of the expected type addressed to the requester.
     *
     * <p>Responses addressed to other destinations or of unrelated types are defensively requeued. If the
     * co-ordinator reports an error, it is rethrown as a {@link CoordinatorErrorException}; if no matching response
     * arrives within the model's thread timeout duration, a {@link CoordinatorTimeoutException} is thrown.
     *
     * @param request the request to send to the co-ordinator
     * @param expectedType the type of the response to wait for
     * @return the payload of the matching response
     */
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

    /**
     * Notifies the co-ordinator that this thread has reached a synchronisation barrier and waits until the
     * co-ordinator releases it.
     *
     * <p>If the model's threads are not synchronised, this method returns immediately.
     *
     * @param requestType the type of the barrier request to send
     * @param responseType the type of the barrier response to wait for
     */
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
     * Requests the current population size of the entire model from the coordinator.
     *
     * @param requesterEntityName the name of the requesting entity
     * @return the current population size integer returned by the coordinator
     */
    public int getCurrentPopulationSizeFromCoordinator(String requesterEntityName) throws InterruptedException {
        Request request = new Request(requesterEntityName, null, RequestType.CURRENT_POPULATION_SIZE_ACCESS, null);
        return (int) sendAndAwait(request, ResponseType.CURRENT_POPULATION_SIZE_ACCESS);
    }

    /**
     * Requests a specific agent from the coordinator.
     *
     * @param requesterEntityName the name of the requesting entity
     * @param targetAgentName the name of the agent to retrieve
     * @return the {@link ReadOnlyAgent} instance returned by the coordinator
     */
    public ReadOnlyAgent getAgentFromCoordinator(String requesterEntityName, String targetAgentName) throws InterruptedException {
        Request request = new Request(requesterEntityName, null, RequestType.AGENT_ACCESS, targetAgentName);
        return (ReadOnlyAgent) sendAndAwait(request, ResponseType.AGENT_ACCESS);
    }

    /**
     * Requests the global agent set from the coordinator.
     *
     * @param requesterEntityName the name of the requester
     * @return an {@link ReadOnlyAgentSet} containing the global agent set
     */
    public ReadOnlyAgentSet getGlobalAgentSetFromCoordinator(String requesterEntityName) throws InterruptedException {
        Request request = new Request(requesterEntityName, null, RequestType.AGENT_SET_ACCESS, null);
        return (ReadOnlyAgentSet) sendAndAwait(request, ResponseType.AGENT_SET_ACCESS);
    }

    /**
     * Requests the current environment state from the coordinator.
     *
     * @param requesterEntityName the requesting entity's name
     * @return the current {@link ReadOnlyEnvironment} instance
     */
    public ReadOnlyEnvironment getEnvironmentFromCoordinator(String requesterEntityName) throws InterruptedException {
        Request request = new Request(requesterEntityName, null, RequestType.ENVIRONMENT_ATTRIBUTES_ACCESS, null);
        return (ReadOnlyEnvironment) sendAndAwait(request, ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS);
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

    /**
     * Requests to set a specific agent in the coordinator as dead.
     *
     * @param agentName the name of the agent to kill
     */
    public void killCoordinatorAgent(String agentName)  throws InterruptedException {
        requestQueue.put(new Request(name, null, RequestType.KILL_AGENT, agentName));
    }

    /**
     * Requests to set a specific set of agents in the coordinator as dead.
     *
     * @param agentNames the list of names of agents to kill
     */
    public void killCoordinatorAgents(List<String> agentNames) throws InterruptedException {
        requestQueue.put(new Request(name, null, RequestType.KILL_AGENTS, agentNames));
    }
}
