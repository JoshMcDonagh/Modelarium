package modelarium.exceptions;

/**
 * Exception for indicating that a simulation thread was interrupted while waiting on another thread.
 *
 * <p>This exception is thrown when a thread is interrupted while fetching an agent, filtered agents or the
 * environment through the co-ordinator, allowing the interruption to propagate as an unchecked exception.
 */
public class SimulationInterruptedException extends RuntimeException {

    /**
     * Constructs a new simulation interrupted exception with the specified detail message and cause.
     *
     * @param message the detail message describing the operation that was interrupted
     * @param cause the original {@link InterruptedException}
     */
    public SimulationInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
