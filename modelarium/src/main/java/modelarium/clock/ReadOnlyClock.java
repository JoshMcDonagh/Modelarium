package modelarium.clock;

import modelarium.internal.Internal;

/**
 * Class for providing a read-only view of a {@link Clock}.
 *
 * <p>This class wraps a mutable clock so that entities can observe the model's current tick without being able to
 * advance it.
 */
public final class ReadOnlyClock {

    /** The mutable clock this read-only view wraps */
    private final Clock clock;

    /**
     * Constructs a new immutable clock wrapping the specified mutable clock.
     *
     * @param clock the mutable clock to provide a read-only view of
     */
    @Internal
    public ReadOnlyClock(Clock clock) {
        this.clock = clock;
    }

    /**
     * Returns the tick the model is currently performing.
     *
     * @return the current tick number
     */
    public int currentTick() {
        return clock.currentTick();
    }

    /**
     * Returns the total number of ticks the model will perform.
     *
     * @return the total tick count
     */
    public int totalTickCount() {
        return clock.totalTickCount();
    }

    /**
     * Returns whether the model has performed all of its ticks.
     *
     * @return true if the current tick has reached the total tick count, false otherwise
     */
    public boolean isFinished() {
        return clock.isFinished();
    }
}
