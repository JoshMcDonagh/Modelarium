package unit.modelarium.attributes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Attributes} base class using a concrete test implementation.
 *
 * <p>Verifies attribute storage, lookup by name/index, and replacement logic.
 */
public class AttributesTest {

    private static class TestAttribute extends Attribute {
        private boolean hasRun = false;

        public TestAttribute(String name, boolean isRecorded) {
            super(name, isRecorded);
        }

        @Override
        public void run() {
            hasRun = true;
        }

        public boolean hasRun() {
            return hasRun;
        }

        @Override
        public Attribute deepCopy() {
            return null;
        }
    }

    // Minimal concrete subclass for testing base behaviour
    private static class TestAttributes extends Attributes {
        public TestAttributes() {
            try {
                // Using reflection to initialise private fields
                var indexesField = Attributes.class.getDeclaredField("attributeIndexes");
                var listField = Attributes.class.getDeclaredField("attributes");
                indexesField.setAccessible(true);
                listField.setAccessible(true);
                indexesField.set(this, new HashMap<String, Integer>());
                listField.set(this, new ArrayList<Attribute>());
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialise Attributes fields for test.");
            }
        }

        @Override
        public void run() {
            // Just run all attributes
            for (int i = 0; i < size(); i++) {
                getAttribute(i).run();
            }
        }

        public void add(Attribute attr) {
            addAttribute(attr);
        }

        public Attribute getByName(String name) {
            return getAttribute(name);
        }

        public Attribute getByIndex(int index) {
            return getAttribute(index);
        }

        @Override
        public Attributes deepCopy() {
            return null;
        }
    }

    private TestAttributes attributes;

    @BeforeEach
    public void setup() {
        attributes = new TestAttributes();
    }

    @Test
    public void testAddAndRetrieveByNameAndIndex() {
        TestAttribute attr = new TestAttribute("A", true);
        attributes.add(attr);

        assertEquals(1, attributes.size(), "Should contain one attribute.");
        assertSame(attr, attributes.getByName("A"), "Attribute should be retrievable by name.");
        assertSame(attr, attributes.getByIndex(0), "Attribute should be retrievable by index.");
    }

    @Test
    public void testAttributeReplacementByName() {
        TestAttribute attr1 = new TestAttribute("X", true);
        TestAttribute attr2 = new TestAttribute("X", false);
        attributes.add(attr1);
        attributes.add(attr2);

        assertEquals(1, attributes.size(), "Should contain one attribute after replacement.");
        assertSame(attr2, attributes.getByName("X"), "Attribute should be replaced with the new one.");
    }

    @Test
    public void testRunDelegatesToAllAttributes() {
        TestAttribute attr1 = new TestAttribute("One", true);
        TestAttribute attr2 = new TestAttribute("Two", false);
        attributes.add(attr1);
        attributes.add(attr2);

        attributes.run();

        assertTrue(attr1.hasRun(), "Attribute One should have been run.");
        assertTrue(attr2.hasRun(), "Attribute Two should have been run.");
    }
}
