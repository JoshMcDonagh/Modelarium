package unit.modelarium.agents;

import modelarium.ModelConfig;
import modelarium.agents.Agent;
import modelarium.agents.AgentSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DefaultAgentGenerator} class.
 * Verifies correct generation of agents with unique names and deep-copied attributes.
 */
public class DefaultAgentGeneratorTest {

    private ModelConfig settings;
    private DefaultAgentGenerator generator;

    @BeforeEach
    public void setUp() throws Exception {
        // Reset static counter via reflection
        java.lang.reflect.Field agentCountField = DefaultAgentGenerator.class.getDeclaredField("agentCount");
        agentCountField.setAccessible(true);
        agentCountField.setInt(null, 0); // static field, so pass null as instance

        settings = new ModelConfig();
        settings.setBaseAgentAttributeSetCollection(new AttributeSetCollection());
        settings.setNumOfAgents(5);
        settings.setNumOfCores(2);
        generator = new DefaultAgentGenerator();
    }

    @Test
    public void testGenerateSingleAgent() {
        Agent agent = generator.generateAgent(settings);

        assertNotNull(agent, "Generated agent should not be null.");
        assertTrue(agent.name().startsWith("Agent_"), "Agent name should follow the 'Agent_X' format.");
        assertNotNull(agent.getAttributeSetCollection(), "Agent should have an attribute set collection.");
    }

    @Test
    public void testGenerateMultipleAgents() {
        AgentSet agents = generator.generateAgents(settings);

        assertEquals(5, agents.size(), "Generated agent set should contain 5 agents.");
        for (int i = 0; i < 5; i++) {
            assertTrue(agents.doesAgentExist("Agent_" + i), "Agent_" + i + " should exist in the set.");
        }
    }

    @Test
    public void testAgentAttributeSetsAreDeepCopied() {
        Agent first = generator.generateAgent(settings);
        Agent second = generator.generateAgent(settings);

        // Each agent should have a different AttributeSetCollection instance
        assertNotSame(
                first.getAttributeSetCollection(),
                second.getAttributeSetCollection(),
                "Agents should not share the same AttributeSetCollection instance."
        );
    }

    @Test
    public void testGetAgentsForEachCoreDistributesCorrectly() {
        List<AgentSet> agentSets = generator.getAgentsForEachCore(settings);

        assertEquals(2, agentSets.size(), "There should be two sets of agents (one per core).");

        int totalAgents = agentSets.stream().mapToInt(AgentSet::size).sum();
        assertEquals(5, totalAgents, "Total number of agents across cores should match model settings.");
    }

    @Test
    public void testGetAgentsForEachCoreIsEmptyWithZeroCores() {
        settings.setNumOfCores(0);
        List<AgentSet> agentSets = generator.getAgentsForEachCore(settings);

        assertTrue(agentSets.isEmpty(), "Agent sets should be empty if zero cores are configured.");
    }
}
