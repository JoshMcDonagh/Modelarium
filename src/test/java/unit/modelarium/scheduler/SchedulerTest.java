package unit.modelarium.scheduler;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.scheduler.Scheduler;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * Unit test for {@link Scheduler}.
 *
 * <p>Includes a simple implementation of the scheduler for test purposes.
 */
public class SchedulerTest {

    /**
     * A basic scheduler implementation that simply steps every agent.
     */
    private static class BasicScheduler implements Scheduler {
        @Override
        public void runTick(AgentSet agentSet) {
            for (Agent agent : agentSet)
                agent.run();
        }
    }

    @Test
    void testRunTickCallsStepOnAllAgents() {
        Agent mockAgent1 = mock(Agent.class);
        Agent mockAgent2 = mock(Agent.class);

        // Stub getName() to avoid indexing issues
        when(mockAgent1.name()).thenReturn("mock1");
        when(mockAgent2.name()).thenReturn("mock2");

        AgentSet agentSet = new AgentSet();
        agentSet.add(mockAgent1);
        agentSet.add(mockAgent2);

        Scheduler scheduler = new BasicScheduler();
        scheduler.runTick(agentSet);

        verify(mockAgent1, times(1)).run();
        verify(mockAgent2, times(1)).run();
    }
}
