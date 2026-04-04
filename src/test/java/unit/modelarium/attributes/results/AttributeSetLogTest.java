package unit.modelarium.attributes.results;

import modelarium.attributes.*;
import modelarium.attributes.AttributeSet;
import modelarium.logging.AttributeSetLog;
import modelarium.logging.databases.AttributeSetLogDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link AttributeSetLog} class.
 *
 * <p>Ensures correct initialisation, recording, and value retrieval
 * using mocked attributes and backing databases.
 */
public class AttributeSetLogTest {

    private AttributeSetLog results;
    private AttributeSetLogDatabase mockDatabase;

    @BeforeEach
    public void setup() {
        // Override the factory to return a mocked database
        mockDatabase = mock(AttributeSetLogDatabase.class);
        AttributeSetRunLogDatabaseFactory.setCustomFactory(() -> mockDatabase);

        // Build a simple attribute set with recordable items
        Properties properties = new Properties();
        properties.add(new TestProperty("prop1"));

        Events preEvents = new Events();
        preEvents.add(new TestEvent("preEvent1"));

        Events postEvents = new Events();
        postEvents.add(new TestEvent("postEvent1"));

        AttributeSet attributeSet = new AttributeSet("testSet", preEvents, properties, postEvents);
        results = new AttributeSetLog("Agent_0", attributeSet);
    }

    @Test
    public void testMetadataIsInitialisedCorrectly() {
        assertEquals("Agent_0", results.getOwnerName());
        assertEquals("testSet", results.getAttributeSetName());
        assertEquals(List.of("prop1"), results.getPropertyNamesList());
        assertEquals(List.of("preEvent1"), results.getPreEventNamesList());
        assertEquals(List.of("postEvent1"), results.getPostEventNamesList());
        assertEquals(Integer.class, results.getPropertyType("prop1"));
    }

    @Test
    public void testLogPropertyDelegatesToDatabase() {
        results.recordProperty("prop1", 42);
        verify(mockDatabase).addPropertyValue("prop1", 42);
    }

    @Test
    public void testLogPreEventDelegatesToDatabase() {
        results.recordPreEvent("preEvent1", true);
        verify(mockDatabase).addPreEventValue("preEvent1", true);
    }

    @Test
    public void testLogPostEventDelegatesToDatabase() {
        results.recordPostEvent("postEvent1", false);
        verify(mockDatabase).addPostEventValue("postEvent1", false);
    }

    @Test
    public void testDisconnectDatabaseCallsUnderlyingDisconnect() {
        results.disconnectDatabase();
        verify(mockDatabase).disconnect();
    }

    // Simple mock property for testing
    static class TestProperty extends Property<Integer> {
        public TestProperty(String name) {
            super(name, true, Integer.class);
        }
        @Override public void set(Integer value) {}
        @Override public Integer get() { return 0; }
        @Override public void run() {}
    }

    // Simple mock event for testing
    static class TestEvent extends Event {
        public TestEvent(String name) {
            super(name, true);
        }
        @Override public boolean isTriggered() { return true; }
        @Override public void run() {}
    }
}
