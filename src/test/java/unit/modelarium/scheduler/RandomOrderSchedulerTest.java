package unit.modelarium.scheduler;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.scheduler.RandomOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit test for {@link RandomOrderScheduler}.
 *
 * <p>Ensures that each agent's {@code run()} method is called once per tick,
 * regardless of the random order.
 */
public class RandomOrderSchedulerTest {

    @Test
    void testRunTickCallsRunOnAllAgentsOnce() {
        // Arrange: create mock agents
        Agent agent1 = mock(Agent.class);
        Agent agent2 = mock(Agent.class);
        Agent agent3 = mock(Agent.class);

        List<Agent> mockAgentList = Arrays.asList(agent1, agent2, agent3);

        AgentSet agentSet = mock(AgentSet.class);
        Iterator<Agent> mockIterator = mock(Iterator.class);

        // Setup a mocked iterator that returns the agents one by one
        when(agentSet.getRandomIterator()).thenReturn(mockIterator);
        when(mockIterator.hasNext()).thenReturn(true, true, true, false);
        when(mockIterator.next()).thenReturn(agent1, agent2, agent3);

        RandomOrderScheduler scheduler = new RandomOrderScheduler();

        // Act
        scheduler.runTick(agentSet);

        // Assert: run() called exactly once on each
        verify(agent1, times(1)).run();
        verify(agent2, times(1)).run();
        verify(agent3, times(1)).run();
    }
}
