package unit.modelarium.attributes.results.databases;

import modelarium.logging.databases.AttributeSetRunLogDatabase;
import modelarium.logging.databases.DiskBasedAttributeSetRunLogDatabase;
import modelarium.logging.databases.MemoryBasedAttributeSetRunLogDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AttributeSetRunLogDatabaseFactory}.
 *
 * <p>Verifies default database type selection, dynamic class assignment,
 * and use of custom factories.
 */
public class AttributeSetLogDatabaseFactoryTest {

    @BeforeEach
    public void setUp() {
        // Reset factory state before each test to avoid cross-test contamination
        AttributeSetRunLogDatabaseFactory.clearCustomFactory();
        AttributeSetRunLogDatabaseFactory.setDatabaseToDiskBased();
    }

    @AfterEach
    void tearDown() {
        AttributeSetRunLogDatabaseFactory.clearCustomFactory();
        AttributeSetRunLogDatabaseFactory.setDatabaseToDiskBased();
    }

    @Test
    public void testDefaultCreatesDiskDatabaseIfUnset() {
        // Clear any existing configuration
        AttributeSetRunLogDatabaseFactory.setDatabaseClass(null);

        AttributeSetRunLogDatabase db = AttributeSetRunLogDatabaseFactory.createDatabase();

        assertNotNull(db);
        assertTrue(db instanceof DiskBasedAttributeSetRunLogDatabase);
        assertNotNull(db.getDatabasePath());
        assertTrue(db.getDatabasePath().endsWith(".db"));
    }

    @Test
    public void testCreateMemoryBasedDatabase() {
        AttributeSetRunLogDatabaseFactory.setDatabaseToMemoryBased();
        AttributeSetRunLogDatabase db = AttributeSetRunLogDatabaseFactory.createDatabase();

        assertNotNull(db);
        assertTrue(db instanceof MemoryBasedAttributeSetRunLogDatabase);
    }

    @Test
    public void testCustomFactoryIsUsed() {
        AttributeSetRunLogDatabase mockDb = new MemoryBasedAttributeSetRunLogDatabase();
        AttributeSetRunLogDatabaseFactory.setCustomFactory(() -> mockDb);

        AttributeSetRunLogDatabase result = AttributeSetRunLogDatabaseFactory.createDatabase();

        assertSame(mockDb, result, "Custom factory result should be returned");
    }

    @Test
    public void testCustomFactoryClearsCorrectly() {
        AttributeSetRunLogDatabase mockDb = new MemoryBasedAttributeSetRunLogDatabase();
        AttributeSetRunLogDatabaseFactory.setCustomFactory(() -> mockDb);

        // Create once with custom factory
        AttributeSetRunLogDatabase db1 = AttributeSetRunLogDatabaseFactory.createDatabase();
        assertSame(mockDb, db1);

        // Clear and ensure it no longer returns the mock
        AttributeSetRunLogDatabaseFactory.clearCustomFactory();
        AttributeSetRunLogDatabaseFactory.setDatabaseToMemoryBased();
        AttributeSetRunLogDatabase db2 = AttributeSetRunLogDatabaseFactory.createDatabase();
        assertNotSame(mockDb, db2);
    }

    @Test
    public void testCreateReturnsNullOnInvalidClass() {
        AttributeSetRunLogDatabaseFactory.setDatabaseClass(InvalidDatabase.class);

        AttributeSetRunLogDatabase db = AttributeSetRunLogDatabaseFactory.createDatabase();

        assertNull(db, "Should return null if instantiation fails");
    }

    /**
     * Dummy class without no-arg constructor, used to trigger instantiation error.
     */
    private static class InvalidDatabase extends AttributeSetRunLogDatabase {
        public InvalidDatabase(String fail) {
        }

        @Override
        public <T> void addPropertyValue(String propertyName, T propertyValue) {}

        @Override
        public <T> void addPreEventValue(String preEventName, T preEventValue) {}

        @Override
        public <T> void addPostEventValue(String postEventName, T postEventValue) {}

        @Override
        public void setPropertyColumn(String propertyName, List<Object> propertyValues) {}

        @Override
        public void setPreEventColumn(String preEventName, List<Object> preEventValues) {}

        @Override
        public void setPostEventColumn(String postEventName, List<Object> postEventValues) {}

        @Override
        public List<Object> getPropertyColumnAsList(String propertyName) {
            return null;
        }

        @Override
        public List<Object> getPreEventColumnAsList(String preEventName) {
            return null;
        }

        @Override
        public List<Object> getPostEventColumnAsList(String postEventName) {
            return null;
        }
    }
}
