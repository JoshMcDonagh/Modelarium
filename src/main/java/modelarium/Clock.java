package modelarium;

/**
 * Class for handling model ticks and storing the model state.
 */
public class Clock {
    private final int numOfTicksToRun;
    private final int numOfWarmUpTicks;

    private int tick = 0;

    /**
     * Constructs a clock for the model with the given number of ticks to run and the number of warm up ticks.
     *
     * @param numOfTicksToRun the number of ticks the model will run for
     * @param numOfWarmUpTicks the number of ticks the warm-up phase performs
     */
    public Clock(int numOfTicksToRun, int numOfWarmUpTicks) {
        this.numOfTicksToRun = numOfTicksToRun;
        this.numOfWarmUpTicks = numOfWarmUpTicks;
    }

    /**
     * @return whether the model is currently warming up or not
     */
    public boolean isWarmingUp() {
        return tick < numOfWarmUpTicks;
    }

    /**
     * @return whether the model is currently running or not
     */
    public boolean isRunning() {
        return tick < numOfTicksToRun + numOfWarmUpTicks;
    }

    /**
     * @return the current tick integer
     */
    public int getTick() {
        return tick;
    }

    /**
     * Triggers the passing of another tick if the model is running.
     */
    public void triggerTick() {
        if (isRunning())
            tick++;
    }
}
