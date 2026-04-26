package modelarium.multithreading.requestresponse;

import modelarium.entities.agents.AgentSet;

/**
 * Represents a request sent from a worker thread to the coordinator thread
 * in a synchronised, multithreaded simulation.
 *
 * <p>Each request includes information about who sent it, who it is intended for,
 * the type of request, and an optional payload carrying additional data (e.g. agent name or filter).
 */
public class Request {

    /** The name of the thread or component initiating the request */
    private final String requester;

    /** The name of the intended recipient (usually the coordinator) */
    private final String destination;

    /** The type of request being made */
    private final RequestType requestType;

    /** Optional payload accompanying the request (e.g. agent name, predicate, etc.) */
    private final Object payload;

    /**
     * Constructs a new request to be processed by the coordinator.
     *
     * @param requester the originator of the request
     * @param destination the intended recipient of the request
     * @param requestType the type of request (see {@link RequestType})
     * @param payload the data being requested or sent (may be null)
     */
    public Request(String requester, String destination, RequestType requestType, Object payload) {
        this.requester = requester;
        this.destination = destination;
        this.requestType = requestType;
        // Type-level validation
        if (requestType == RequestType.UPDATE_COORDINATOR_AGENTS) {
            if (!(payload instanceof AgentSet)) {
                throw new IllegalArgumentException(
                        "Request UPDATE_COORDINATOR_AGENTS requires non-null AgentSet payload; got: " +
                                (payload == null ? "null" : payload.getClass().getName()) +
                                " from requester: " + requester
                );
            }
        }
        this.payload = payload;
    }

    /** @return the name of the requester who sent this request */
    public String getRequester() {
        return requester;
    }

    /** @return the intended recipient of the request */
    public String getDestination() {
        return destination;
    }

    /** @return the type of request being made */
    public RequestType getRequestType() {
        return requestType;
    }

    /** @return the request payload, or null if not required */
    public Object getPayload() {
        return payload;
    }
}
