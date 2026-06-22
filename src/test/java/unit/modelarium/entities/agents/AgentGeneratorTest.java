package unit.modelarium.entities.agents;

import helpers.TestFixtures;
import modelarium.Config;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.generators.FunctionalDefaultAgentGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DefaultAgentGenerator} and {@link FunctionalDefaultAgentGenerator}.
 */
public class AgentGeneratorTest {

    @Test
    public void testDefaultGenerator_producesCorrectPopulationSize() {
        Config config = TestFixtures.syncedConfig(10, 5, 2);
        DefaultAgentGenerator gen = TestFixtures.counterAgentGenerator();

        AgentSet agents = gen.generateAgents(config);
        assertEquals(10, agents.size(), "Should generate exactly populationSize agents.");
    }

    @Test
    public void testDefaultGenerator_distributesAcrossCoresEvenly() {
        Config config = TestFixtures.syncedConfig(9, 5, 3);
        DefaultAgentGenerator gen = TestFixtures.counterAgentGenerator();

        List<AgentSet> perCore = gen.getAgentsForEachCore(config);

        assertEquals(3, perCore.size(), "Should produce one set per core.");
        assertEquals(3, perCore.get(0).size());
        assertEquals(3, perCore.get(1).size());
        assertEquals(3, perCore.get(2).size());
    }

    @Test
    public void testDefaultGenerator_handlesUnevenDistribution() {
        // 10 agents across 3 cores: 4+3+3 (round-robin)
        Config config = TestFixtures.syncedConfig(10, 5, 3);
        DefaultAgentGenerator gen = TestFixtures.counterAgentGenerator();

        List<AgentSet> perCore = gen.getAgentsForEachCore(config);
        int total = perCore.stream().mapToInt(AgentSet::size).sum();

        assertEquals(10, total, "All agents should be distributed.");
    }

    @Test
    public void testDefaultGenerator_singleCoreGetsAll() {
        Config config = TestFixtures.syncedConfig(5, 5, 1);
        DefaultAgentGenerator gen = TestFixtures.counterAgentGenerator();

        List<AgentSet> perCore = gen.getAgentsForEachCore(config);

        assertEquals(1, perCore.size());
        assertEquals(5, perCore.get(0).size());
    }

    @Test
    public void testFunctionalGenerator_delegatesToFunction() {
        FunctionalDefaultAgentGenerator gen = new FunctionalDefaultAgentGenerator(
                config -> TestFixtures.uniqueAgent()
        );

        Config config = TestFixtures.syncedConfig(3, 5, 1);
        AgentSet agents = gen.generateAgents(config);

        assertEquals(3, agents.size());
    }
}
