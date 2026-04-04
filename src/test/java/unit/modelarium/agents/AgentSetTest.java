package unit.modelarium.agents;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AgentSet} class.
 * Verifies expected behaviour in both deep-copy and reference modes.
 */
public class AgentSetTest {

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
        AgentSet agentSet = new AgentSet();
        agentSet.add(agentA);

        assertEquals(1, agentSet.size(), "Agent set should contain one agent.");
        assertSame(agentA, agentSet.get("A"), "Retrieved agent should be the same instance.");
    }

    @Test
    public void testAddListOfAgents() {
        AgentSet agentSet = new AgentSet();
        agentSet.add(Arrays.asList(agentA, agentB));

        assertEquals(2, agentSet.size(), "Agent set should contain two agents.");
        assertTrue(agentSet.doesAgentExist("A"), "Agent A should exist.");
        assertTrue(agentSet.doesAgentExist("B"), "Agent B should exist.");
    }

    @Test
    public void testAddAgentSet() {
        AgentSet sourceSet = new AgentSet(Arrays.asList(agentA, agentB));
        AgentSet targetSet = new AgentSet();
        targetSet.add(sourceSet);

        assertEquals(2, targetSet.size(), "Target set should have agents copied from source set.");
    }

    @Test
    public void testReplaceExistingAgent() {
        AgentSet agentSet = new AgentSet();
        agentSet.add(agentA);
        agentSet.add(new Agent("A", new AttributeSetCollection()));

        assertEquals(1, agentSet.size(), "Agent should be replaced, not duplicated.");
    }

    @Test
    public void testClearAgentSet() {
        AgentSet agentSet = new AgentSet(Arrays.asList(agentA, agentB));
        agentSet.clear();

        assertEquals(0, agentSet.size(), "Agent set should be empty after clear.");
    }

    @Test
    public void testGetFilteredAgents() {
        AgentSet agentSet = new AgentSet(Arrays.asList(agentA, agentB, agentC));
        AgentSet filtered = agentSet.getFilteredAgents(a -> a.name().equals("B"));

        assertEquals(1, filtered.size(), "Only one agent should match the filter.");
        assertEquals("B", filtered.get(0).name(), "Filtered agent should be B.");
    }

    @Test
    public void testGetRandomIteratorShufflesAgents() {
        AgentSet agentSet = new AgentSet(Arrays.asList(agentA, agentB, agentC));
        Set<String> names = new HashSet<>();
        Iterator<Agent> iterator = agentSet.getRandomIterator();

        while (iterator.hasNext())
            names.add(iterator.next().name());

        assertEquals(3, names.size(), "Random iterator should cover all agents.");
        assertTrue(names.containsAll(Arrays.asList("A", "B", "C")), "All agent names should be included.");
    }

    @Test
    public void testDuplicateWithCloneDisabled() {
        AgentSet agentSet = new AgentSet(false);
        agentSet.add(agentA);
        AgentSet duplicate = agentSet.duplicate();

        assertEquals(1, duplicate.size(), "Duplicate should contain same number of agents.");
        assertSame(agentA, duplicate.get(0), "Agent should be same instance if deep copy is disabled.");
    }

    @Test
    public void testDuplicateWithCloneEnabled() {
        AgentSet agentSet = new AgentSet(true);
        agentSet.add(agentA);
        AgentSet duplicate = agentSet.duplicate();

        assertEquals(1, duplicate.size(), "Duplicate should contain same number of agents.");
        assertNotSame(agentA, duplicate.get(0), "Agent should be a different instance if deep copy is enabled.");
        assertEquals(agentA.name(), duplicate.get(0).name(), "Agent name should remain the same.");
    }

    @Test
    public void testUpdateReplacesAgentsWithSameName() {
        Agent updatedAgentA = new Agent("A", new AttributeSetCollection());
        AgentSet originalSet = new AgentSet(Arrays.asList(agentA, agentB));
        AgentSet updateSet = new AgentSet(Collections.singletonList(updatedAgentA));

        originalSet.update(updateSet);

        assertSame(updatedAgentA, originalSet.get("A"), "Agent A should be updated.");
        assertSame(agentB, originalSet.get("B"), "Agent B should remain unchanged.");
    }
}
