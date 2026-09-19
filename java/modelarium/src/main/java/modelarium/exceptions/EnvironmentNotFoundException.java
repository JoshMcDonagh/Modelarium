package modelarium.exceptions;

/**
 * Exception for indicating that the model's environment could not be retrieved.
 *
 * <p>This exception is thrown when a request for the environment made through the co-ordinator fails or times out.
 */
public class EnvironmentNotFoundException extends RuntimeException {

    /**
     * Constructs a new environment not found exception with the specified detail message and cause.
     *
     * @param message the detail message describing who requested the environment
     * @param cause the underlying cause of the retrieval failure
     */
    public EnvironmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
