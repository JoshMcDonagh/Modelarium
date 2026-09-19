package modelarium.exceptions;

/**
 * Exception for indicating that the co-ordinator reported an error while handling a request.
 *
 * <p>This exception is thrown when a worker thread receives an error response from the co-ordinator, with the
 * co-ordinator's original failure attached as the cause.
 */
public class CoordinatorErrorException extends RuntimeException {

    /**
     * Constructs a new coordinator error exception with the specified detail message and cause.
     *
     * @param message the detail message describing the request the co-ordinator failed to handle
     * @param cause the failure reported by the co-ordinator
     */
    public CoordinatorErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
