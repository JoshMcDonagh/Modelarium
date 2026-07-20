package modelarium.clock;

/**
 * Interface for representing the model's clock, which tracks the progression of time steps (ticks) during a run.
 *
 * <p>This interface provides read-only access to the clock's state and is implemented by {@link MutableClock}, which
 * the model uses to advance time, and {@link ImmutableClock}, which entities use to safely observe time.
 */
public sealed interface Clock permits ImmutableClock, MutableClock {

    /**
     * Returns the tick the model is currently performing.
     *
     * @return the current tick number
     */
    int currentTick();

    /**
     * Returns the total number of ticks the model will perform.
     *
     * @return the total tick count
     */
    int totalTickCount();

    /**
     * Returns whether the model has performed all of its ticks.
     *
     * @return true if the current tick has reached the total tick count, false otherwise
     */
    boolean isFinished();
}
