package modelarium.multithreading;

/**
 * Record for containing the co-ordinator thread and the runnable it executes.
 *
 * <p>This record is returned when the model launches its co-ordinator, allowing the model to later signal the
 * co-ordinator to shut down and to join the underlying thread.
 *
 * @param coordinatorThread the thread the co-ordinator runnable is executing on
 * @param coordinator the co-ordinator runnable managing synchronised access to shared simulation state
 */
public record CoordinatorHandle(
        Thread coordinatorThread,
        CoordinatorThread coordinator
) {}
