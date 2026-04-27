package modelarium.clock;

import modelarium.internal.Internal;

import java.util.concurrent.atomic.AtomicInteger;

public final class MutableClock implements Clock {
    private final int totalTickCount;

    private final AtomicInteger tick = new AtomicInteger(0);

    @Internal
    public MutableClock(int totalTickCount) {
        this.totalTickCount = totalTickCount;
    }

    /**
     * @return the current tick integer
     */
    @Override
    public int currentTick() {
        return tick.get();
    }

    @Override
    public int totalTickCount() {
        return totalTickCount;
    }

    @Override
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
