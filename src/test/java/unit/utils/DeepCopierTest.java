package unit.utils;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DeepCopier} utility class.
 * <p>
 * These tests verify that the deep copying mechanism correctly
 * duplicates objects and collections without sharing references.
 */
public class DeepCopierTest {

    /**
     * A simple test class with fields to verify deep copying.
     */
    static class TestObject {
        String name;
        List<Integer> values;

        public TestObject(String name, List<Integer> values) {
            this.name = name;
            this.values = values;
        }

        // Equals override for assertion comparison
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(values, that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, values);
        }
    }

    @Test
    public void testDeepCopy_Object() {
        TestObject original = new TestObject("Alpha", Arrays.asList(1, 2, 3));
        TestObject copy = DeepCopier.deepCopy(original, TestObject.class);

        assertEquals(original, copy, "The copied object should be equal to the original.");
        assertNotSame(original, copy, "The copied object should not be the same reference as the original.");
        assertNotSame(original.values, copy.values, "Nested lists should also be deeply copied.");
    }

    @Test
    public void testDeepCopy_GenericCollection() {
        List<Map<String, Integer>> original = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 10);
        original.add(map);

        Type listType = new TypeToken<List<Map<String, Integer>>>() {}.getType();
        List<Map<String, Integer>> copy = DeepCopier.deepCopy(original, listType);

        assertEquals(original, copy, "The copied collection should be equal to the original.");
        assertNotSame(original, copy, "The copied list should not be the same reference.");
        assertNotSame(original.get(0), copy.get(0), "Nested map should also be a different reference.");
    }

    @Test
    public void testDeepCopy_NullInput() {
        TestObject copy = DeepCopier.deepCopy(null, TestObject.class);
        assertNull(copy, "Copying a null object should return null.");
    }
}
