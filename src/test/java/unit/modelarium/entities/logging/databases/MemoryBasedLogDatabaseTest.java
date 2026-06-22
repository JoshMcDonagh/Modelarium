package unit.modelarium.entities.logging.databases;

import modelarium.entities.logging.databases.MemoryBasedAttributeSetLogDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MemoryBasedAttributeSetLogDatabase}.
 */
public class MemoryBasedAttributeSetLogDatabaseTest {

    private MemoryBasedAttributeSetLogDatabase db;

    @BeforeEach
    void setUp() {
        db = new MemoryBasedAttributeSetLogDatabase();
    }

    @Test
    void testAddAndRetrieveSingleValue() {
        db.addAttributeValue("hunger", 0.5);

        List<Object> values = db.getAttributeColumnAsList("hunger");
        assertEquals(1, values.size());
        assertEquals(0.5, values.get(0));
    }

    @Test
    void testAddMultipleValues() {
        db.addAttributeValue("hunger", 0.1);
        db.addAttributeValue("hunger", 0.2);
        db.addAttributeValue("hunger", 0.3);

        List<Object> values = db.getAttributeColumnAsList("hunger");
        assertEquals(3, values.size());
        assertEquals(0.2, values.get(1));
    }

    @Test
    void testSetAttributeColumnReplacesValues() {
        db.addAttributeValue("x", 1.0);
        db.addAttributeValue("x", 2.0);

        db.setAttributeColumn("x", Arrays.asList(10.0, 20.0, 30.0));

        List<Object> values = db.getAttributeColumnAsList("x");
        assertEquals(3, values.size());
        assertEquals(10.0, values.get(0));
        assertEquals(30.0, values.get(2));
    }

    @Test
    void testSetAttributeColumnWithNullCreatesEmptyList() {
        db.setAttributeColumn("y", null);

        List<Object> values = db.getAttributeColumnAsList("y");
        assertNotNull(values);
        assertEquals(0, values.size());
    }

    @Test
    void testDisconnectClearsAllData() {
        db.addAttributeValue("a", 1);
        db.addAttributeValue("b", 2);

        db.disconnect();

        assertNull(db.getAttributeColumnAsList("a"), "Data should be cleared after disconnect.");
    }

    @Test
    void testSeparateAttributeColumns() {
        db.addAttributeValue("x", 1);
        db.addAttributeValue("y", 2);

        assertEquals(1, db.getAttributeColumnAsList("x").size());
        assertEquals(1, db.getAttributeColumnAsList("y").size());
    }

    @Test
    void testTypeSafetyRejectsWrongType() {
        db.addAttributeValue("hunger", 1.0); // establishes Double

        assertThrows(IllegalArgumentException.class,
                () -> db.addAttributeValue("hunger", "not a double"),
                "Should reject values of a different type to the first recorded value.");
    }

    @Test
    void testNullValuesAreAllowed() {
        db.addAttributeValue("x", null);

        List<Object> values = db.getAttributeColumnAsList("x");
        assertEquals(1, values.size());
        assertNull(values.get(0));
    }
}
