package unit.modelarium.entities.logging.databases;

import modelarium.entities.logging.databases.DiskBasedAttributeSetLogDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DiskBasedLogDatabaseTest {
    private DiskBasedAttributeSetLogDatabase database;

    @BeforeEach
    public void setUp() {
        database = new DiskBasedAttributeSetLogDatabase();
        database.connect();
    }

    @AfterEach
    public void tearDown() {
        database.disconnect();
    }

    @Test
    public void testGetDatabasePath() {
        assertNotNull(database.getDatabasePath());
    }

    @Test
    public void testAddAttributeValue() {
        database.addAttributeValue("hunger", 0.5);

        List<Object> values = database.getAttributeColumnAsList("hunger");

        assertEquals(1, values.size());
        assertEquals(0.5, (double) values.get(0), 1e-9);
    }

    @Test
    public void testAddAttributeValue_MultipleValuesPreserveOrder() {
        database.addAttributeValue("x", 10);
        database.addAttributeValue("x", 20);
        database.addAttributeValue("x", 30);

        List<Object> values = database.getAttributeColumnAsList("x");

        assertEquals(3, values.size());
        assertEquals(10, values.get(0));
        assertEquals(20, values.get(1));
        assertEquals(30, values.get(2));
    }

    @Test
    public void testAddAttributeValue_BooleanValues() {
        database.addAttributeValue("triggered", true);
        database.addAttributeValue("triggered", false);

        List<Object> values = database.getAttributeColumnAsList("triggered");

        assertEquals(2, values.size());
        assertEquals(true, values.get(0));
        assertEquals(false, values.get(1));
    }

    @Test
    public void testAddAttributeValue_StringValues() {
        database.addAttributeValue("label", "hello");

        List<Object> values = database.getAttributeColumnAsList("label");

        assertEquals(1, values.size());
        assertEquals("hello", values.get(0));
    }

    @Test
    public void testSetAttributeColumn() {
        database.addAttributeValue("y", 1);
        database.addAttributeValue("y", 2);

        database.setAttributeColumn("y", List.of(100, 200, 300));

        List<Object> values = database.getAttributeColumnAsList("y");

        assertEquals(3, values.size());
        assertEquals(100, values.get(0));
    }

    @Test
    public void testSetAttributeColumn_EmptyList() {
        database.addAttributeValue("z", 1);

        database.setAttributeColumn("z", List.of());

        List<Object> values = database.getAttributeColumnAsList("z");

        assertEquals(0, values.size());
    }

    @Test
    public void testGetAttributeColumnAsList_NonExistentColumn() {
        List<Object> values = database.getAttributeColumnAsList("nonexistent");

        assertNotNull(values);
        assertEquals(0, values.size());
    }

    @Test
    public void testDisconnect_MultipleDisconnects() {
        database.disconnect();

        assertDoesNotThrow(() -> database.disconnect());
    }
}
