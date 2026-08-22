package unit.modelarium.scheduler;

import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.scheduler.functional.FunctionalScheduler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static unit.modelarium.scheduler.SchedulerTestHelpers.immutableClock;
import static unit.modelarium.scheduler.SchedulerTestHelpers.immutableEnvironment;

public class FunctionalSchedulerTest {
    @Test
    public void testRunTick_DelegatesToFunction() {
        AtomicInteger callCount = new AtomicInteger(0);
        FunctionalScheduler scheduler = new FunctionalScheduler(
                (threadName, clock, environment, agents, randomGenerator) -> callCount.incrementAndGet()
        );
        AgentSet agentSet = new AgentSet();

        scheduler.runTick("0", immutableClock(), immutableEnvironment(), agentSet, new SplittableRandom(42));

        assertEquals(1, callCount.get());
    }

    @Test
    public void testRunTick_PassesAgentSetToFunction() {
        Agent agent = mock(Agent.class);
        when(agent.name()).thenReturn("a");
        AgentSet agentSet = new AgentSet(List.of(agent));

        List<AgentSet> capturedAgentSets = new ArrayList<>();
        FunctionalScheduler scheduler = new FunctionalScheduler(
                (threadName, clock, environment, agents, randomGenerator) -> capturedAgentSets.add(agents)
        );

        scheduler.runTick("0", immutableClock(), immutableEnvironment(), agentSet, new SplittableRandom(42));

        assertSame(agentSet, capturedAgentSets.get(0));
    }
}
