package unit.modelarium.attributes.results.databases;

import modelarium.entities.logging.databases.MemoryBasedAttributeSetLogDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MemoryBasedAttributeSetLogDatabase}.
 *
 * <p>Verifies correct in-memory storage and retrieval of properties and event values.
 */
public class MemoryBasedAttributeSetLogDatabaseTest {

    private MemoryBasedAttributeSetLogDatabase database;

    @BeforeEach
    public void setUp() {
        database = new MemoryBasedAttributeSetLogDatabase();
    }

    @Test
    public void testAddAndRetrievePropertyValue() {
        database.addPropertyValue("speed", 10);
        List<Object> values = database.getPropertyColumnAsList("speed");

        assertEquals(1, values.size());
        assertEquals(10, values.get(0));
    }

    @Test
    public void testAddAndRetrievePreEventValue() {
        database.addPreEventValue("activated", true);
        List<Object> values = database.getPreEventColumnAsList("activated");

        assertEquals(1, values.size());
        assertTrue((Boolean) values.get(0));
    }

    @Test
    public void testAddAndRetrievePostEventValue() {
        database.addPostEventValue("completed", false);
        List<Object> values = database.getPostEventColumnAsList("completed");

        assertEquals(1, values.size());
        assertFalse((Boolean) values.get(0));
    }

    @Test
    public void testSetAndGetPropertyColumn() {
        database.addPropertyValue("count", 1);
        List<Object> replacement = Arrays.asList(1, 2, 3, 4);
        database.setPropertyColumn("count", replacement);

        assertEquals(replacement, database.getPropertyColumnAsList("count"));
    }

    @Test
    public void testSetAndGetPreEventColumn() {
        database.addPreEventValue("pinged", false);
        List<Object> updated = Arrays.asList(true, true);
        database.setPreEventColumn("pinged", updated);

        assertEquals(updated, database.getPreEventColumnAsList("pinged"));
    }

    @Test
    public void testSetAndGetPostEventColumn() {
        database.addPostEventValue("failed", false);
        List<Object> updated = Arrays.asList(true, false, true);
        database.setPostEventColumn("failed", updated);

        assertEquals(updated, database.getPostEventColumnAsList("failed"));
    }

    @Test
    public void testRejectTypeMismatchForProperty() {
        database.addPropertyValue("counter", 5);
        assertThrows(IllegalArgumentException.class,
                () -> database.addPropertyValue("counter", "wrongType"));
    }

    @Test
    public void testRejectTypeMismatchForPreEvent() {
        database.addPreEventValue("trigger", true);
        assertThrows(IllegalArgumentException.class,
                () -> database.addPreEventValue("trigger", "wrongType"));
    }

    @Test
    public void testRejectTypeMismatchForPostEvent() {
        database.addPostEventValue("done", false);
        assertThrows(IllegalArgumentException.class,
                () -> database.addPostEventValue("done", 123));
    }
}
