package unit.modelarium.agents;

import modelarium.ModelSettings;
import modelarium.agents.Agent;
import modelarium.agents.AgentGenerator;
import modelarium.agents.AgentSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AgentGenerator}.
 * A mock subclass is used to validate behaviour of the abstract base class.
 */
public class AgentGeneratorTest {

    private TestAgentGenerator testAgentGenerator;
    private ModelSettings modelSettings;

    @BeforeEach
    public void setUp() {
        testAgentGenerator = new TestAgentGenerator();
        modelSettings = new ModelSettings();
    }

    @Test
    public void testGenerateAgents_returnsCorrectNumberOfAgents() {
        modelSettings.setNumOfAgents(5);

        AgentSet agents = testAgentGenerator.generateAgents(modelSettings);

        assertEquals(5, agents.size(), "Should generate the specified number of agents.");
        for (int i = 0; i < agents.size(); i++) {
            assertEquals("Agent_" + i, agents.get(i).name(), "Agent name should match expected pattern.");
        }
    }

    @Test
    public void testGetAgentsForEachCore_singleCore_allAgentsAssigned() {
        modelSettings.setNumOfAgents(4);
        modelSettings.setNumOfCores(1);

        List<AgentSet> cores = testAgentGenerator.getAgentsForEachCore(modelSettings);

        assertEquals(1, cores.size(), "Should return a single agent set for one core.");
        assertEquals(4, cores.get(0).size(), "All agents should be in the one agent set.");
    }

    @Test
    public void testGetAgentsForEachCore_multipleCores_evenDistribution() {
        modelSettings.setNumOfAgents(4);
        modelSettings.setNumOfCores(2);

        List<AgentSet> cores = testAgentGenerator.getAgentsForEachCore(modelSettings);

        assertEquals(2, cores.size(), "Should return one agent set per core.");
        assertEquals(2, cores.get(0).size(), "Each core should receive 2 agents.");
        assertEquals(2, cores.get(1).size(), "Each core should receive 2 agents.");
    }

    @Test
    public void testGetAgentsForEachCore_zeroCores_returnsEmptyList() {
        modelSettings.setNumOfAgents(3);
        modelSettings.setNumOfCores(0);

        List<AgentSet> cores = testAgentGenerator.getAgentsForEachCore(modelSettings);

        assertTrue(cores.isEmpty(), "Should return an empty list when zero cores are specified.");
    }

    // Simple concrete subclass of AgentGenerator for testing
    private static class TestAgentGenerator extends AgentGenerator {
        private int count = 0;

        @Override
        protected Agent generateAgent(ModelSettings modelSettings) {
            return new Agent("Agent_" + count++, new modelarium.attributes.AttributeSetCollection());
        }
    }
}
