package unit.modelarium.agents;

import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.agents.generators.FunctionalAgentGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link FunctionalAgentGenerator}.
 *
 * <p>Verifies that agents are generated correctly and distributed across cores.</p>
 */
public class FunctionalAgentGeneratorTest {

    private final AtomicInteger idGenerator = new AtomicInteger();

    /**
     * Creates a generator function that returns uniquely named mock agents.
     */
    private Function<Config, Agent> createUniqueMockAgentGenerator() {
        return s -> {
            Agent mockAgent = mock(Agent.class);
            when(mockAgent.name()).thenReturn("agent" + idGenerator.getAndIncrement());
            return mockAgent;
        };
    }

    @Test
    void testGenerateAgentsCreatesCorrectNumber() {
        // Arrange
        Config settings = mock(Config.class);
        when(settings.getNumOfAgents()).thenReturn(5);

        FunctionalAgentGenerator generator = new FunctionalAgentGenerator(createUniqueMockAgentGenerator());

        // Act
        AgentSet result = generator.generateAgents(settings);

        // Assert
        assertEquals(5, result.size(), "Expected 5 agents in the generated AgentSet");
    }

    @Test
    void testGetAgentsForEachCoreDistributesEvenly() {
        // Arrange
        Config settings = mock(Config.class);
        when(settings.getNumOfAgents()).thenReturn(4);
        when(settings.getNumOfCores()).thenReturn(2);

        FunctionalAgentGenerator generator = new FunctionalAgentGenerator(createUniqueMockAgentGenerator());

        // Act
        List<AgentSet> coreAssignments = generator.getAgentsForEachCore(settings);

        // Assert
        assertEquals(2, coreAssignments.size(), "Expected 2 cores");
        assertEquals(2, coreAssignments.get(0).size(), "Expected 2 agents per core");
        assertEquals(2, coreAssignments.get(1).size(), "Expected 2 agents per core");
    }

    @Test
    void testZeroCoresReturnsEmptyList() {
        // Arrange
        Config settings = mock(Config.class);
        when(settings.getNumOfAgents()).thenReturn(3);
        when(settings.getNumOfCores()).thenReturn(0);

        FunctionalAgentGenerator generator = new FunctionalAgentGenerator(createUniqueMockAgentGenerator());

        // Act
        List<AgentSet> result = generator.getAgentsForEachCore(settings);

        // Assert
        assertTrue(result.isEmpty(), "Expected an empty list when core count is zero");
    }

    @Test
    void testOneCoreReturnsAllAgentsInSingleSet() {
        // Arrange
        Config settings = mock(Config.class);
        when(settings.getNumOfAgents()).thenReturn(3);
        when(settings.getNumOfCores()).thenReturn(1);

        FunctionalAgentGenerator generator = new FunctionalAgentGenerator(createUniqueMockAgentGenerator());

        // Act
        List<AgentSet> result = generator.getAgentsForEachCore(settings);

        // Assert
        assertEquals(1, result.size(), "Expected only one core");
        assertEquals(3, result.get(0).size(), "Expected all agents in a single AgentSet");
    }
}
