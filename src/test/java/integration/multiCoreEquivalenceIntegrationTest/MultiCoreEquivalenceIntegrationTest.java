package integration.multiCoreEquivalenceIntegrationTest;

import modelarium.Model;
import modelarium.ModelSettings;
import modelarium.agents.DefaultAgentGenerator;
import modelarium.attributes.results.databases.AttributeSetRunLogDatabaseFactory;
import modelarium.environments.DefaultEnvironmentGenerator;
import modelarium.results.Results;
import modelarium.scheduler.InOrderScheduler;
import integration.syncedCachedBasicModelUsageIntegrationTest.attributes.ModelAttributes;
import integration.syncedCachedBasicModelUsageIntegrationTest.results.ModelResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MultiCoreEquivalenceIntegrationTest {

    private ModelSettings base;

    @BeforeEach
    public void setup() {
        AttributeSetRunLogDatabaseFactory.clearCustomFactory();
        AttributeSetRunLogDatabaseFactory.setDatabaseToMemoryBased();

        base = new ModelSettings();
        base.setNumOfAgents(200);
        base.setNumOfTicksToRun(60);
        base.setNumOfWarmUpTicks(10);

        base.setBaseAgentAttributeSetCollection(ModelAttributes.getAgentAttributeSetCollection());
        base.setBaseEnvironmentAttributeSetCollection(ModelAttributes.getEnvironmentAttributeSetCollection());

        base.setAreProcessesSynced(true); // keep coordinator path active
        base.setIsCacheUsed(false);
        base.setDoAgentStoresHoldAgentCopies(false);

        base.setResultsClass(ModelResults.class);
        base.setResults(new ModelResults());

        base.setAgentGenerator(new DefaultAgentGenerator());
        base.setEnvironmentGenerator(new DefaultEnvironmentGenerator());
        base.setModelScheduler(new InOrderScheduler());

        base.setAreAttributeSetResultsStoredOnDisk(false);
    }

    @Test
    public void testSingleCoreMatchesMultiCore() throws Exception {
        ModelSettings s1 = base;
        s1.setNumOfCores(1);
        Results r1 = new Model(s1).run();
        List<Object> h1 = r1.getAccumulatedAgentPropertyValues("food", "Hunger");

        ModelSettings s4 = new ModelSettings();
        // clone key fields manually (ModelSettings has no deepCopy)
        s4.setNumOfAgents(base.getNumOfAgents());
        s4.setNumOfTicksToRun(base.getNumOfTicksToRun());
        s4.setNumOfWarmUpTicks(base.getNumOfWarmUpTicks());
        s4.setBaseAgentAttributeSetCollection(base.getBaseAgentAttributeSetCollection());
        s4.setBaseEnvironmentAttributeSetCollection(base.getBaseEnvironmentAttributeSetCollection());
        s4.setAreProcessesSynced(true);
        s4.setIsCacheUsed(false);
        s4.setDoAgentStoresHoldAgentCopies(false);
        s4.setResultsClass(ModelResults.class);
        s4.setResults(new ModelResults());
        s4.setAgentGenerator(new DefaultAgentGenerator());
        s4.setEnvironmentGenerator(new DefaultEnvironmentGenerator());
        s4.setModelScheduler(new InOrderScheduler());
        s4.setAreAttributeSetResultsStoredOnDisk(false);

        s4.setNumOfCores(4);
        Results r4 = new Model(s4).run();
        List<Object> h4 = r4.getAccumulatedAgentPropertyValues("food", "Hunger");

        assertEquals(h1.size(), h4.size());

        for (int i = 0; i < h1.size(); i++) {
            double a = ((Number) h1.get(i)).doubleValue();
            double b = ((Number) h4.get(i)).doubleValue();
            assertEquals(a, b, 1e-9, "same aggregated hunger at i=" + i);
        }
    }
}
