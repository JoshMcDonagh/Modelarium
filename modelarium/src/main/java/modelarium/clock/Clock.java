package modelarium.clock;

import modelarium.internal.Internal;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for representing a clock whose tick can be advanced as the model progresses.
 *
 * <p>This class is used by the model and its worker threads to drive the passing of time, with the current tick
 * stored atomically so that it can be safely shared between synchronised threads.
 */
public final class Clock {

    /** The total number of ticks the model will perform */
    private final int totalTickCount;

    /** The tick the model is currently performing, stored atomically for safe multithreaded access */
    private final AtomicInteger tick = new AtomicInteger(0);

    /**
     * Constructs a new mutable clock with the specified total tick count.
     *
     * @param totalTickCount the total number of ticks the model will perform
     */
    @Internal
    public Clock(int totalTickCount) {
        this.totalTickCount = totalTickCount;
    }

    /**
     * Returns the tick the model is currently performing.
     *
     * @return the current tick number
     */
    public int currentTick() {
        return tick.get();
    }

    /**
     * Returns the total number of ticks the model will perform.
     *
     * @return the total tick count
     */
    public int totalTickCount() {
        return totalTickCount;
    }

    /**
     * Returns whether the model has performed all of its ticks.
     *
     * @return true if the current tick has reached the total tick count, false otherwise
     */
    public boolean isFinished() {
        return tick.get() >= totalTickCount;
    }

    /**
     * Triggers the passing of another tick if the model is running.
     */
    @Internal
    public void triggerTick() {
        tick.updateAndGet(current -> current >= totalTickCount ? current : current + 1);
    }
}
