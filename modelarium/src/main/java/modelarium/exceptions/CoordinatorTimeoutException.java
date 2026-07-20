package modelarium.exceptions;

/**
 * Exception for indicating that a thread timed out while waiting for a response from the co-ordinator.
 *
 * <p>This exception is thrown when no response of the expected type arrives within the model's configured thread
 * timeout duration.
 */
public class CoordinatorTimeoutException extends RuntimeException {

    /**
     * Constructs a new coordinator timeout exception with the specified detail message.
     *
     * @param message the detail message describing the request and expected response that timed out
     */
    public CoordinatorTimeoutException(String message) {
        super(message);
    }
}
