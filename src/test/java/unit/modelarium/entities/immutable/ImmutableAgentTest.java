package unit.modelarium.entities.immutable;

import modelarium.entities.agents.Agent;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.immutable.ImmutableAgent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static unit.modelarium.entities.immutable.ImmutableEntityTestHelpers.agentWithCounter;
import static unit.modelarium.entities.immutable.ImmutableEntityTestHelpers.singlePropertyAgentSet;

public class ImmutableAgentTest {
    @Test
    public void testName() {
        Agent agent = agentWithCounter("Alice");
        ImmutableAgent immutableAgent = new ImmutableAgent(agent);

        assertEquals("Alice", immutableAgent.name());
    }

    @Test
    public void testAttributeSetCount() {
        AgentAttributeSet firstAttributeSet = singlePropertyAgentSet("a", "food", "hunger");
        AgentAttributeSet secondAttributeSet = singlePropertyAgentSet("a", "health", "hp");
        Agent agent = new Agent("a", List.of(firstAttributeSet, secondAttributeSet));
        ImmutableAgent immutableAgent = new ImmutableAgent(agent);

        assertEquals(2, immutableAgent.attributeSetCount());
    }

    @Test
    public void testAttributeCount() {
        Agent agent = agentWithCounter("a");
        ImmutableAgent immutableAgent = new ImmutableAgent(agent);

        assertEquals(agent.attributeCount(), immutableAgent.attributeCount());
    }

    @Test
    public void testGetAttributeSetByName() {
        Agent agent = agentWithCounter("a");
        ImmutableAgent immutableAgent = new ImmutableAgent(agent);

        assertNotNull(immutableAgent.getAttributeSet("stats"));
    }

    @Test
    public void testGetAttributeSetByIndex() {
        Agent agent = agentWithCounter("a");
        ImmutableAgent immutableAgent = new ImmutableAgent(agent);

        assertNotNull(immutableAgent.getAttributeSet(0));
    }
}
