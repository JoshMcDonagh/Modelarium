package modelarium.multithreading.requestresponse;

import modelarium.Config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Central controller for managing the request and response queues used in
 * synchronised, multithreaded agent-based simulations.
 *
 * <p>This class acts as a message broker between worker threads and the coordinator thread,
 * ensuring safe, concurrent access to both queues via {@link BlockingQueue}.
 *
 * <p>Each thread can obtain a thread-safe {@link RequestResponseInterface} instance
 * tied to its name for interacting with the controller.
 */
public class RequestResponseController {

    /** Simulation settings, accessible to interfaces and handlers */
    private final Config settings;

    /** Queue for incoming requests from worker threads */
    private final BlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();

    /** Queue for outgoing responses from the coordinator to workers */
    private final BlockingQueue<Response> responseQueue = new LinkedBlockingQueue<>();

    /**
     * Constructs a new request-response controller for coordinating simulation threads.
     *
     * @param settings the shared model settings used across threads
     */
    public RequestResponseController(Config settings) {
        this.settings = settings;
    }

    /**
     * Returns the shared request queue.
     *
     * @return the request queue
     */
    public BlockingQueue<Request> getRequestQueue() {
        return requestQueue;
    }

    /**
     * Returns the shared response queue.
     *
     * @return the response queue
     */
    public BlockingQueue<Response> getResponseQueue() {
        return responseQueue;
    }

    /**
     * Creates and returns a {@link RequestResponseInterface} for a given thread name.
     * This interface wraps queue operations and helps manage request lifecycles.
     *
     * @param name the name of the requesting thread
     * @return a new {@link RequestResponseInterface} instance for the caller
     */
    public RequestResponseInterface getInterface(String name) {
        return new RequestResponseInterface(name, settings, this);
    }
}
