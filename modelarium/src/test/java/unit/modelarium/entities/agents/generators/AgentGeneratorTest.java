package unit.modelarium.entities.agents.generators;

import modelarium.Config;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.generators.FunctionalDefaultAgentGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static unit.modelarium.entities.agents.generators.AgentGeneratorTestHelpers.*;

public class AgentGeneratorTest {
    @Test
    public void testGenerateAgents() {
        Config config = syncedConfig(10, 5, 2);
        DefaultAgentGenerator generator = agentGenerator();

        MutableAgentSet agentSet = generator.generateAgents(config, new SplittableRandom());

        assertEquals(10, agentSet.size());
    }

    @Test
    public void testGetAgentsForEachCore_EvenDistribution() {
        Config config = syncedConfig(9, 5, 3);
        DefaultAgentGenerator generator = agentGenerator();

        List<MutableAgentSet> agentSetsForEachCore = generator.getAgentsForEachCore(config, new SplittableRandom());

        assertEquals(3, agentSetsForEachCore.size());
        assertEquals(3, agentSetsForEachCore.get(0).size());
        assertEquals(3, agentSetsForEachCore.get(1).size());
        assertEquals(3, agentSetsForEachCore.get(2).size());
    }

    @Test
    public void testGetAgentsForEachCore_UnevenDistribution() {
        Config config = syncedConfig(10, 5, 3);
        DefaultAgentGenerator generator = agentGenerator();

        List<MutableAgentSet> agentSetsForEachCore = generator.getAgentsForEachCore(config, new SplittableRandom());

        int totalAgentCount = agentSetsForEachCore.stream().mapToInt(MutableAgentSet::size).sum();

        assertEquals(10, totalAgentCount);
    }

    @Test
    public void testGetAgentsForEachCore_SingleCore() {
        Config config = syncedConfig(5, 5, 1);
        DefaultAgentGenerator generator = agentGenerator();

        List<MutableAgentSet> agentSetsForEachCore = generator.getAgentsForEachCore(config, new SplittableRandom());

        assertEquals(1, agentSetsForEachCore.size());
        assertEquals(5, agentSetsForEachCore.get(0).size());
    }

    @Test
    public void testFunctionalDefaultAgentGenerator_DelegatesToFunction() {
        FunctionalDefaultAgentGenerator generator = new FunctionalDefaultAgentGenerator(
                (config, random) -> uniqueAgent()
        );
        Config config = syncedConfig(3, 5, 1);

        MutableAgentSet agentSet = generator.generateAgents(config, new SplittableRandom());

        assertEquals(3, agentSet.size());
    }
}
