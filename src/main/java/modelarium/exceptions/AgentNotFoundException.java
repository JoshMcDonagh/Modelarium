package modelarium.exceptions;

public class AgentNotFoundException extends RuntimeException {
    public AgentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AgentNotFoundException(String message) {
        super(message);
    }
}
