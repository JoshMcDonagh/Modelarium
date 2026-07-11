package unit.modelarium.entities.immutable;

import modelarium.entities.agents.AgentSet;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static unit.modelarium.entities.immutable.ImmutableEntityTestHelpers.emptyAgent;

public class ImmutableAgentSetTest {
    @Test
    public void testGetByName() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A")));
        ImmutableAgentSet immutableAgentSet = agentSet.getAsImmutable();

        ImmutableAgent immutableAgent = immutableAgentSet.get("A");

        assertEquals("A", immutableAgent.name());
    }

    @Test
    public void testGetByIndex() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B")));
        ImmutableAgentSet immutableAgentSet = agentSet.getAsImmutable();

        assertEquals("A", immutableAgentSet.get(0).name());
        assertEquals("B", immutableAgentSet.get(1).name());
    }

    @Test
    public void testIterator() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B")));
        ImmutableAgentSet immutableAgentSet = agentSet.getAsImmutable();

        int agentCount = 0;
        for (ImmutableAgent immutableAgent : immutableAgentSet)
            agentCount++;

        assertEquals(2, agentCount);
    }
}
