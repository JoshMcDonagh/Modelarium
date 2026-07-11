package unit.modelarium.scheduler;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.scheduler.RandomOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.SplittableRandom;

import static org.mockito.Mockito.*;
import static unit.modelarium.scheduler.SchedulerTestHelpers.immutableClock;
import static unit.modelarium.scheduler.SchedulerTestHelpers.immutableEnvironment;

public class RandomOrderSchedulerTest {
    @Test
    public void testRunTick_RunsAllAgents() {
        Agent firstAgent = mock(Agent.class);
        Agent secondAgent = mock(Agent.class);
        when(firstAgent.name()).thenReturn("a1");
        when(secondAgent.name()).thenReturn("a2");
        AgentSet agentSet = new AgentSet(List.of(firstAgent, secondAgent));

        new RandomOrderScheduler().runTick("0", immutableClock(), immutableEnvironment(), agentSet, new SplittableRandom(42));

        verify(firstAgent, times(1)).run();
        verify(secondAgent, times(1)).run();
    }
}
