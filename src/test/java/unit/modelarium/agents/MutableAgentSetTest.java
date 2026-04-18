package unit.modelarium.agents;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.MutableAgentSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link MutableAgentSet} class.
 * Verifies expected behaviour in both deep-copy and reference modes.
 */
public class MutableAgentSetTest {

    private Agent agentA;
    private Agent agentB;
    private Agent agentC;

    @BeforeEach
    public void setUp() {
        agentA = new Agent("A", new AttributeSetCollection());
        agentB = new Agent("B", new AttributeSetCollection());
        agentC = new Agent("C", new AttributeSetCollection());
    }

    @Test
    public void testAddAndRetrieveAgent() {
        MutableAgentSet agentSet = new MutableAgentSet();
        agentSet.add(agentA);

        assertEquals(1, agentSet.size(), "Agent set should contain one agent.");
        assertSame(agentA, agentSet.get("A"), "Retrieved agent should be the same instance.");
    }

    @Test
    public void testAddListOfAgents() {
        MutableAgentSet agentSet = new MutableAgentSet();
        agentSet.add(Arrays.asList(agentA, agentB));

        assertEquals(2, agentSet.size(), "Agent set should contain two agents.");
        assertTrue(agentSet.doesAgentExist("A"), "Agent A should exist.");
        assertTrue(agentSet.doesAgentExist("B"), "Agent B should exist.");
    }

    @Test
    public void testAddAgentSet() {
        MutableAgentSet sourceSet = new MutableAgentSet(Arrays.asList(agentA, agentB));
        MutableAgentSet targetSet = new MutableAgentSet();
        targetSet.add(sourceSet);

        assertEquals(2, targetSet.size(), "Target set should have agents copied from source set.");
    }

    @Test
    public void testReplaceExistingAgent() {
        MutableAgentSet agentSet = new MutableAgentSet();
        agentSet.add(agentA);
        agentSet.add(new Agent("A", new AttributeSetCollection()));

        assertEquals(1, agentSet.size(), "Agent should be replaced, not duplicated.");
    }

    @Test
    public void testClearAgentSet() {
        MutableAgentSet agentSet = new MutableAgentSet(Arrays.asList(agentA, agentB));
        agentSet.clear();

        assertEquals(0, agentSet.size(), "Agent set should be empty after clear.");
    }

    @Test
    public void testGetFilteredAgents() {
        MutableAgentSet agentSet = new MutableAgentSet(Arrays.asList(agentA, agentB, agentC));
        MutableAgentSet filtered = agentSet.getFilteredAgents(a -> a.name().equals("B"));

        assertEquals(1, filtered.size(), "Only one agent should match the filter.");
        assertEquals("B", filtered.get(0).name(), "Filtered agent should be B.");
    }

    @Test
    public void testGetRandomIteratorShufflesAgents() {
        MutableAgentSet agentSet = new MutableAgentSet(Arrays.asList(agentA, agentB, agentC));
        Set<String> names = new HashSet<>();
        Iterator<Agent> iterator = agentSet.getRandomIterator();

        while (iterator.hasNext())
            names.add(iterator.next().name());

        assertEquals(3, names.size(), "Random iterator should cover all agents.");
        assertTrue(names.containsAll(Arrays.asList("A", "B", "C")), "All agent names should be included.");
    }

    @Test
    public void testDuplicateWithCloneDisabled() {
        MutableAgentSet agentSet = new MutableAgentSet(false);
        agentSet.add(agentA);
        MutableAgentSet duplicate = agentSet.duplicate();

        assertEquals(1, duplicate.size(), "Duplicate should contain same number of agents.");
        assertSame(agentA, duplicate.get(0), "Agent should be same instance if deep copy is disabled.");
    }

    @Test
    public void testDuplicateWithCloneEnabled() {
        MutableAgentSet agentSet = new MutableAgentSet(true);
        agentSet.add(agentA);
        MutableAgentSet duplicate = agentSet.duplicate();

        assertEquals(1, duplicate.size(), "Duplicate should contain same number of agents.");
        assertNotSame(agentA, duplicate.get(0), "Agent should be a different instance if deep copy is enabled.");
        assertEquals(agentA.name(), duplicate.get(0).name(), "Agent name should remain the same.");
    }

    @Test
    public void testUpdateReplacesAgentsWithSameName() {
        Agent updatedAgentA = new Agent("A", new AttributeSetCollection());
        MutableAgentSet originalSet = new MutableAgentSet(Arrays.asList(agentA, agentB));
        MutableAgentSet updateSet = new MutableAgentSet(Collections.singletonList(updatedAgentA));

        originalSet.update(updateSet);

        assertSame(updatedAgentA, originalSet.get("A"), "Agent A should be updated.");
        assertSame(agentB, originalSet.get("B"), "Agent B should remain unchanged.");
    }
}
