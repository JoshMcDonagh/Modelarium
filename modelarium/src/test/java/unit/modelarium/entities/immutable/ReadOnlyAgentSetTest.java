package unit.modelarium.entities.immutable;

import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.immutable.ReadOnlyAgentSet;
import modelarium.entities.agents.mutable.AgentSet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.immutable.ImmutableEntityTestHelpers.emptyAgent;

public class ReadOnlyAgentSetTest {
    @Test
    public void testGetByName() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A")));
        ReadOnlyAgentSet immutableAgentSet = agentSet.getAsImmutable();

        ReadOnlyAgent immutableAgent = immutableAgentSet.get("A");

        assertEquals("A", immutableAgent.name());
    }

    @Test
    public void testGetByIndex() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B")));
        ReadOnlyAgentSet immutableAgentSet = agentSet.getAsImmutable();

        assertEquals("A", immutableAgentSet.get(0).name());
        assertEquals("B", immutableAgentSet.get(1).name());
    }

    @Test
    public void testIterator() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B")));
        ReadOnlyAgentSet immutableAgentSet = agentSet.getAsImmutable();

        int agentCount = 0;
        for (ReadOnlyAgent immutableAgent : immutableAgentSet)
            agentCount++;

        assertEquals(2, agentCount);
    }


    @Test
    public void testIsEmpty_True() {
        ReadOnlyAgentSet immutableAgentSet = new AgentSet().getAsImmutable();

        assertTrue(immutableAgentSet.isEmpty());
    }

    @Test
    public void testIsEmpty_False() {
        ReadOnlyAgentSet immutableAgentSet = new AgentSet(List.of(emptyAgent("A"))).getAsImmutable();

        assertFalse(immutableAgentSet.isEmpty());
    }

    @Test
    public void testDoesAgentExist_True() {
        ReadOnlyAgentSet immutableAgentSet = new AgentSet(List.of(emptyAgent("A"))).getAsImmutable();

        assertTrue(immutableAgentSet.doesAgentExist("A"));
    }

    @Test
    public void testDoesAgentExist_False() {
        ReadOnlyAgentSet immutableAgentSet = new AgentSet(List.of(emptyAgent("A"))).getAsImmutable();

        assertFalse(immutableAgentSet.doesAgentExist("B"));
    }

    @Test
    public void testGetFilteredAgents() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("keep_0"), emptyAgent("keep_1"), emptyAgent("drop_0")));
        ReadOnlyAgentSet immutableAgentSet = agentSet.getAsImmutable();

        ReadOnlyAgentSet filtered = immutableAgentSet.getFilteredAgents(agent -> agent.name().startsWith("keep"));

        List<String> names = new ArrayList<>();
        for (ReadOnlyAgent immutableAgent : filtered)
            names.add(immutableAgent.name());

        assertEquals(2, names.size());
        assertTrue(names.contains("keep_0"));
        assertTrue(names.contains("keep_1"));
        assertFalse(names.contains("drop_0"));
    }

    @Test
    public void testGetRandomIterator() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B"), emptyAgent("C")));
        ReadOnlyAgentSet immutableAgentSet = agentSet.getAsImmutable();

        Iterator<ReadOnlyAgent> randomIterator = immutableAgentSet.getRandomIterator(new SplittableRandom(42));

        List<String> names = new ArrayList<>();
        while (randomIterator.hasNext())
            names.add(randomIterator.next().name());

        assertEquals(3, names.size());
        assertTrue(names.contains("A"));
        assertTrue(names.contains("B"));
        assertTrue(names.contains("C"));
    }
}
