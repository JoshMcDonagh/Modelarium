package unit.modelarium.scheduler;

import modelarium.clock.ImmutableClock;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.scheduler.RandomOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RandomOrderSchedulerTest {

    private ImmutableClock dummyClock() {
        return new ImmutableClock(new MutableClock(10));
    }

    private ImmutableEnvironment dummyEnvironment() {
        return mock(ImmutableEnvironment.class);
    }

    @Test
    void callsRunOnAllAgents() {
        Agent a1 = mock(Agent.class);
        Agent a2 = mock(Agent.class);

        when(a1.name()).thenReturn("a1");
        when(a2.name()).thenReturn("a2");

        // The RandomOrderScheduler uses agentSet.getRandomIterator(),
        // which returns a shuffled iterator over the underlying list.
        // We need a real AgentSet to test this properly.
        AgentSet set = new AgentSet(List.of(a1, a2));

        new RandomOrderScheduler().runTick(dummyClock(), dummyEnvironment(), set);

        verify(a1, times(1)).run();
        verify(a2, times(1)).run();
    }
}
