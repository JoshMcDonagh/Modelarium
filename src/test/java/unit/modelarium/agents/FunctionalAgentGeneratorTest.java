package unit.modelarium.agents;

import modelarium.ModelSettings;
import modelarium.agents.Agent;
import modelarium.agents.AgentSet;
import modelarium.agents.FunctionalAgentGenerator;
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
    private Function<ModelSettings, Agent> createUniqueMockAgentGenerator() {
        return s -> {
            Agent mockAgent = mock(Agent.class);
            when(mockAgent.name()).thenReturn("agent" + idGenerator.getAndIncrement());
            return mockAgent;
        };
    }

    @Test
    void testGenerateAgentsCreatesCorrectNumber() {
        // Arrange
        ModelSettings settings = mock(ModelSettings.class);
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
        ModelSettings settings = mock(ModelSettings.class);
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
        ModelSettings settings = mock(ModelSettings.class);
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
        ModelSettings settings = mock(ModelSettings.class);
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
