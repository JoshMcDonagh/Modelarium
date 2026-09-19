package modelarium.exceptions;

/**
 * Exception for indicating that a model run failed to complete.
 *
 * <p>This exception is thrown when a worker thread fails during the simulation or when the model is interrupted while
 * waiting for worker results, with the worker's original failure attached as the cause.
 */
public class ModelRunException extends RuntimeException {

    /**
     * Constructs a new model run exception with the specified detail message and cause.
     *
     * @param message the detail message describing the stage of the run that failed
     * @param cause the underlying cause of the run failure
     */
    public ModelRunException(String message, Throwable cause) {
        super(message, cause);
    }
}
