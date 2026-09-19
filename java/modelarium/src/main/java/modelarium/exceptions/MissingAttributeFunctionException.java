package modelarium.exceptions;

/**
 * Exception for indicating that a functional attribute was used without a required logic function.
 *
 * <p>This exception is thrown when a functional attribute (such as a functional event or property) is run, triggered,
 * set or read without the corresponding function having been provided during construction.
 */
public class MissingAttributeFunctionException extends RuntimeException {

    /**
     * Constructs a new missing attribute function exception with the specified detail message.
     *
     * @param message the detail message describing which function is missing and for which attribute
     */
    public MissingAttributeFunctionException(String message) {
        super(message);
    }
}
