package unit.modelarium.attributes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Event} class using a simple test subclass.
 *
 * <p>Verifies name assignment, recording flags, and triggering behaviour.
 */
public class EventTest {

    private Event testEvent;

    // A simple test subclass of Event to control trigger state manually
    private static class TestEvent extends Event {
        private boolean triggered;

        public TestEvent(String name, boolean isRecorded, boolean triggered) {
            super(name, isRecorded);
            this.triggered = triggered;
        }

        public void setTriggered(boolean triggered) {
            this.triggered = triggered;
        }

        @Override
        public boolean isTriggered() {
            return triggered;
        }

        @Override
        public void run() {
            // Logic for test purposes only — could toggle trigger, etc.
        }
    }

    @BeforeEach
    public void setup() {
        testEvent = new TestEvent("MyEvent", true, false);
    }

    @Test
    public void testNameIsAssignedCorrectly() {
        assertEquals("MyEvent", testEvent.getName());
    }

    @Test
    public void testIsRecordedFlag() {
        assertTrue(testEvent.isRecorded());
    }

    @Test
    public void testIsTriggeredDefault() {
        assertFalse(testEvent.isTriggered());
    }

    @Test
    public void testIsTriggeredAfterUpdate() {
        ((TestEvent) testEvent).setTriggered(true);
        assertTrue(testEvent.isTriggered());
    }

    @Test
    public void testGeneratedNameDoesNotThrow() {
        Event autoNamed = new TestEvent(null, true, false) {
            @Override public boolean isTriggered() { return false; }
            @Override public void run() {}
        };
        assertNotNull(autoNamed.getName());
    }
}
