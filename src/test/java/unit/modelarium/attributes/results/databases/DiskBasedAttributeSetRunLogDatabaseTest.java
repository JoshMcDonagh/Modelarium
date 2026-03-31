package unit.modelarium.attributes.results.databases;

import modelarium.attributes.results.databases.AttributeSetRunLogDatabase;
import modelarium.attributes.results.databases.DiskBasedAttributeSetRunLogDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DiskBasedAttributeSetRunLogDatabase}.
 *
 * <p>Verifies that values written to properties and events can be retrieved correctly.
 * Uses a temporary database file that is deleted after each test.
 */
public class DiskBasedAttributeSetRunLogDatabaseTest {

    private DiskBasedAttributeSetRunLogDatabase database;

    @BeforeEach
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        database = new DiskBasedAttributeSetRunLogDatabase();

        Method setDatabasePathMethod = AttributeSetRunLogDatabase.class
                .getDeclaredMethod("setDatabasePath", String.class);
        setDatabasePathMethod.setAccessible(true);
        setDatabasePathMethod.invoke(database, "test_" + System.nanoTime() + ".db");

        database.connect();
    }

    @AfterEach
    public void tearDown() {
        database.disconnect();
    }

    @Test
    public void testAddAndRetrievePropertyValue() {
        database.addPropertyValue("health", 100);
        List<Object> values = database.getPropertyColumnAsList("health");

        assertEquals(1, values.size());
        assertEquals(100, values.get(0));
    }

    @Test
    public void testAddAndRetrieveEventValues() {
        database.addPreEventValue("start", true);
        database.addPostEventValue("end", false);

        List<Object> preValues = database.getPreEventColumnAsList("start");
        List<Object> postValues = database.getPostEventColumnAsList("end");

        assertEquals(1, preValues.size());
        assertEquals(true, preValues.get(0));

        assertEquals(1, postValues.size());
        assertEquals(false, postValues.get(0));
    }

    @Test
    public void testSetAndGetPropertyColumn() {
        List<Object> input = Arrays.asList(1, 2, 3, 4, 5);
        database.setPropertyColumn("steps", input);
        List<Object> output = database.getPropertyColumnAsList("steps");

        assertEquals(input, output);
    }

    @Test
    public void testSetAndGetPreEventColumn() {
        List<Object> triggers = Arrays.asList(true, false, true);
        database.setPreEventColumn("alert", triggers);
        List<Object> result = database.getPreEventColumnAsList("alert");

        assertEquals(triggers, result);
    }

    @Test
    public void testSetAndGetPostEventColumn() {
        List<Object> triggers = Arrays.asList(false, false, true);
        database.setPostEventColumn("shutdown", triggers);
        List<Object> result = database.getPostEventColumnAsList("shutdown");

        assertEquals(triggers, result);
    }
}
