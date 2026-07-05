package unit.modelarium.scheduler;

import modelarium.clock.ImmutableClock;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.scheduler.functional.FunctionalScheduler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FunctionalSchedulerTest {

    private ImmutableClock dummyClock() {
        return new ImmutableClock(new MutableClock(10));
    }

    private ImmutableEnvironment dummyEnvironment() {
        return mock(ImmutableEnvironment.class);
    }

    @Test
    void delegatesToProvidedConsumer() {
        AtomicInteger callCount = new AtomicInteger(0);
        FunctionalScheduler scheduler = new FunctionalScheduler(agents -> callCount.incrementAndGet());

        AgentSet set = new AgentSet();
        scheduler.runTick(dummyClock(), dummyEnvironment(), set);

        assertEquals(1, callCount.get(), "Consumer should have been called exactly once.");
    }

    @Test
    void receivesCorrectAgentSet() {
        Agent a = mock(Agent.class);
        when(a.name()).thenReturn("a");
        AgentSet set = new AgentSet(List.of(a));

        List<AgentSet> captured = new ArrayList<>();
        FunctionalScheduler scheduler = new FunctionalScheduler(captured::add);
        scheduler.runTick(dummyClock(), dummyEnvironment(), set);

        assertSame(set, captured.get(0), "Consumer should receive the original agent set.");
    }
}
