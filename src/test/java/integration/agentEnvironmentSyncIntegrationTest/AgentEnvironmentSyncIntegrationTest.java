package integration.agentEnvironmentSyncIntegrationTest;

import modelarium.Model;
import modelarium.ModelConfig;
import modelarium.results.Results;
import modelarium.scheduler.InOrderScheduler; // or RandomOrderScheduler — either is fine
import integration.agentEnvironmentSyncIntegrationTest.attributes.ModelAttributes;
import integration.agentEnvironmentSyncIntegrationTest.results.ModelResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AgentEnvironmentSyncIntegrationTest {

    private ModelConfig base;

    @BeforeEach
    public void setup() {
        base = new ModelConfig();
        base.setNumOfAgents(100);
        base.setNumOfCores(4);
        base.setNumOfTicksToRun(20);
        base.setNumOfWarmUpTicks(5);
        base.setAreProcessesSynced(true);         // *** important ***
        base.setIsCacheUsed(false);               // toggled per test
        base.setDoAgentStoresHoldAgentCopies(false);

        base.setBaseAgentAttributeSetCollection(ModelAttributes.getAgentAttributeSetCollection());
        base.setBaseEnvironmentAttributeSetCollection(ModelAttributes.getEnvironmentAttributeSetCollection());

        base.setResultsClass(ModelResults.class);
        base.setResults(new ModelResults());

        base.setAgentGenerator(new DefaultAgentGenerator());
        base.setEnvironmentGenerator(new DefaultEnvironmentGenerator());
        base.setModelScheduler(new InOrderScheduler()); // deterministic; Random also OK
    }

    private void runAndAssert(ModelConfig s) throws Exception {
        Results results = new Model(s).run();

        // After run(), Results has already: setEnvironmentResults, accumulateAgentAttributeData, processEnvironmentAttributeData, seal()

        // Agent accumulated (we averaged by summing; divide by agent count for a proper average)
        List<Object> agentSeen = results.getAccumulatedAgentPropertyValues("perception", "SeenEnvTick");
        // Environment processed (identity)
        List<Object> envTick = results.getAccumulatedEnvironmentPropertyValues("climate", "EnvTick");

        assertEquals(s.getNumOfTicksToRun(), agentSeen.size());
        assertEquals(s.getNumOfTicksToRun(), envTick.size());

        // Because we summed across agents in accumulate step, divide by N to compare with env
        for (int i = 0; i < agentSeen.size(); i++) {
            double avgSeen = ((Number)agentSeen.get(i)).doubleValue() / s.getNumOfAgents();
            double env = ((Number)envTick.get(i)).doubleValue();
            assertEquals(env, avgSeen, 1e-9, "agents must see the same env tick at t=" + i);
            if (i > 0) {
                assertEquals(env - ((Number)envTick.get(i-1)).doubleValue(), 1.0, 1e-9, "env increments by 1 per tick");
            }
        }
    }

    @Test
    public void testSyncedMultiCore_NoCache() throws Exception {
        base.setIsCacheUsed(false);
        base.setAreAttributeSetResultsStoredOnDisk(false);
        runAndAssert(base);
    }

    @Test
    public void testSyncedMultiCore_WithCache() throws Exception {
        base.setIsCacheUsed(true);
        base.setAreAttributeSetResultsStoredOnDisk(true);
        runAndAssert(base);
    }
}
