package unit.modelarium.attributes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Property} class using a concrete test implementation.
 *
 * <p>Validates value access, type information, and execution behaviour.
 */
public class PropertyTest {

    // A concrete implementation of Property<Integer> for testing
    private static class TestIntegerProperty extends Property<Integer> {
        private Integer value;
        private boolean wasRun;

        public TestIntegerProperty(String name, boolean isRecorded) {
            super(name, isRecorded, Integer.class);
            this.value = 0;
            this.wasRun = false;
        }

        @Override
        public void set(Integer value) {
            this.value = value;
        }

        @Override
        public Integer get() {
            return value;
        }

        @Override
        public void run() {
            // Simulates property logic — for testing, just mark that it was run
            wasRun = true;
        }

        public boolean wasRun() {
            return wasRun;
        }
    }

    private TestIntegerProperty property;

    @BeforeEach
    public void setup() {
        property = new TestIntegerProperty("TestProp", true);
    }

    @Test
    public void testInitialValueIsZero() {
        assertEquals(0, property.get());
    }

    @Test
    public void testSetAndGetValue() {
        property.set(42);
        assertEquals(42, property.get());
    }

    @Test
    public void testGetType() {
        assertEquals(Integer.class, property.getType());
    }

    @Test
    public void testRunMethodSetsRunFlag() {
        assertFalse(property.wasRun());
        property.run();
        assertTrue(property.wasRun());
    }

    @Test
    public void testRecordingFlag() {
        assertTrue(property.isRecorded());
    }
}
