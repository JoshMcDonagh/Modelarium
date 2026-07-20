package modelarium.multithreading.requestresponse;

/**
 * Represents a response from the coordinator thread to a worker thread
 * in a synchronised simulation model.
 *
 * <p>Each response contains metadata identifying the requester and destination,
 * a {@link ResponseType} to indicate the nature of the response, and an optional payload
 * containing data (such as an agent, environment, or collection).
 */
public class Response {

    /** The name of the thread or component that made the original request */
    private final String requester;

    /** The intended recipient of the response */
    private final String destination;

    /** The type of response being returned */
    private final ResponseType responseType;

    /** The payload of the response (may be null depending on type) */
    private final Object payload;

    /**
     * Constructs a new response.
     *
     * @param requester the name of the requester who initiated the original request
     * @param destination the name of the intended recipient of the response
     * @param responseType the type of response
     * @param payload the response payload (data returned, may be null)
     */
    public Response(String requester, String destination, ResponseType responseType, Object payload) {
        this.requester = requester;
        this.destination = destination;
        this.responseType = responseType;
        this.payload = payload;
    }

    /**
     * Returns the name of the requester who initiated the original request.
     *
     * @return the requester's name
     */
    public String getRequester() {
        return requester;
    }

    /**
     * Returns the intended recipient of the response.
     *
     * @return the destination's name
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Returns the type of response being returned.
     *
     * @return the response's {@link ResponseType}
     */
    public ResponseType getResponseType() {
        return responseType;
    }

    /**
     * Returns the data accompanying this response.
     *
     * @return the payload attached to this response, or null if not applicable
     */
    public Object getPayload() {
        return payload;
    }
}
