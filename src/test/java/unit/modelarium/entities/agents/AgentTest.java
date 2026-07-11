package unit.modelarium.entities.agents;

import modelarium.entities.agents.Agent;
import modelarium.entities.attributes.AgentAttributeSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static unit.modelarium.entities.agents.AgentTestHelpers.*;

public class AgentTest {
    @Test
    public void testName() {
        Agent agent = emptyAgent("Alice");

        assertEquals("Alice", agent.name());
    }

    @Test
    public void testAttributeSetCount_NoAttributeSets() {
        Agent agent = emptyAgent("empty");

        assertEquals(0, agent.attributeSetCount());
    }

    @Test
    public void testAttributeCount_NoAttributeSets() {
        Agent agent = emptyAgent("empty");

        assertEquals(0, agent.attributeCount());
    }

    @Test
    public void testAttributeSetCount() {
        AgentAttributeSet firstAttributeSet = singlePropertyAgentSet("agent", "food", "hunger");
        AgentAttributeSet secondAttributeSet = singlePropertyAgentSet("agent", "health", "hp");
        Agent agent = new Agent("agent", List.of(firstAttributeSet, secondAttributeSet));

        assertEquals(2, agent.attributeSetCount());
    }

    @Test
    public void testGetAttributeSetByIndex() {
        AgentAttributeSet attributeSet = singlePropertyAgentSet("agent", "food", "hunger");
        Agent agent = new Agent("agent", List.of(attributeSet));

        assertSame(attributeSet, agent.getAttributeSet(0));
    }

    @Test
    public void testGetAttributeSetByName() {
        AgentAttributeSet attributeSet = singlePropertyAgentSet("agent", "food", "hunger");
        Agent agent = new Agent("agent", List.of(attributeSet));

        assertSame(attributeSet, agent.getAttributeSet("food"));
    }

    @Test
    public void testAttributeCount() {
        AgentAttributeSet firstAttributeSet = agentAttributeSet("agent", "s1", new AgentCounterProperty("a"));
        AgentAttributeSet secondAttributeSet = agentAttributeSet("agent", "s2", new AgentCounterProperty("b"));
        Agent agent = new Agent("agent", List.of(firstAttributeSet, secondAttributeSet));

        assertEquals(2, agent.attributeCount());
    }
}
