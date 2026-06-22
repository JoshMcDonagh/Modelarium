package unit.modelarium.entities.logging.databases;

import modelarium.entities.logging.databases.DiskBasedAttributeSetLogDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DiskBasedAttributeSetLogDatabase}.
 *
 * <p>Each test creates a fresh temp database and cleans up after itself.
 */
public class DiskBasedLogDatabaseTest {

    private DiskBasedAttributeSetLogDatabase db;

    @BeforeEach
    void setUp() {
        db = new DiskBasedAttributeSetLogDatabase();
        db.connect();
    }

    @AfterEach
    void tearDown() {
        db.disconnect();
    }

    @Test
    void testDatabasePathIsGenerated() {
        assertNotNull(db.getDatabasePath(), "Should have an auto-generated temp path.");
    }

    @Test
    void testAddAndRetrieveSingleValue() {
        db.addAttributeValue("hunger", 0.5);

        List<Object> values = db.getAttributeColumnAsList("hunger");
        assertEquals(1, values.size());
        assertEquals(0.5, (double) values.get(0), 1e-9);
    }

    @Test
    void testAddMultipleValuesPreservesOrder() {
        db.addAttributeValue("x", 10);
        db.addAttributeValue("x", 20);
        db.addAttributeValue("x", 30);

        List<Object> values = db.getAttributeColumnAsList("x");
        assertEquals(3, values.size());
        assertEquals(10, values.get(0));
        assertEquals(20, values.get(1));
        assertEquals(30, values.get(2));
    }

    @Test
    void testSetAttributeColumnReplacesExisting() {
        db.addAttributeValue("y", 1);
        db.addAttributeValue("y", 2);

        db.setAttributeColumn("y", Arrays.asList(100, 200, 300));

        List<Object> values = db.getAttributeColumnAsList("y");
        assertEquals(3, values.size());
        assertEquals(100, values.get(0));
    }

    @Test
    void testSetAttributeColumnWithEmptyList() {
        db.addAttributeValue("z", 1);
        db.setAttributeColumn("z", List.of());

        List<Object> values = db.getAttributeColumnAsList("z");
        assertEquals(0, values.size());
    }

    @Test
    void testRetrieveNonExistentColumnReturnsEmptyList() {
        List<Object> values = db.getAttributeColumnAsList("nonexistent");
        assertNotNull(values);
        assertEquals(0, values.size());
    }

    @Test
    void testBooleanRoundTrip() {
        db.addAttributeValue("triggered", true);
        db.addAttributeValue("triggered", false);

        List<Object> values = db.getAttributeColumnAsList("triggered");
        assertEquals(2, values.size());
        assertEquals(true, values.get(0));
        assertEquals(false, values.get(1));
    }

    @Test
    void testStringRoundTrip() {
        db.addAttributeValue("label", "hello");

        List<Object> values = db.getAttributeColumnAsList("label");
        assertEquals(1, values.size());
        assertEquals("hello", values.get(0));
    }

    @Test
    void testMultipleDisconnectsDoNotThrow() {
        db.disconnect();
        assertDoesNotThrow(() -> db.disconnect());
    }
}
