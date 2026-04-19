package modelarium;

import java.util.concurrent.atomic.AtomicInteger;

public final class Clock {
    private final int totalTickCount;

    private final AtomicInteger tick = new AtomicInteger(0);

    public Clock(int totalTickCount) {
        this.totalTickCount = totalTickCount;
    }

    public boolean isFinished() {
        return tick.get() >= totalTickCount;
    }

    /**
     * @return the current tick integer
     */
    public int currentTick() {
        return tick.get();
    }

    public int totalTickCount() {
        return totalTickCount;
    }

    /**
     * Triggers the passing of another tick if the model is running.
     */
    public void triggerTick() {
        tick.updateAndGet(current -> current >= totalTickCount ? current : current + 1);
    }
}
