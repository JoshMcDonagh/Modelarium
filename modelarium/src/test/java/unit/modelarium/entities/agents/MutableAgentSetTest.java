package unit.modelarium.entities.agents;

import com.rits.cloning.Cloner;
import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.mutable.MutableAgentSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.agents.AgentTestHelpers.emptyAgent;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;

import static unit.modelarium.entities.agents.AgentTestHelpers.singlePropertyAgentSet;
import static unit.modelarium.entities.agents.AgentTestHelpers.agentAttributeSet;
import static unit.modelarium.entities.agents.AgentTestHelpers.AgentCounterProperty;

public class MutableAgentSetTest {
    @BeforeAll
    static void openForCloning() {
        MutableAgentSetTest.class.getModule().addOpens(
                "unit.modelarium.entities.agents",
                Cloner.class.getModule()
        );
    }

    @Test
    public void testAdd() {
        MutableAgent agent = emptyAgent("A");
        MutableAgentSet agentSet = new MutableAgentSet();

        agentSet.add(agent);

        assertEquals(1, agentSet.size());
        assertSame(agent, agentSet.get("A"));
    }

    @Test
    public void testAdd_ReplacesExistingAgent() {
        MutableAgent agent = emptyAgent("A");
        MutableAgent replacementAgent = emptyAgent("A");
        MutableAgentSet agentSet = new MutableAgentSet();
        agentSet.add(agent);

        agentSet.add(replacementAgent);

        assertEquals(1, agentSet.size());
        assertSame(replacementAgent, agentSet.get("A"));
    }

    @Test
    public void testAddList() {
        MutableAgentSet agentSet = new MutableAgentSet();

        agentSet.add(List.of(emptyAgent("A"), emptyAgent("B")));

        assertEquals(2, agentSet.size());
        assertTrue(agentSet.doesAgentExist("A"));
        assertTrue(agentSet.doesAgentExist("B"));
    }

    @Test
    public void testAddAgentSet() {
        MutableAgentSet sourceAgentSet = new MutableAgentSet(List.of(emptyAgent("A"), emptyAgent("B")));
        MutableAgentSet targetAgentSet = new MutableAgentSet();

        targetAgentSet.add(sourceAgentSet);

        assertEquals(2, targetAgentSet.size());
    }

    @Test
    public void testConstructorFromList() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A"), emptyAgent("B"), emptyAgent("C")));

        assertEquals(3, agentSet.size());
    }

    @Test
    public void testGetByIndex() {
        MutableAgent firstAgent = emptyAgent("A");
        MutableAgent secondAgent = emptyAgent("B");
        MutableAgentSet agentSet = new MutableAgentSet();
        agentSet.add(firstAgent);
        agentSet.add(secondAgent);

        assertSame(firstAgent, agentSet.get(0));
        assertSame(secondAgent, agentSet.get(1));
    }

    @Test
    public void testDoesAgentExistTrue() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A")));

        assertTrue(agentSet.doesAgentExist("A"));
    }

    @Test
    public void testDoesAgentExistFalse() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A")));

        assertFalse(agentSet.doesAgentExist("Z"));
    }

    @Test
    public void testGetFilteredAgents() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A"), emptyAgent("B"), emptyAgent("C")));

        MutableAgentSet filteredAgentSet = agentSet.getFilteredAgents(agent -> agent.name().equals("B"));

        assertEquals(1, filteredAgentSet.size());
        assertEquals("B", filteredAgentSet.get(0).name());
    }

    @Test
    public void testGetFilteredAgents_NoMatches() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A")));

        MutableAgentSet filteredAgentSet = agentSet.getFilteredAgents(agent -> false);

        assertEquals(0, filteredAgentSet.size());
    }

    @Test
    public void testGetRandomIterator() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A"), emptyAgent("B"), emptyAgent("C")));
        Set<String> agentNames = new HashSet<>();

        Iterator<MutableAgent> randomIterator = agentSet.getRandomIterator(new SplittableRandom(42));
        while (randomIterator.hasNext())
            agentNames.add(randomIterator.next().name());

        assertEquals(3, agentNames.size());
        assertTrue(agentNames.containsAll(List.of("A", "B", "C")));
    }

    @Test
    public void testGetAsList() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A"), emptyAgent("B")));

        List<MutableAgent> agentList = agentSet.getAsList();

        assertEquals(2, agentList.size());
    }

    @Test
    public void testDuplicate() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A"), emptyAgent("B")));

        MutableAgentSet duplicateAgentSet = agentSet.duplicate();

        assertEquals(2, duplicateAgentSet.size());
    }

    @Test
    public void testIterator() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A"), emptyAgent("B")));
        List<String> agentNames = new ArrayList<>();

        for (MutableAgent agent : agentSet)
            agentNames.add(agent.name());

        assertEquals(List.of("A", "B"), agentNames);
    }

    @Test
    public void testSize_Empty() {
        MutableAgentSet agentSet = new MutableAgentSet();

        assertEquals(0, agentSet.size());
    }

    @Test
    public void testGetAsImmutable() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A")));

        assertNotNull(agentSet.getAsImmutable());
    }


    @Test
    public void testAddDeepCopy_NewAgent() {
        MutableAgentAttributeSet attributeSet = singlePropertyAgentSet("A", "food", "hunger");
        MutableAgent original = new MutableAgent("A", List.of(attributeSet));
        MutableAgentSet agentSet = new MutableAgentSet();

        agentSet.addDeepCopy(original);

        MutableAgent stored = agentSet.get("A");
        assertNotSame(original, stored);
        assertEquals("A", stored.name());
        assertNotSame(original.getAttributeSet(0), stored.getAttributeSet(0));
    }

    @Test
    public void testAddDeepCopy_ReplacesExistingAgent() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A")));
        MutableAgent replacement = new MutableAgent("A", List.of(singlePropertyAgentSet("A", "food", "hunger")));

        agentSet.addDeepCopy(replacement);

        assertEquals(1, agentSet.size());
        assertEquals(1, agentSet.get("A").attributeSetCount());
    }

    @Test
    public void testAddDeepCopy_StoredCopyIsIndependentOfOriginal() {
        AgentCounterProperty property = new AgentCounterProperty("hunger");
        property.set(5.0);
        MutableAgent original = new MutableAgent("A", List.of(agentAttributeSet("A", "food", property)));
        MutableAgentSet agentSet = new MutableAgentSet();
        agentSet.addDeepCopy(original);

        property.set(9.0);

        assertEquals(5.0, agentSet.get("A").getProperty("food", "hunger").get());
    }

    @Test
    public void testAddDeepCopyList() {
        List<MutableAgent> agentList = List.of(emptyAgent("A"), emptyAgent("B"));
        MutableAgentSet agentSet = new MutableAgentSet();

        agentSet.addDeepCopy(agentList);

        assertEquals(2, agentSet.size());
        assertEquals("A", agentSet.get("A").name());
        assertEquals("B", agentSet.get("B").name());

        assertNotSame(agentList.get(0), agentSet.get("A"));
        assertNotSame(agentList.get(1), agentSet.get("B"));
    }

    @Test
    public void testAddDeepCopyAgentSet() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(emptyAgent("A")));
        MutableAgentSet other = new MutableAgentSet(List.of(
                new MutableAgent("A", List.of(singlePropertyAgentSet("A", "food", "hunger"))),
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
        MutableAgentSet agentSet = new MutableAgentSet();

        assertThrows(IllegalArgumentException.class, () -> agentSet.add((MutableAgentSet) null));
    }

    @Test
    public void testAddDeepCopyAgentSet_Null_IllegalArgumentException() {
        MutableAgentSet agentSet = new MutableAgentSet();

        assertThrows(IllegalArgumentException.class, () -> agentSet.addDeepCopy((MutableAgentSet) null));
    }

    @Test
    public void testUpdate_ShallowSharesInstances() {
        MutableAgentSet agentSet = new MutableAgentSet();
        MutableAgent agent = emptyAgent("A");
        MutableAgentSet other = new MutableAgentSet(List.of(agent));

        agentSet.update(other, false);

        assertSame(agent, agentSet.get("A"));
    }

    @Test
    public void testUpdate_DeepCopied() {
        MutableAgentSet agentSet = new MutableAgentSet();
        MutableAgent agent = new MutableAgent("A", List.of(singlePropertyAgentSet("A", "food", "hunger")));
        MutableAgentSet other = new MutableAgentSet(List.of(agent));

        agentSet.update(other, true);

        assertNotSame(agent, agentSet.get("A"));
        assertEquals("A", agentSet.get("A").name());
    }

    @Test
    public void testUpdate_Null_IllegalArgumentException() {
        MutableAgentSet agentSet = new MutableAgentSet();

        assertThrows(IllegalArgumentException.class, () -> agentSet.update(null, false));
    }

    @Test
    public void testSetLogDatabaseFactory_PropagatesToAgents() {
        MutableAgentSet agentSet = new MutableAgentSet(List.of(
                new MutableAgent("A", List.of(singlePropertyAgentSet("A", "food", "hunger")))
        ));

        agentSet.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());

        assertNotNull(agentSet.get("A").getAttributeSet("food").getLog());
    }
}
