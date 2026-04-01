package unit.modelarium;

import modelarium.Clock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClockTest {
    @Test
    void newClock_startAtTickZero() {
        Clock clock = new Clock(10, 3);
        assertEquals(0, clock.getTick());
    }

    @Test
    void isWarmingUp_trueUntilWarmUpTicksAreCompleted() {
        Clock clock = new Clock(5, 3);

        // Warm-up ticks
        assertTrue(clock.isWarmingUp(), "tick 0 should be warm-up");
        clock.triggerTick(); // -> 1
        assertTrue(clock.isWarmingUp(), "tick 1 should be warm-up");
        clock.triggerTick(); // -> 2
        assertTrue(clock.isWarmingUp(), "tick 2 should be warm-up");

        // Non-warm-up ticks
        clock.triggerTick();
        assertFalse(clock.isWarmingUp(), "tick 3 should be post warm-up");
    }

    @Test
    void isRunning_trueForWarmUpPlusRunTicks_thenFalse() {
        int runTicks = 5;
        int warmUpTicks = 5;
        int totalTicks = runTicks + warmUpTicks;

        Clock clock = new Clock(runTicks, warmUpTicks);

        for (int i = 0; i < totalTicks; i++) {
            assertTrue(clock.isRunning(), "Expected running at tick " + clock.getTick());
            clock.triggerTick();
        }

        assertEquals(totalTicks, clock.getTick());
        assertFalse(clock.isRunning(), "Expected not running after total ticks elapsed");
    }

    @Test
    void triggerTick_incrementsTickWhileRunning() {
        Clock clock = new Clock(2, 1);

        assertEquals(0, clock.getTick());
        clock.triggerTick();
        assertEquals(1, clock.getTick());
        clock.triggerTick();
        assertEquals(2, clock.getTick());
        clock.triggerTick();
        assertEquals(3, clock.getTick());
    }

    @Test
    void triggerTick_doesNotAdvanceAfterFinished() {
        Clock clock = new Clock(2, 1); // total = 3

        while (clock.isRunning())
            clock.triggerTick();

        int finishedTick = clock.getTick();
        assertEquals(3, finishedTick);

        clock.triggerTick();

        assertEquals(finishedTick, clock.getTick(), "Tick should not advance after the modelClock stops running");
    }
}
