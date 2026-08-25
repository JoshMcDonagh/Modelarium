package unit.modelarium.clock;

import modelarium.clock.ReadOnlyClock;
import modelarium.clock.Clock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClockTest {
    @Test
    public void testCurrentTick_StartsAtZero() {
        Clock clock = new Clock(10);

        assertEquals(0, clock.currentTick());
    }

    @Test
    public void testTotalTickCount() {
        Clock clock = new Clock(42);

        assertEquals(42, clock.totalTickCount());
    }

    @Test
    public void testTriggerTick() {
        Clock clock = new Clock(5);

        clock.triggerTick();

        assertEquals(1, clock.currentTick());
    }

    @Test
    public void testTriggerTick_RunsToCompletion() {
        int totalTickCount = 5;
        Clock clock = new Clock(totalTickCount);

        for (int i = 0; i < totalTickCount; i++) {
            assertFalse(clock.isFinished());
            clock.triggerTick();
        }

        assertTrue(clock.isFinished());
        assertEquals(totalTickCount, clock.currentTick());
    }

    @Test
    public void testTriggerTick_DoesNotAdvancePastTotalTickCount() {
        Clock clock = new Clock(3);

        for (int i = 0; i < 10; i++)
            clock.triggerTick();

        assertEquals(3, clock.currentTick());
    }

    @Test
    public void testIsFinishedFalse() {
        Clock clock = new Clock(2);

        assertFalse(clock.isFinished());

        clock.triggerTick();

        assertFalse(clock.isFinished());
    }

    @Test
    public void testIsFinishedTrue() {
        Clock clock = new Clock(1);

        clock.triggerTick();

        assertTrue(clock.isFinished());
    }

    @Test
    public void testImmutableClock_ReflectsMutableClock() {
        Clock mutableClock = new Clock(5);
        ReadOnlyClock immutableClock = new ReadOnlyClock(mutableClock);

        assertEquals(0, immutableClock.currentTick());
        assertEquals(5, immutableClock.totalTickCount());
        assertFalse(immutableClock.isFinished());

        mutableClock.triggerTick();

        assertEquals(1, immutableClock.currentTick());
    }

    @Test
    public void testImmutableClock_IsFinished() {
        Clock mutableClock = new Clock(1);
        ReadOnlyClock immutableClock = new ReadOnlyClock(mutableClock);

        mutableClock.triggerTick();

        assertTrue(immutableClock.isFinished());
    }
}
