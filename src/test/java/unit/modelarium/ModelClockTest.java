package unit.modelarium;

import modelarium.ModelClock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelClockTest {
    @Test
    void newClock_startAtTickZero() {
        ModelClock modelClock = new ModelClock(10, 3);
        assertEquals(0, modelClock.getTick());
    }

    @Test
    void isWarmingUp_trueUntilWarmUpTicksAreCompleted() {
        ModelClock modelClock = new ModelClock(5, 3);

        // Warm-up ticks
        assertTrue(modelClock.isWarmingUp(), "tick 0 should be warm-up");
        modelClock.triggerTick(); // -> 1
        assertTrue(modelClock.isWarmingUp(), "tick 1 should be warm-up");
        modelClock.triggerTick(); // -> 2
        assertTrue(modelClock.isWarmingUp(), "tick 2 should be warm-up");

        // Non-warm-up ticks
        modelClock.triggerTick();
        assertFalse(modelClock.isWarmingUp(), "tick 3 should be post warm-up");
    }

    @Test
    void isRunning_trueForWarmUpPlusRunTicks_thenFalse() {
        int runTicks = 5;
        int warmUpTicks = 5;
        int totalTicks = runTicks + warmUpTicks;

        ModelClock modelClock = new ModelClock(runTicks, warmUpTicks);

        for (int i = 0; i < totalTicks; i++) {
            assertTrue(modelClock.isRunning(), "Expected running at tick " + modelClock.getTick());
            modelClock.triggerTick();
        }

        assertEquals(totalTicks, modelClock.getTick());
        assertFalse(modelClock.isRunning(), "Expected not running after total ticks elapsed");
    }

    @Test
    void triggerTick_incrementsTickWhileRunning() {
        ModelClock modelClock = new ModelClock(2, 1);

        assertEquals(0, modelClock.getTick());
        modelClock.triggerTick();
        assertEquals(1, modelClock.getTick());
        modelClock.triggerTick();
        assertEquals(2, modelClock.getTick());
        modelClock.triggerTick();
        assertEquals(3, modelClock.getTick());
    }

    @Test
    void triggerTick_doesNotAdvanceAfterFinished() {
        ModelClock modelClock = new ModelClock(2, 1); // total = 3

        while (modelClock.isRunning())
            modelClock.triggerTick();

        int finishedTick = modelClock.getTick();
        assertEquals(3, finishedTick);

        modelClock.triggerTick();

        assertEquals(finishedTick, modelClock.getTick(), "Tick should not advance after the modelClock stops running");
    }
}
