package unit.modelarium.scheduler;

import modelarium.clock.ImmutableClock;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.mockito.Mockito.*;

public class InOrderSchedulerTest {

    private ImmutableClock dummyClock() {
        return new ImmutableClock(new MutableClock(10));
    }

    private ImmutableEnvironment dummyEnvironment() {
        return mock(ImmutableEnvironment.class);
    }

    @Test
    void callsRunOnEachAgentSequentially() {
        Agent a1 = mock(Agent.class);
        Agent a2 = mock(Agent.class);
        Agent a3 = mock(Agent.class);

        when(a1.name()).thenReturn("a1");
        when(a2.name()).thenReturn("a2");
        when(a3.name()).thenReturn("a3");

        AgentSet set = new AgentSet();
        set.add(a1);
        set.add(a2);
        set.add(a3);

        new InOrderScheduler().runTick("0", dummyClock(), dummyEnvironment(), set, new SplittableRandom(42));

        org.mockito.InOrder inOrder = inOrder(a1, a2, a3);
        inOrder.verify(a1).run();
        inOrder.verify(a2).run();
        inOrder.verify(a3).run();
    }
}
