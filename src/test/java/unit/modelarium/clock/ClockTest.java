package unit.modelarium.clock;

import modelarium.clock.ImmutableClock;
import modelarium.clock.MutableClock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MutableClock} and {@link ImmutableClock}.
 */
public class ClockTest {

    @Test
    void newClock_startsAtTickZero() {
        MutableClock clock = new MutableClock(10);
        assertEquals(0, clock.currentTick());
    }

    @Test
    void totalTickCount_matchesConstructorArgument() {
        MutableClock clock = new MutableClock(42);
        assertEquals(42, clock.totalTickCount());
    }

    @Test
    void triggerTick_incrementsTickByOne() {
        MutableClock clock = new MutableClock(5);
        clock.triggerTick();
        assertEquals(1, clock.currentTick());
    }

    @Test
    void triggerTick_progressesCorrectlyToCompletion() {
        int total = 5;
        MutableClock clock = new MutableClock(total);

        for (int i = 0; i < total; i++) {
            assertFalse(clock.isFinished(), "Should not be finished at tick " + clock.currentTick());
            clock.triggerTick();
        }

        assertTrue(clock.isFinished(), "Should be finished after all ticks");
        assertEquals(total, clock.currentTick());
    }

    @Test
    void triggerTick_doesNotAdvancePastTotal() {
        MutableClock clock = new MutableClock(3);

        // Run past the end
        for (int i = 0; i < 10; i++)
            clock.triggerTick();

        assertEquals(3, clock.currentTick(), "Tick should saturate at totalTickCount");
    }

    @Test
    void isFinished_falseWhenClockHasRemainingTicks() {
        MutableClock clock = new MutableClock(2);
        assertFalse(clock.isFinished());
        clock.triggerTick();
        assertFalse(clock.isFinished());
    }

    @Test
    void isFinished_trueOnceAllTicksElapsed() {
        MutableClock clock = new MutableClock(1);
        clock.triggerTick();
        assertTrue(clock.isFinished());
    }

    // ---- ImmutableClock delegation ----

    @Test
    void immutableClock_reflectsMutableState() {
        MutableClock mutable = new MutableClock(5);
        ImmutableClock immutable = new ImmutableClock(mutable);

        assertEquals(0, immutable.currentTick());
        assertEquals(5, immutable.totalTickCount());
        assertFalse(immutable.isFinished());

        mutable.triggerTick();

        assertEquals(1, immutable.currentTick(), "Immutable should see the updated tick");
    }

    @Test
    void immutableClock_seesFinishedStateFromMutable() {
        MutableClock mutable = new MutableClock(1);
        ImmutableClock immutable = new ImmutableClock(mutable);

        mutable.triggerTick();
        assertTrue(immutable.isFinished());
    }
}
