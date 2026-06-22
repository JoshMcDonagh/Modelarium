package unit.modelarium.entities.agents;

import helpers.TestAttributes;
import helpers.TestFixtures;
import modelarium.entities.agents.Agent;
import modelarium.entities.attributes.AgentAttributeSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Agent} class.
 */
public class AgentTest {

    @Test
    public void testAgentNameIsAssigned() {
        Agent agent = TestFixtures.emptyAgent("Alice");
        assertEquals("Alice", agent.name());
    }

    @Test
    public void testAgentWithNoAttributeSets() {
        Agent agent = TestFixtures.emptyAgent("empty");
        assertEquals(0, agent.attributeSetCount());
        assertEquals(0, agent.attributeCount());
    }

    @Test
    public void testAgentAttributeSetCountMatchesInput() {
        AgentAttributeSet setA = TestAttributes.singlePropertyAgentSet("agent", "food", "hunger");
        AgentAttributeSet setB = TestAttributes.singlePropertyAgentSet("agent", "health", "hp");
        Agent agent = new Agent("agent", List.of(setA, setB));

        assertEquals(2, agent.attributeSetCount());
    }

    @Test
    public void testGetAttributeSetByIndex() {
        AgentAttributeSet set = TestAttributes.singlePropertyAgentSet("agent", "food", "hunger");
        Agent agent = new Agent("agent", List.of(set));

        assertSame(set, agent.getAttributeSet(0));
    }

    @Test
    public void testGetAttributeSetByName() {
        AgentAttributeSet set = TestAttributes.singlePropertyAgentSet("agent", "food", "hunger");
        Agent agent = new Agent("agent", List.of(set));

        assertSame(set, agent.getAttributeSet("food"));
    }

    @Test
    public void testTotalAttributeCountAcrossSets() {
        TestAttributes.CounterProperty p1 = new TestAttributes.CounterProperty("a");
        TestAttributes.CounterProperty p2 = new TestAttributes.CounterProperty("b");
        AgentAttributeSet setA = TestAttributes.agentAttributeSet("agent", "s1", p1);
        AgentAttributeSet setB = TestAttributes.agentAttributeSet("agent", "s2", p2);
        Agent agent = new Agent("agent", List.of(setA, setB));

        assertEquals(2, agent.attributeCount(), "Total attributes across both sets.");
    }
}
