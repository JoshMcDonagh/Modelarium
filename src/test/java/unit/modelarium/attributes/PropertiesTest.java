package unit.modelarium.attributes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Properties} class.
 *
 * <p>Verifies property management including addition, access, and execution behaviour.
 */
public class PropertiesTest {

    // A minimal concrete implementation for testing integer properties
    private static class TestIntegerProperty extends Property<Integer> {
        private Integer value;
        private boolean wasRun;

        public TestIntegerProperty(String name) {
            super(name, true, Integer.class);
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
            wasRun = true;
        }

        public boolean wasRun() {
            return wasRun;
        }
    }

    private Properties properties;

    @BeforeEach
    public void setup() {
        properties = new Properties();
    }

    @Test
    public void testAddAndGetByName() {
        TestIntegerProperty prop = new TestIntegerProperty("speed");
        properties.add(prop);
        assertSame(prop, properties.get("speed"));
    }

    @Test
    public void testAddAndGetByIndex() {
        TestIntegerProperty prop = new TestIntegerProperty("energy");
        properties.add(prop);
        assertSame(prop, properties.get(0));
    }

    @Test
    public void testAddMultipleProperties() {
        TestIntegerProperty a = new TestIntegerProperty("a");
        TestIntegerProperty b = new TestIntegerProperty("b");
        properties.add(Arrays.asList(a, b));
        assertEquals(2, properties.size());
        assertSame(b, properties.get("b"));
    }

    @Test
    public void testRunInvokesAllProperties() {
        TestIntegerProperty a = new TestIntegerProperty("a");
        TestIntegerProperty b = new TestIntegerProperty("b");
        properties.add(Arrays.asList(a, b));

        properties.run();

        assertTrue(a.wasRun());
        assertTrue(b.wasRun());
    }
}
