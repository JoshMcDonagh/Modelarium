package modelarium.exceptions;

/**
 * Exception for indicating that a requested agent is dead.
 *
 * <p>This exception is thrown when a requested agent is dead.
 */
public class AgentIsDeadException extends RuntimeException {
    /**
     * Constructs a new agent is dead exception with the specified detail message.
     *
     * @param message the detail message describing which requested agent is dead
     */
    public AgentIsDeadException(String message) {
        super(message);
    }
}
