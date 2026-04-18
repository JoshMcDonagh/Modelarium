package integration.diskDatabaseSanityIntegrationTest;

import integration.agentEnvironmentSyncIntegrationTest.attributes.ModelAttributes;
import integration.agentEnvironmentSyncIntegrationTest.results.ModelMutableResults;
import modelarium.Config;
import modelarium.Model;
import modelarium.results.mutable.MutableResults;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DiskDatabaseSanityIntegrationTest {

    @SuppressWarnings("unchecked")
    private static List<Object> getPrivateList(Object target, String fieldName) throws Exception {
        Field f = target.getClass().getSuperclass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return (List<Object>) f.get(target);
    }

    @Test
    public void testDiskBackedDatabasesExistAndAreReadable() throws Exception {
        Config s = new Config();
        s.setNumOfAgents(20);
        s.setNumOfCores(2);
        s.setNumOfTicksToRun(10);
        s.setNumOfWarmUpTicks(2);

        s.setAreProcessesSynced(true);
        s.setIsCacheUsed(false);
        s.setDoAgentStoresHoldAgentCopies(false);

        s.setBaseAgentAttributeSetCollection(ModelAttributes.getAgentAttributeSetCollection());
        s.setBaseEnvironmentAttributeSetCollection(ModelAttributes.getEnvironmentAttributeSetCollection());

        s.setResultsClass(ModelMutableResults.class);
        s.setResults(new ModelMutableResults());

        s.setAgentGenerator(new DefaultAgentGenerator());
        s.setEnvironmentGenerator(new DefaultEnvironmentGenerator());
        s.setModelScheduler(new InOrderScheduler());

        s.setAreAttributeSetResultsStoredOnDisk(true);

        MutableResults r = new Model(s).run();

        // Pull out the internal DB lists from Results
        List<Object> agentDBs = getPrivateList(r, "accumulatedAgentAttributeSetResultsDatabaseList");
        List<Object> envDBs   = getPrivateList(r, "processedEnvironmentAttributeSetResultsDatabaseList");

        assertTrue(agentDBs.size() > 0, "should have at least 1 accumulated agent db");
        assertTrue(envDBs.size() > 0, "should have at least 1 processed env db");

        // For each DB: ensure file exists and has expected tables
        for (Object db : agentDBs) {
            String path = (String) db.getClass().getMethod("getDatabasePath").invoke(db);
            assertNotNull(path);
            assertTrue(new File(path).exists(), "db file exists: " + path);

            try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + path);
                 Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='properties_table'")) {
                assertTrue(rs.next(), "properties_table exists in " + path);
            }
        }

        for (Object db : envDBs) {
            String path = (String) db.getClass().getMethod("getDatabasePath").invoke(db);
            assertNotNull(path);
            assertTrue(new File(path).exists(), "db file exists: " + path);

            try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + path);
                 Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='properties_table'")) {
                assertTrue(rs.next(), "properties_table exists in " + path);
            }
        }

        // Optional: now explicitly disconnect to ensure deletion happens
        r.disconnectAccumulatedDatabases();
        for (Object db : agentDBs) {
            String path = (String) db.getClass().getMethod("getDatabasePath").invoke(db);
            assertFalse(new File(path).exists(), "db file deleted after disconnect: " + path);
        }
    }
}
