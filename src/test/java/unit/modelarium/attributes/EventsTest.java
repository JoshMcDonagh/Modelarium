package unit.modelarium.attributes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Events} class using mock event implementations.
 *
 * <p>Tests addition, access, and conditional execution behaviour of events.
 */
public class EventsTest {

    private Events events;

    // A test implementation of Event to track whether run() is called
    private static class TestEvent extends Event {
        private final AtomicBoolean triggered;
        private final AtomicBoolean wasRun;

        public TestEvent(String name, boolean isTriggeredInitially) {
            super(name);
            this.triggered = new AtomicBoolean(isTriggeredInitially);
            this.wasRun = new AtomicBoolean(false);
        }

        public void setTriggered(boolean triggered) {
            this.triggered.set(triggered);
        }

        public boolean wasRun() {
            return wasRun.get();
        }

        @Override
        public boolean isTriggered() {
            return triggered.get();
        }

        @Override
        public void run() {
            wasRun.set(true);
        }
    }

    @BeforeEach
    public void setup() {
        events = new Events();
    }

    @Test
    public void testAddAndRetrieveEventByNameAndIndex() {
        TestEvent event = new TestEvent("TestEvent", true);
        events.add(event);

        assertEquals(event, events.get("TestEvent"));
        assertEquals(event, events.get(0));
    }

    @Test
    public void testAddMultipleEvents() {
        TestEvent e1 = new TestEvent("E1", true);
        TestEvent e2 = new TestEvent("E2", false);
        events.add(Arrays.asList(e1, e2));

        assertEquals(2, events.size());
        assertEquals(e1, events.get("E1"));
        assertEquals(e2, events.get(1));
    }

    @Test
    public void testRunOnlyExecutesTriggeredEvents() {
        TestEvent triggeredEvent = new TestEvent("Triggered", true);
        TestEvent untriggeredEvent = new TestEvent("NotTriggered", false);

        events.add(triggeredEvent);
        events.add(untriggeredEvent);

        events.run();

        assertTrue(triggeredEvent.wasRun());
        assertFalse(untriggeredEvent.wasRun());
    }

    @Test
    public void testEventCanChangeTriggerState() {
        TestEvent event = new TestEvent("Dynamic", false);
        events.add(event);

        events.run();
        assertFalse(event.wasRun());

        event.setTriggered(true);
        events.run();
        assertTrue(event.wasRun());
    }
}
