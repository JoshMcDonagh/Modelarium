package modelarium.exceptions;

/**
 * Exception for indicating that an attribute was accessed in a way that is not permitted.
 *
 * <p>This exception is thrown when a private attribute is requested by another entity, or when an attribute is
 * requested as a type (event, routine or property) that does not match its actual type.
 */
public class AttributeAccessException extends RuntimeException {

    /**
     * Constructs a new attribute access exception with the specified detail message.
     *
     * @param message the detail message describing the disallowed access
     */
    public AttributeAccessException(String message) {
        super(message);
    }
}
