package unit.modelarium.entities.immutable;

import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.agents.immutable.ImmutableAgent;
import modelarium.entities.agents.immutable.ImmutableAgentSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static unit.modelarium.entities.immutable.ImmutableEntityTestHelpers.emptyAgent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SplittableRandom;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ImmutableAgentSetTest {
    @Test
    public void testGetByName() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A")));
        ImmutableAgentSet immutableAgentSet = agentSet.getAsImmutable();

        ImmutableAgent immutableAgent = immutableAgentSet.get("A");

        assertEquals("A", immutableAgent.name());
    }

    @Test
    public void testGetByIndex() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A"), emptyAgent("B")));
        ImmutableAgentSet immutableAgentSet = agentSet.getAsImmutable();

        assertEquals("A", immutableAgentSet.get(0).name());
        assertEquals("B", immutableAgentSet.get(1).name());
    }

    @Test
    public void testIterator() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A"), emptyAgent("B")));
        ImmutableAgentSet immutableAgentSet = agentSet.getAsImmutable();

        int agentCount = 0;
        for (ImmutableAgent immutableAgent : immutableAgentSet)
            agentCount++;

        assertEquals(2, agentCount);
    }


    @Test
    public void testIsEmpty_True() {
        ImmutableAgentSet immutableAgentSet = new MutableAgentSet().getAsImmutable();

        assertTrue(immutableAgentSet.isEmpty());
    }

    @Test
    public void testIsEmpty_False() {
        ImmutableAgentSet immutableAgentSet = new MutableAgentSet(List.of(emptyAgent("A"))).getAsImmutable();

        assertFalse(immutableAgentSet.isEmpty());
    }

    @Test
    public void testDoesAgentExist_True() {
        ImmutableAgentSet immutableAgentSet = new MutableAgentSet(List.of(emptyAgent("A"))).getAsImmutable();

        assertTrue(immutableAgentSet.doesAgentExist("A"));
    }

    @Test
    public void testDoesAgentExist_False() {
        ImmutableAgentSet immutableAgentSet = new MutableAgentSet(List.of(emptyAgent("A"))).getAsImmutable();

        assertFalse(immutableAgentSet.doesAgentExist("B"));
    }

    @Test
    public void testGetFilteredAgents() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("keep_0"), emptyAgent("keep_1"), emptyAgent("drop_0")));
        ImmutableAgentSet immutableAgentSet = agentSet.getAsImmutable();

        ImmutableAgentSet filtered = immutableAgentSet.getFilteredAgents(agent -> agent.name().startsWith("keep"));

        List<String> names = new ArrayList<>();
        for (ImmutableAgent immutableAgent : filtered)
            names.add(immutableAgent.name());

        assertEquals(2, names.size());
        assertTrue(names.contains("keep_0"));
        assertTrue(names.contains("keep_1"));
        assertFalse(names.contains("drop_0"));
    }

    @Test
    public void testGetRandomIterator() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A"), emptyAgent("B"), emptyAgent("C")));
        ImmutableAgentSet immutableAgentSet = agentSet.getAsImmutable();

        Iterator<ImmutableAgent> randomIterator = immutableAgentSet.getRandomIterator(new SplittableRandom(42));

        List<String> names = new ArrayList<>();
        while (randomIterator.hasNext())
            names.add(randomIterator.next().name());

        assertEquals(3, names.size());
        assertTrue(names.contains("A"));
        assertTrue(names.contains("B"));
        assertTrue(names.contains("C"));
    }
}
