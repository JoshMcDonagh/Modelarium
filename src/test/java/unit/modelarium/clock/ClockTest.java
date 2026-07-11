package unit.modelarium.clock;

import modelarium.clock.ImmutableClock;
import modelarium.clock.MutableClock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClockTest {
    @Test
    public void testCurrentTick_StartsAtZero() {
        MutableClock clock = new MutableClock(10);

        assertEquals(0, clock.currentTick());
    }

    @Test
    public void testTotalTickCount() {
        MutableClock clock = new MutableClock(42);

        assertEquals(42, clock.totalTickCount());
    }

    @Test
    public void testTriggerTick() {
        MutableClock clock = new MutableClock(5);

        clock.triggerTick();

        assertEquals(1, clock.currentTick());
    }

    @Test
    public void testTriggerTick_RunsToCompletion() {
        int totalTickCount = 5;
        MutableClock clock = new MutableClock(totalTickCount);

        for (int i = 0; i < totalTickCount; i++) {
            assertFalse(clock.isFinished());
            clock.triggerTick();
        }

        assertTrue(clock.isFinished());
        assertEquals(totalTickCount, clock.currentTick());
    }

    @Test
    public void testTriggerTick_DoesNotAdvancePastTotalTickCount() {
        MutableClock clock = new MutableClock(3);

        for (int i = 0; i < 10; i++)
            clock.triggerTick();

        assertEquals(3, clock.currentTick());
    }

    @Test
    public void testIsFinishedFalse() {
        MutableClock clock = new MutableClock(2);

        assertFalse(clock.isFinished());

        clock.triggerTick();

        assertFalse(clock.isFinished());
    }

    @Test
    public void testIsFinishedTrue() {
        MutableClock clock = new MutableClock(1);

        clock.triggerTick();

        assertTrue(clock.isFinished());
    }

    @Test
    public void testImmutableClock_ReflectsMutableClock() {
        MutableClock mutableClock = new MutableClock(5);
        ImmutableClock immutableClock = new ImmutableClock(mutableClock);

        assertEquals(0, immutableClock.currentTick());
        assertEquals(5, immutableClock.totalTickCount());
        assertFalse(immutableClock.isFinished());

        mutableClock.triggerTick();

        assertEquals(1, immutableClock.currentTick());
    }

    @Test
    public void testImmutableClock_IsFinished() {
        MutableClock mutableClock = new MutableClock(1);
        ImmutableClock immutableClock = new ImmutableClock(mutableClock);

        mutableClock.triggerTick();

        assertTrue(immutableClock.isFinished());
    }
}
