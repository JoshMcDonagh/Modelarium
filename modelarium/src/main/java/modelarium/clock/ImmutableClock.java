package modelarium.clock;

/**
 * Class for providing a read-only view of a {@link MutableClock}.
 *
 * <p>This class wraps a mutable clock so that entities can observe the model's current tick without being able to
 * advance it.
 */
public final class ImmutableClock implements Clock {

    /** The mutable clock this read-only view wraps */
    private final MutableClock clock;

    /**
     * Constructs a new immutable clock wrapping the specified mutable clock.
     *
     * @param clock the mutable clock to provide a read-only view of
     */
    public ImmutableClock(MutableClock clock) {
        this.clock = clock;
    }

    /**
     * Returns the tick the model is currently performing.
     *
     * @return the current tick number
     */
    @Override
    public int currentTick() {
        return clock.currentTick();
    }

    /**
     * Returns the total number of ticks the model will perform.
     *
     * @return the total tick count
     */
    @Override
    public int totalTickCount() {
        return clock.totalTickCount();
    }

    /**
     * Returns whether the model has performed all of its ticks.
     *
     * @return true if the current tick has reached the total tick count, false otherwise
     */
    @Override
    public boolean isFinished() {
        return clock.isFinished();
    }
}
