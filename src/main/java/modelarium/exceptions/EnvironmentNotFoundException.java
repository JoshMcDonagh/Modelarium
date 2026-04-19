package modelarium.exceptions;

public class EnvironmentNotFoundException extends RuntimeException {
    public EnvironmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
