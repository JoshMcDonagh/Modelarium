package modelarium.exceptions;

public class CoordinatorErrorException extends RuntimeException {
    public CoordinatorErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
