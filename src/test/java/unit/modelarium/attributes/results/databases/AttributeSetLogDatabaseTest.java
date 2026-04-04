package unit.modelarium.attributes.results.databases;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AttributeSetLogDatabaseTest {

    private TestAttributeSetLogDatabase database;

    @BeforeEach
    void setUp() {
        database = new TestAttributeSetLogDatabase();
    }

    @Test
    void testSetAndGetDatabasePath() {
        assertNull(database.getDatabasePath());
        database.setDatabasePath("test_path.db");
        assertEquals("test_path.db", database.getDatabasePath());
    }

    @Test
    void testPropertyColumnOperations() {
        List<Object> values = Arrays.asList("A", "B", "C");
        database.setPropertyColumn("prop1", values);
        assertEquals(values, database.getPropertyColumnAsList("prop1"));
    }

    @Test
    void testPreEventColumnOperations() {
        List<Object> values = Arrays.asList(true, false, true);
        database.setPreEventColumn("preEvt1", values);
        assertEquals(values, database.getPreEventColumnAsList("preEvt1"));
    }

    @Test
    void testPostEventColumnOperations() {
        List<Object> values = Arrays.asList(false, false, true);
        database.setPostEventColumn("postEvt1", values);
        assertEquals(values, database.getPostEventColumnAsList("postEvt1"));
    }

    @Test
    void testAddValues() {
        database.addPropertyValue("propX", 5);
        database.addPreEventValue("preX", true);
        database.addPostEventValue("postX", false);

        assertEquals(Collections.singletonList(5), database.getPropertyColumnAsList("propX"));
        assertEquals(Collections.singletonList(true), database.getPreEventColumnAsList("preX"));
        assertEquals(Collections.singletonList(false), database.getPostEventColumnAsList("postX"));
    }

    // A minimal concrete implementation for testing purposes
    static class TestAttributeSetLogDatabase extends AttributeSetLogDatabase {
        private final Map<String, List<Object>> properties = new HashMap<>();
        private final Map<String, List<Object>> preEvents = new HashMap<>();
        private final Map<String, List<Object>> postEvents = new HashMap<>();

        @Override
        public <T> void addPropertyValue(String propertyName, T propertyValue) {
            properties.computeIfAbsent(propertyName, k -> new ArrayList<>()).add(propertyValue);
        }

        @Override
        public <T> void addPreEventValue(String preEventName, T preEventValue) {
            preEvents.computeIfAbsent(preEventName, k -> new ArrayList<>()).add(preEventValue);
        }

        @Override
        public <T> void addPostEventValue(String postEventName, T postEventValue) {
            postEvents.computeIfAbsent(postEventName, k -> new ArrayList<>()).add(postEventValue);
        }

        @Override
        public void setPropertyColumn(String propertyName, List<Object> propertyValues) {
            properties.put(propertyName, new ArrayList<>(propertyValues));
        }

        @Override
        public void setPreEventColumn(String preEventName, List<Object> preEventValues) {
            preEvents.put(preEventName, new ArrayList<>(preEventValues));
        }

        @Override
        public void setPostEventColumn(String postEventName, List<Object> postEventValues) {
            postEvents.put(postEventName, new ArrayList<>(postEventValues));
        }

        @Override
        public List<Object> getPropertyColumnAsList(String propertyName) {
            return properties.getOrDefault(propertyName, Collections.emptyList());
        }

        @Override
        public List<Object> getPreEventColumnAsList(String preEventName) {
            return preEvents.getOrDefault(preEventName, Collections.emptyList());
        }

        @Override
        public List<Object> getPostEventColumnAsList(String postEventName) {
            return postEvents.getOrDefault(postEventName, Collections.emptyList());
        }

        // Allow test access to set path
        @Override
        protected void setDatabasePath(String databasePath) {
            super.setDatabasePath(databasePath);
        }
    }
}
