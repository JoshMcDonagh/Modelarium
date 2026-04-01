package integration.schedulerInvarianceIntegrationTest;

import modelarium.Model;
import modelarium.Config;
import modelarium.logging.databases.AttributeSetRunLogDatabaseFactory;
import modelarium.results.Results;
import modelarium.scheduler.InOrderScheduler;
import modelarium.scheduler.RandomOrderScheduler;
import integration.syncedCachedBasicModelUsageIntegrationTest.attributes.ModelAttributes;
import integration.syncedCachedBasicModelUsageIntegrationTest.results.ModelResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SchedulerInvarianceIntegrationTest {

    private Config base;

    @BeforeEach
    public void setup() {
        AttributeSetRunLogDatabaseFactory.clearCustomFactory();
        AttributeSetRunLogDatabaseFactory.setDatabaseToMemoryBased();

        base = new Config();
        base.setNumOfAgents(100);
        base.setNumOfCores(4);
        base.setNumOfTicksToRun(40);
        base.setNumOfWarmUpTicks(10);

        base.setBaseAgentAttributeSetCollection(ModelAttributes.getAgentAttributeSetCollection());
        base.setBaseEnvironmentAttributeSetCollection(ModelAttributes.getEnvironmentAttributeSetCollection());

        base.setAreProcessesSynced(true);
        base.setDoAgentStoresHoldAgentCopies(false);
        base.setIsCacheUsed(false);

        base.setResultsClass(ModelResults.class);
        base.setResults(new ModelResults());

        base.setAgentGenerator(new DefaultAgentGenerator());
        base.setEnvironmentGenerator(new DefaultEnvironmentGenerator());
        base.setAreAttributeSetResultsStoredOnDisk(false);
    }

    private void assertHungerSeriesOK(Results results, int numAgents, int expectedRows) {
        List<Object> hunger = results.getAccumulatedAgentPropertyValues("food", "Hunger");
        assertEquals(expectedRows, hunger.size());

        for (int t = 0; t < hunger.size(); t++) {
            double v = ((Number) hunger.get(t)).doubleValue();
            assertTrue(Double.isFinite(v), "finite at t=" + t);
            assertTrue(v >= 0.0, "non-negative at t=" + t);
            assertTrue(v <= numAgents + 1e-9, "bounded by numAgents at t=" + t);
        }
    }

    @Test
    public void testInOrderSchedulerInvariant() throws Exception {
        base.setModelScheduler(new InOrderScheduler());
        Results r = new Model(base).run();
        assertHungerSeriesOK(r, base.getNumOfAgents(), base.getNumOfTicksToRun());
    }

    @Test
    public void testRandomOrderSchedulerInvariant() throws Exception {
        base.setModelScheduler(new RandomOrderScheduler());
        Results r = new Model(base).run();
        assertHungerSeriesOK(r, base.getNumOfAgents(), base.getNumOfTicksToRun());
    }
}
