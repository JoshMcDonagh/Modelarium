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
     * Request to access the current population size of entire the model.
     */
    CURRENT_POPULATION_SIZE_ACCESS,

    /**
     * Request to access a specific agent from another thread or core.
     */
    AGENT_ACCESS,

    /**
     * Request to access the global agent set.
     */
    AGENT_SET_ACCESS,

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
    SHUTDOWN,

    /**
     * Request to set an agent to a removed state.
     */
    KILL_AGENT,

    /**
     * Request to set multiple agents to a removed state.
     */
    KILL_AGENTS
}
