package modelarium;

public class Clock {
    private final int totalTickCount;

    private int tick = 0;

    public Clock(int totalTickCount) {
        this.totalTickCount = totalTickCount;
    }

    public boolean isFinished() {
        return tick >= totalTickCount;
    }

    /**
     * @return the current tick integer
     */
    public int currentTick() {
        return tick;
    }

    /**
     * Triggers the passing of another tick if the model is running.
     */
    public void triggerTick() {
        if (isFinished())
            return;
        tick++;
    }
}
