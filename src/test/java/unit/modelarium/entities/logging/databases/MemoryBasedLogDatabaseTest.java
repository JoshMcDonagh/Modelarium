package unit.modelarium.entities.logging.databases;

import modelarium.entities.logging.databases.MemoryBasedAttributeSetLogDatabase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryBasedLogDatabaseTest {
    @Test
    public void testAddAttributeValue() {
        MemoryBasedAttributeSetLogDatabase database = new MemoryBasedAttributeSetLogDatabase();

        database.addAttributeValue("hunger", 0.5);

        List<Object> values = database.getAttributeColumnAsList("hunger");

        assertEquals(1, values.size());
        assertEquals(0.5, values.get(0));
    }

    @Test
    public void testAddAttributeValue_MultipleValuesPreserveOrder() {
        MemoryBasedAttributeSetLogDatabase database = new MemoryBasedAttributeSetLogDatabase();

        database.addAttributeValue("hunger", 0.1);
        database.addAttributeValue("hunger", 0.2);
        database.addAttributeValue("hunger", 0.3);

        List<Object> values = database.getAttributeColumnAsList("hunger");

        assertEquals(3, values.size());
        assertEquals(0.2, values.get(1));
    }

    @Test
    public void testAddAttributeValue_SeparateColumns() {
        MemoryBasedAttributeSetLogDatabase database = new MemoryBasedAttributeSetLogDatabase();

        database.addAttributeValue("x", 1);
        database.addAttributeValue("y", 2);

        assertEquals(1, database.getAttributeColumnAsList("x").size());
        assertEquals(1, database.getAttributeColumnAsList("y").size());
    }

    @Test
    public void testAddAttributeValue_WrongType_IllegalArgumentException() {
        MemoryBasedAttributeSetLogDatabase database = new MemoryBasedAttributeSetLogDatabase();
        database.addAttributeValue("hunger", 1.0);

        assertThrows(IllegalArgumentException.class, () -> database.addAttributeValue("hunger", "not a double"));
    }

    @Test
    public void testAddAttributeValue_NullValue() {
        MemoryBasedAttributeSetLogDatabase database = new MemoryBasedAttributeSetLogDatabase();

        database.addAttributeValue("x", null);

        List<Object> values = database.getAttributeColumnAsList("x");

        assertEquals(1, values.size());
        assertNull(values.get(0));
    }

    @Test
    public void testSetAttributeColumn() {
        MemoryBasedAttributeSetLogDatabase database = new MemoryBasedAttributeSetLogDatabase();
        database.addAttributeValue("x", 1.0);
        database.addAttributeValue("x", 2.0);

        database.setAttributeColumn("x", List.of(10.0, 20.0, 30.0));

        List<Object> values = database.getAttributeColumnAsList("x");

        assertEquals(3, values.size());
        assertEquals(10.0, values.get(0));
        assertEquals(30.0, values.get(2));
    }

    @Test
    public void testSetAttributeColumn_NullList() {
        MemoryBasedAttributeSetLogDatabase database = new MemoryBasedAttributeSetLogDatabase();

        database.setAttributeColumn("y", null);

        List<Object> values = database.getAttributeColumnAsList("y");

        assertNotNull(values);
        assertEquals(0, values.size());
    }

    @Test
    public void testDisconnect() {
        MemoryBasedAttributeSetLogDatabase database = new MemoryBasedAttributeSetLogDatabase();
        database.addAttributeValue("a", 1);
        database.addAttributeValue("b", 2);

        database.disconnect();

        assertNull(database.getAttributeColumnAsList("a"));
    }
}
