package unit.modelarium.scheduler;

import modelarium.clock.ImmutableClock;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.scheduler.FunctionalScheduler;
import modelarium.scheduler.InOrderScheduler;
import modelarium.scheduler.RandomOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the scheduler implementations.
 */
public class SchedulerTest {

    private ImmutableClock dummyClock() {
        return new ImmutableClock(new MutableClock(10));
    }

    private ImmutableEnvironment dummyEnvironment() {
        return mock(ImmutableEnvironment.class);
    }

    // ---- InOrderScheduler ----

    @Test
    void inOrder_callsRunOnEachAgentSequentially() {
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

        new InOrderScheduler().runTick(dummyClock(), dummyEnvironment(), set);

        org.mockito.InOrder inOrder = inOrder(a1, a2, a3);
        inOrder.verify(a1).run();
        inOrder.verify(a2).run();
        inOrder.verify(a3).run();
    }

    // ---- RandomOrderScheduler ----

    @Test
    void randomOrder_callsRunOnAllAgents() {
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

    // ---- FunctionalScheduler ----

    @Test
    void functional_delegatesToProvidedConsumer() {
        AtomicInteger callCount = new AtomicInteger(0);
        FunctionalScheduler scheduler = new FunctionalScheduler(agents -> callCount.incrementAndGet());

        AgentSet set = new AgentSet();
        scheduler.runTick(dummyClock(), dummyEnvironment(), set);

        assertEquals(1, callCount.get(), "Consumer should have been called exactly once.");
    }

    @Test
    void functional_receivesCorrectAgentSet() {
        Agent a = mock(Agent.class);
        when(a.name()).thenReturn("a");
        AgentSet set = new AgentSet(List.of(a));

        List<AgentSet> captured = new ArrayList<>();
        FunctionalScheduler scheduler = new FunctionalScheduler(captured::add);
        scheduler.runTick(dummyClock(), dummyEnvironment(), set);

        assertSame(set, captured.get(0), "Consumer should receive the original agent set.");
    }
}
