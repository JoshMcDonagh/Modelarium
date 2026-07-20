package modelarium.multithreading.requestresponse;

/**
 * Enum representing the different types of requests that can be sent
 * between worker threads and the coordinator thread during synchronised simulation.
 */
public enum RequestType {

    /**
     * Indicates that a worker has completed its tick and is ready to synchronise
     * with other workers before continuing.
     */
    ALL_WORKERS_FINISH_TICK,

    /**
     * Indicates that a worker has updated the coordinator with its agent state
     * and is waiting for others to do the same.
     */
    ALL_WORKERS_UPDATE_COORDINATOR,

    /**
     * Request to access a specific agent from another thread or core.
     */
    AGENT_ACCESS,

    /**
     * Request to access a subset of agents based on a filter predicate.
     */
    FILTERED_AGENTS_ACCESS,

    /**
     * Request to retrieve the current state of the environment's attributes.
     */
    ENVIRONMENT_ATTRIBUTES_ACCESS,

    /**
     * Request to update the coordinator with the latest agent data from a worker thread.
     */
    UPDATE_COORDINATOR_AGENTS,

    /**
     * Request to shut the model down.
     */
    SHUTDOWN
}
