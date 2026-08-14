package unit.modelarium.scheduler;

import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.mutable.MutableAgentSet;
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
        MutableAgent firstAgent = mock(MutableAgent.class);
        MutableAgent secondAgent = mock(MutableAgent.class);
        when(firstAgent.name()).thenReturn("a1");
        when(secondAgent.name()).thenReturn("a2");
        MutableAgentSet agentSet = new MutableAgentSet(List.of(firstAgent, secondAgent));

        new RandomOrderScheduler().runTick("0", immutableClock(), immutableEnvironment(), agentSet, new SplittableRandom(42));

        verify(firstAgent, times(1)).run();
        verify(secondAgent, times(1)).run();
    }
}
