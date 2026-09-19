package modelarium.exceptions;

/**
 * Exception for indicating that a requested agent could not be found.
 *
 * <p>This exception is thrown when an agent requested by name cannot be resolved, whether from the requesting
 * thread's local agent set, the context cache, or the co-ordinator's global agent set.
 */
public class AgentNotFoundException extends RuntimeException {
    /**
     * Constructs a new agent not found exception with the specified detail message and cause.
     *
     * @param message the detail message describing which agent could not be found and who requested it
     * @param cause the underlying cause of the lookup failure
     */
    public AgentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new agent not found exception with the specified detail message.
     *
     * @param message the detail message describing which agent could not be found and who requested it
     */
    public AgentNotFoundException(String message) {
        super(message);
    }
}
