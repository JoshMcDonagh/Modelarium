package unit.modelarium.scheduler;

import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.SplittableRandom;

import static org.mockito.Mockito.*;
import static unit.modelarium.scheduler.SchedulerTestHelpers.immutableClock;
import static unit.modelarium.scheduler.SchedulerTestHelpers.immutableEnvironment;

public class InOrderSchedulerTest {
    @Test
    public void testRunTick_RunsAgentsInOrder() {
        MutableAgent firstAgent = mock(MutableAgent.class);
        MutableAgent secondAgent = mock(MutableAgent.class);
        MutableAgent thirdAgent = mock(MutableAgent.class);
        when(firstAgent.name()).thenReturn("a1");
        when(secondAgent.name()).thenReturn("a2");
        when(thirdAgent.name()).thenReturn("a3");

        MutableAgentSet agentSet = new MutableAgentSet();
        agentSet.add(firstAgent);
        agentSet.add(secondAgent);
        agentSet.add(thirdAgent);

        new InOrderScheduler().runTick("0", immutableClock(), immutableEnvironment(), agentSet, new SplittableRandom(42));

        InOrder inOrder = inOrder(firstAgent, secondAgent, thirdAgent);
        inOrder.verify(firstAgent).run();
        inOrder.verify(secondAgent).run();
        inOrder.verify(thirdAgent).run();
    }
}
