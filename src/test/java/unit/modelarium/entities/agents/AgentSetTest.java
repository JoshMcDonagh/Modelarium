package unit.modelarium.entities.agents;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.agents.AgentTestHelpers.emptyAgent;

public class AgentSetTest {
    @Test
    public void testAdd() {
        Agent agent = emptyAgent("A");
        AgentSet agentSet = new AgentSet();

        agentSet.add(agent);

        assertEquals(1, agentSet.size());
        assertSame(agent, agentSet.get("A"));
    }

    @Test
    public void testAdd_ReplacesExistingAgent() {
        Agent agent = emptyAgent("A");
        Agent replacementAgent = emptyAgent("A");
        AgentSet agentSet = new AgentSet();
        agentSet.add(agent);

        agentSet.add(replacementAgent);

        assertEquals(1, agentSet.size());
        assertSame(replacementAgent, agentSet.get("A"));
    }

    @Test
    public void testAddList() {
        AgentSet agentSet = new AgentSet();

        agentSet.add(List.of(emptyAgent("A"), emptyAgent("B")));

        assertEquals(2, agentSet.size());
        assertTrue(agentSet.doesAgentExist("A"));
        assertTrue(agentSet.doesAgentExist("B"));
    }

    @Test
    public void testAddAgentSet() {
        AgentSet sourceAgentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B")));
        AgentSet targetAgentSet = new AgentSet();

        targetAgentSet.add(sourceAgentSet);

        assertEquals(2, targetAgentSet.size());
    }

    @Test
    public void testConstructorFromList() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B"), emptyAgent("C")));

        assertEquals(3, agentSet.size());
    }

    @Test
    public void testGetByIndex() {
        Agent firstAgent = emptyAgent("A");
        Agent secondAgent = emptyAgent("B");
        AgentSet agentSet = new AgentSet();
        agentSet.add(firstAgent);
        agentSet.add(secondAgent);

        assertSame(firstAgent, agentSet.get(0));
        assertSame(secondAgent, agentSet.get(1));
    }

    @Test
    public void testDoesAgentExistTrue() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A")));

        assertTrue(agentSet.doesAgentExist("A"));
    }

    @Test
    public void testDoesAgentExistFalse() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A")));

        assertFalse(agentSet.doesAgentExist("Z"));
    }

    @Test
    public void testGetFilteredAgents() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B"), emptyAgent("C")));

        AgentSet filteredAgentSet = agentSet.getFilteredAgents(agent -> agent.name().equals("B"));

        assertEquals(1, filteredAgentSet.size());
        assertEquals("B", filteredAgentSet.get(0).name());
    }

    @Test
    public void testGetFilteredAgents_NoMatches() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A")));

        AgentSet filteredAgentSet = agentSet.getFilteredAgents(agent -> false);

        assertEquals(0, filteredAgentSet.size());
    }

    @Test
    public void testGetRandomIterator() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B"), emptyAgent("C")));
        Set<String> agentNames = new HashSet<>();

        Iterator<Agent> randomIterator = agentSet.getRandomIterator(new SplittableRandom(42));
        while (randomIterator.hasNext())
            agentNames.add(randomIterator.next().name());

        assertEquals(3, agentNames.size());
        assertTrue(agentNames.containsAll(List.of("A", "B", "C")));
    }

    @Test
    public void testGetAsList() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B")));

        List<Agent> agentList = agentSet.getAsList();

        assertEquals(2, agentList.size());
    }

    @Test
    public void testDuplicate() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B")));

        AgentSet duplicateAgentSet = agentSet.duplicate();

        assertEquals(2, duplicateAgentSet.size());
    }

    @Test
    public void testIterator() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A"), emptyAgent("B")));
        List<String> agentNames = new ArrayList<>();

        for (Agent agent : agentSet)
            agentNames.add(agent.name());

        assertEquals(List.of("A", "B"), agentNames);
    }

    @Test
    public void testSize_Empty() {
        AgentSet agentSet = new AgentSet();

        assertEquals(0, agentSet.size());
    }

    @Test
    public void testGetAsImmutable() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A")));

        assertNotNull(agentSet.getAsImmutable());
    }
}
