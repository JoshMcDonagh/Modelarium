package modelarium.multithreading.requestresponse;

import modelarium.Config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    private final Config config;
    private final BlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();
    private final ConcurrentMap<String, BlockingQueue<Response>> responseQueues = new ConcurrentHashMap<>();

    /**
     * Constructs a new request-response controller for coordinating simulation threads.
     *
     * @param config the shared model settings used across threads
     */
    public RequestResponseController(Config config) {
        this.config = config;
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
    public BlockingQueue<Response> getResponseQueue(String threadName) {
        return responseQueues.computeIfAbsent(threadName, k -> new LinkedBlockingQueue<>());
    }

    /**
     * Creates and returns a {@link RequestResponseInterface} for a given thread name.
     * This interface wraps queue operations and helps manage request lifecycles.
     *
     * @param name the name of the requesting thread
     * @return a new {@link RequestResponseInterface} instance for the caller
     */
    public RequestResponseInterface getInterface(String name) {
        return new RequestResponseInterface(name, config, this);
    }
}
