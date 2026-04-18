package modelarium.multithreading.requestresponse;

/**
 * Enum representing the different types of responses returned by the coordinator
 * to worker threads during synchronised simulation execution.
 */
public enum ResponseType {

    /**
     * Signals that all workers have completed their current tick
     * and may proceed to the next step.
     */
    ALL_WORKERS_FINISH_TICK,

    /**
     * Signals that all workers have finished updating the coordinator with
     * their agent data, allowing coordinated state changes to proceed.
     */
    ALL_WORKERS_UPDATE_COORDINATOR,

    /**
     * Response to a request for accessing a specific agent from another thread.
     */
    AGENT_ACCESS,

    /**
     * Response containing a filtered set of agents based on a predicate.
     */
    FILTERED_AGENTS_ACCESS,

    /**
     * Response containing the current state of environment attributes.
     */
    ENVIRONMENT_ATTRIBUTES_ACCESS,
    ERROR
}
