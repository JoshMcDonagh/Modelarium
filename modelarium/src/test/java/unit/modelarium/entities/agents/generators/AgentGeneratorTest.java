package unit.modelarium.entities.agents.generators;

import modelarium.Config;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.generators.FunctionalDefaultAgentGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static unit.modelarium.entities.agents.generators.AgentGeneratorTestHelpers.*;

public class AgentGeneratorTest {
    @Test
    public void testGenerateAgents() {
        Config config = syncedConfig(10, 5, 2);
        DefaultAgentGenerator generator = agentGenerator();

        AgentSet agentSet = generator.generateAgents(config);

        assertEquals(10, agentSet.size());
    }

    @Test
    public void testGetAgentsForEachCore_EvenDistribution() {
        Config config = syncedConfig(9, 5, 3);
        DefaultAgentGenerator generator = agentGenerator();

        List<AgentSet> agentSetsForEachCore = generator.getAgentsForEachCore(config);

        assertEquals(3, agentSetsForEachCore.size());
        assertEquals(3, agentSetsForEachCore.get(0).size());
        assertEquals(3, agentSetsForEachCore.get(1).size());
        assertEquals(3, agentSetsForEachCore.get(2).size());
    }

    @Test
    public void testGetAgentsForEachCore_UnevenDistribution() {
        Config config = syncedConfig(10, 5, 3);
        DefaultAgentGenerator generator = agentGenerator();

        List<AgentSet> agentSetsForEachCore = generator.getAgentsForEachCore(config);

        int totalAgentCount = agentSetsForEachCore.stream().mapToInt(AgentSet::size).sum();

        assertEquals(10, totalAgentCount);
    }

    @Test
    public void testGetAgentsForEachCore_SingleCore() {
        Config config = syncedConfig(5, 5, 1);
        DefaultAgentGenerator generator = agentGenerator();

        List<AgentSet> agentSetsForEachCore = generator.getAgentsForEachCore(config);

        assertEquals(1, agentSetsForEachCore.size());
        assertEquals(5, agentSetsForEachCore.get(0).size());
    }

    @Test
    public void testFunctionalDefaultAgentGenerator_DelegatesToFunction() {
        FunctionalDefaultAgentGenerator generator = new FunctionalDefaultAgentGenerator(
                config -> uniqueAgent()
        );
        Config config = syncedConfig(3, 5, 1);

        AgentSet agentSet = generator.generateAgents(config);

        assertEquals(3, agentSet.size());
    }
}
