package unit.modelarium.attributes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Attribute} class via a concrete test implementation.
 *
 * <p>Ensures that the name and recording flags are properly initialised and returned.
 */
public class AttributeTest {

    // A minimal concrete subclass for testing purposes
    private static class TestAttribute extends Attribute {
        private boolean wasRun = false;

        public TestAttribute(String name, boolean isRecorded) {
            super(name, isRecorded);
        }

        @Override
        public void run() {
            wasRun = true;
        }

        public boolean wasRun() {
            return wasRun;
        }

        @Override
        public Attribute deepCopy() {
            return null;
        }
    }

    @Test
    public void testAttributeProperties() {
        TestAttribute attr = new TestAttribute("TestAttr", true);

        assertEquals("TestAttr", attr.getName(), "Attribute name should match the given name.");
        assertTrue(attr.isRecorded(), "Attribute should be marked as recorded.");
    }

    @Test
    public void testRunMethodIsCalled() {
        TestAttribute attr = new TestAttribute("RunnableAttr", false);

        assertFalse(attr.wasRun(), "Attribute should not have run yet.");
        attr.run();
        assertTrue(attr.wasRun(), "Attribute run flag should be true after calling run().");
    }
}
