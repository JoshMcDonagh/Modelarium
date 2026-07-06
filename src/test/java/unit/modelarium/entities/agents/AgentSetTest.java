package unit.modelarium.entities.agents;

import helpers.TestFixtures;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AgentSet}.
 * Covers add, retrieve, filter, iterate, duplicate, and update operations.
 */
public class AgentSetTest {

    private Agent agentA;
    private Agent agentB;
    private Agent agentC;

    @BeforeEach
    public void setUp() {
        agentA = TestFixtures.emptyAgent("A");
        agentB = TestFixtures.emptyAgent("B");
        agentC = TestFixtures.emptyAgent("C");
    }

    @Test
    public void testAddAndRetrieveAgentByName() {
        AgentSet set = new AgentSet();
        set.add(agentA);

        assertEquals(1, set.size(), "Set should contain one agent.");
        assertSame(agentA, set.get("A"), "Retrieved agent should be the same instance.");
    }

    @Test
    public void testAddAndRetrieveAgentByIndex() {
        AgentSet set = new AgentSet();
        set.add(agentA);
        set.add(agentB);

        assertSame(agentA, set.get(0));
        assertSame(agentB, set.get(1));
    }

    @Test
    public void testAddListOfAgents() {
        AgentSet set = new AgentSet();
        set.add(Arrays.asList(agentA, agentB));

        assertEquals(2, set.size(), "Set should contain two agents.");
        assertTrue(set.doesAgentExist("A"));
        assertTrue(set.doesAgentExist("B"));
    }

    @Test
    public void testConstructFromList() {
        AgentSet set = new AgentSet(Arrays.asList(agentA, agentB, agentC));
        assertEquals(3, set.size());
    }

    @Test
    public void testAddAgentSet() {
        AgentSet source = new AgentSet(Arrays.asList(agentA, agentB));
        AgentSet target = new AgentSet();
        target.add(source);

        assertEquals(2, target.size(), "Target set should contain agents from source.");
    }

    @Test
    public void testReplaceExistingAgent() {
        AgentSet set = new AgentSet();
        set.add(agentA);

        Agent replacement = TestFixtures.emptyAgent("A");
        set.add(replacement);

        assertEquals(1, set.size(), "Duplicate name should replace, not grow.");
        assertSame(replacement, set.get("A"), "Should be the replacement instance.");
    }

    @Test
    public void testDoesAgentExist() {
        AgentSet set = new AgentSet();
        set.add(agentA);

        assertTrue(set.doesAgentExist("A"));
        assertFalse(set.doesAgentExist("Z"));
    }

    @Test
    public void testGetFilteredAgents() {
        AgentSet set = new AgentSet(Arrays.asList(agentA, agentB, agentC));
        AgentSet filtered = set.getFilteredAgents(a -> a.name().equals("B"));

        assertEquals(1, filtered.size(), "Only one agent should match.");
        assertEquals("B", filtered.get(0).name());
    }

    @Test
    public void testFilterReturnsEmptySetWhenNothingMatches() {
        AgentSet set = new AgentSet(List.of(agentA));
        AgentSet filtered = set.getFilteredAgents(a -> false);

        assertEquals(0, filtered.size());
    }

    @Test
    public void testGetRandomIteratorCoversAllAgents() {
        AgentSet set = new AgentSet(Arrays.asList(agentA, agentB, agentC));
        Set<String> names = new HashSet<>();
        Iterator<Agent> it = set.getRandomIterator(new SplittableRandom(42));

        while (it.hasNext())
            names.add(it.next().name());

        assertEquals(3, names.size(), "Random iterator should visit all agents.");
        assertTrue(names.containsAll(Arrays.asList("A", "B", "C")));
    }

    @Test
    public void testGetAsList() {
        AgentSet set = new AgentSet(Arrays.asList(agentA, agentB));
        List<Agent> list = set.getAsList();

        assertEquals(2, list.size());
    }

    @Test
    public void testDuplicate() {
        AgentSet set = new AgentSet(Arrays.asList(agentA, agentB));
        AgentSet dup = set.duplicate();

        assertEquals(2, dup.size(), "Duplicate should have the same number of agents.");
    }

    @Test
    public void testIterator() {
        AgentSet set = new AgentSet(Arrays.asList(agentA, agentB));
        List<String> names = new ArrayList<>();

        for (Agent agent : set)
            names.add(agent.name());

        assertEquals(Arrays.asList("A", "B"), names);
    }

    @Test
    public void testSizeOfEmptySet() {
        AgentSet set = new AgentSet();
        assertEquals(0, set.size());
    }

    @Test
    public void testGetImmutable() {
        AgentSet set = new AgentSet(List.of(agentA));
        assertNotNull(set.getAsImmutable(), "Should produce a non-null immutable view.");
    }
}
