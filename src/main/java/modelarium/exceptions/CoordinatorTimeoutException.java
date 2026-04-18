package modelarium.exceptions;

public class CoordinatorTimeoutException extends RuntimeException {
    public CoordinatorTimeoutException(String message) {
        super(message);
    }
}
