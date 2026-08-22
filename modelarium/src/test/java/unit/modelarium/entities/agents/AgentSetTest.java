package unit.modelarium.entities.agents;

import com.rits.cloning.Cloner;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.agents.AgentTestHelpers.*;

public class AgentSetTest {
    @BeforeAll
    static void openForCloning() {
        AgentSetTest.class.getModule().addOpens(
                "unit.modelarium.entities.agents",
                Cloner.class.getModule()
        );
    }

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


    @Test
    public void testAddDeepCopy_NewAgent() {
        MutableAgentAttributeSet attributeSet = singlePropertyAgentSet("A", "food", "hunger");
        Agent original = new Agent("A", List.of(attributeSet));
        AgentSet agentSet = new AgentSet();

        agentSet.addDeepCopy(original);

        Agent stored = agentSet.get("A");
        assertNotSame(original, stored);
        assertEquals("A", stored.name());
        assertNotSame(original.getAttributeSet(0), stored.getAttributeSet(0));
    }

    @Test
    public void testAddDeepCopy_ReplacesExistingAgent() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A")));
        Agent replacement = new Agent("A", List.of(singlePropertyAgentSet("A", "food", "hunger")));

        agentSet.addDeepCopy(replacement);

        assertEquals(1, agentSet.size());
        assertEquals(1, agentSet.get("A").attributeSetCount());
    }

    @Test
    public void testAddDeepCopy_StoredCopyIsIndependentOfOriginal() {
        AgentCounterProperty property = new AgentCounterProperty("hunger");
        property.set(5.0);
        Agent original = new Agent("A", List.of(agentAttributeSet("A", "food", property)));
        AgentSet agentSet = new AgentSet();
        agentSet.addDeepCopy(original);

        property.set(9.0);

        assertEquals(5.0, agentSet.get("A").getProperty("food", "hunger").get());
    }

    @Test
    public void testAddDeepCopyList() {
        List<Agent> agentList = List.of(emptyAgent("A"), emptyAgent("B"));
        AgentSet agentSet = new AgentSet();

        agentSet.addDeepCopy(agentList);

        assertEquals(2, agentSet.size());
        assertEquals("A", agentSet.get("A").name());
        assertEquals("B", agentSet.get("B").name());

        assertNotSame(agentList.get(0), agentSet.get("A"));
        assertNotSame(agentList.get(1), agentSet.get("B"));
    }

    @Test
    public void testAddDeepCopyAgentSet() {
        AgentSet agentSet = new AgentSet(List.of(emptyAgent("A")));
        AgentSet other = new AgentSet(List.of(
                new Agent("A", List.of(singlePropertyAgentSet("A", "food", "hunger"))),
                emptyAgent("B")
        ));

        agentSet.addDeepCopy(other);

        assertEquals(2, agentSet.size());
        assertEquals(1, agentSet.get("A").attributeSetCount());
        assertNotSame(agentSet.get("A"), other.get("A"));
        assertNotSame(agentSet.get("B"), other.get("B"));
    }

    @Test
    public void testAddAgentSet_Null_IllegalArgumentException() {
        AgentSet agentSet = new AgentSet();

        assertThrows(IllegalArgumentException.class, () -> agentSet.add((AgentSet) null));
    }

    @Test
    public void testAddDeepCopyAgentSet_Null_IllegalArgumentException() {
        AgentSet agentSet = new AgentSet();

        assertThrows(IllegalArgumentException.class, () -> agentSet.addDeepCopy((AgentSet) null));
    }

    @Test
    public void testUpdate_ShallowSharesInstances() {
        AgentSet agentSet = new AgentSet();
        Agent agent = emptyAgent("A");
        AgentSet other = new AgentSet(List.of(agent));

        agentSet.update(other, false);

        assertSame(agent, agentSet.get("A"));
    }

    @Test
    public void testUpdate_DeepCopied() {
        AgentSet agentSet = new AgentSet();
        Agent agent = new Agent("A", List.of(singlePropertyAgentSet("A", "food", "hunger")));
        AgentSet other = new AgentSet(List.of(agent));

        agentSet.update(other, true);

        assertNotSame(agent, agentSet.get("A"));
        assertEquals("A", agentSet.get("A").name());
    }

    @Test
    public void testUpdate_Null_IllegalArgumentException() {
        AgentSet agentSet = new AgentSet();

        assertThrows(IllegalArgumentException.class, () -> agentSet.update(null, false));
    }

    @Test
    public void testSetLogDatabaseFactory_PropagatesToAgents() {
        AgentSet agentSet = new AgentSet(List.of(
                new Agent("A", List.of(singlePropertyAgentSet("A", "food", "hunger")))
        ));

        agentSet.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());

        assertNotNull(agentSet.get("A").getAttributeSet("food").getLog());
    }
}
