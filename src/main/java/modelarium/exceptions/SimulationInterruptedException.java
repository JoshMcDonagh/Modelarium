package modelarium.exceptions;

public class SimulationInterruptedException extends RuntimeException {
    public SimulationInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
