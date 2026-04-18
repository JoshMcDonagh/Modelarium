package unit.modelarium.scheduler;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.scheduler.FunctionalScheduler;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

/**
 * Unit test for {@link FunctionalScheduler}.
 *
 * <p>Ensures that the user-defined tick function is executed correctly
 * and that each agent's {@code run()} method is called once during the tick.</p>
 */
public class FunctionalSchedulerTest {

    @Test
    void testRunTickExecutesCustomTickFunction() {
        // Arrange: create mock agents
        Agent agent1 = mock(Agent.class);
        Agent agent2 = mock(Agent.class);

        Iterator<Agent> mockIterator = mock(Iterator.class);
        MutableAgentSet agentSet = mock(MutableAgentSet.class);

        // Set up the iterator to return the agents
        when(agentSet.iterator()).thenReturn(mockIterator);
        when(mockIterator.hasNext()).thenReturn(true, true, false);
        when(mockIterator.next()).thenReturn(agent1, agent2);

        // Define a tick function that runs each agent in order
        Consumer<MutableAgentSet> tickFunction = set -> {
            for (Agent agent : set) {
                agent.run();
            }
        };

        FunctionalScheduler scheduler = new FunctionalScheduler(tickFunction);

        // Act
        scheduler.runTick(agentSet);

        // Assert: each agent's run() should be called once
        verify(agent1, times(1)).run();
        verify(agent2, times(1)).run();
    }
}
