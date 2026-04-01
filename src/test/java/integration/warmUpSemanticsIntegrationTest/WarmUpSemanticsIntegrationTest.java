package integration.warmUpSemanticsIntegrationTest;

import modelarium.Model;
import modelarium.ModelSettings;
import modelarium.environments.DefaultEnvironmentGenerator;
import modelarium.results.Results;
import modelarium.scheduler.InOrderScheduler;
import integration.agentEnvironmentSyncIntegrationTest.attributes.ModelAttributes;
import integration.agentEnvironmentSyncIntegrationTest.results.ModelResults;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WarmUpSemanticsIntegrationTest {

    private ModelSettings baseSettings(int warmUpTicks) {
        ModelSettings s = new ModelSettings();
        s.setNumOfAgents(50);
        s.setNumOfCores(2);

        s.setNumOfTicksToRun(20);       // after warm-up
        s.setNumOfWarmUpTicks(warmUpTicks);

        s.setAreProcessesSynced(true);
        s.setIsCacheUsed(false);
        s.setDoAgentStoresHoldAgentCopies(false);

        s.setBaseAgentAttributeSetCollection(ModelAttributes.getAgentAttributeSetCollection());
        s.setBaseEnvironmentAttributeSetCollection(ModelAttributes.getEnvironmentAttributeSetCollection());

        s.setResultsClass(ModelResults.class);
        s.setResults(new ModelResults());

        s.setAgentGenerator(new DefaultAgentGenerator());
        s.setEnvironmentGenerator(new DefaultEnvironmentGenerator());
        s.setModelScheduler(new InOrderScheduler());

        // Run in-memory or on-disk is irrelevant to the semantics, but feel free to toggle.
        s.setAreAttributeSetResultsStoredOnDisk(false);
        return s;
    }

    @Test
    public void testWarmUpShiftsRecordedTicks() throws Exception {
        ModelSettings sA = baseSettings(0);
        ModelSettings sB = baseSettings(10);

        Results rA = new Model(sA).run();
        Results rB = new Model(sB).run();

        List<Object> ticksA = rA.getAccumulatedEnvironmentPropertyValues("climate", "EnvTick");
        List<Object> ticksB = rB.getAccumulatedEnvironmentPropertyValues("climate", "EnvTick");

        assertEquals(sA.getNumOfTicksToRun(), ticksA.size(), "A: rows after warm-up");
        assertEquals(sB.getNumOfTicksToRun(), ticksB.size(), "B: rows after warm-up");

        double firstA = ((Number) ticksA.get(0)).doubleValue();
        double firstB = ((Number) ticksB.get(0)).doubleValue();

        // If your model clock counts absolute ticks, warm-up should shift the recorded tick values.
        // If you later decide the clock should reset post-warm-up, this assertion will intentionally fail,
        // forcing you to update the spec in one place.
        assertEquals(10.0, firstB - firstA, 1e-9, "warm-up should shift first recorded tick");

        // Tick should increment by 1 per recorded row
        for (int i = 1; i < ticksA.size(); i++) {
            double prev = ((Number) ticksA.get(i - 1)).doubleValue();
            double cur  = ((Number) ticksA.get(i)).doubleValue();
            assertEquals(1.0, cur - prev, 1e-9, "A: tick increments by 1 at i=" + i);
        }
        for (int i = 1; i < ticksB.size(); i++) {
            double prev = ((Number) ticksB.get(i - 1)).doubleValue();
            double cur  = ((Number) ticksB.get(i)).doubleValue();
            assertEquals(1.0, cur - prev, 1e-9, "B: tick increments by 1 at i=" + i);
        }
    }
}
