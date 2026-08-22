package unit.modelarium.entities.immutable;

import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
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
        ReadOnlyAgent immutableAgent = new ReadOnlyAgent(agent);

        assertEquals("Alice", immutableAgent.name());
    }

    @Test
    public void testAttributeSetCount() {
        MutableAgentAttributeSet firstAttributeSet = singlePropertyAgentSet("a", "food", "hunger");
        MutableAgentAttributeSet secondAttributeSet = singlePropertyAgentSet("a", "health", "hp");
        Agent agent = new Agent("a", List.of(firstAttributeSet, secondAttributeSet));
        ReadOnlyAgent immutableAgent = new ReadOnlyAgent(agent);

        assertEquals(2, immutableAgent.attributeSetCount());
    }

    @Test
    public void testAttributeCount() {
        Agent agent = agentWithCounter("a");
        ReadOnlyAgent immutableAgent = new ReadOnlyAgent(agent);

        assertEquals(agent.attributeCount(), immutableAgent.attributeCount());
    }

    @Test
    public void testGetAttributeSetByName() {
        Agent agent = agentWithCounter("a");
        ReadOnlyAgent immutableAgent = new ReadOnlyAgent(agent);

        assertNotNull(immutableAgent.getAttributeSet("stats"));
    }

    @Test
    public void testGetAttributeSetByIndex() {
        Agent agent = agentWithCounter("a");
        ReadOnlyAgent immutableAgent = new ReadOnlyAgent(agent);

        assertNotNull(immutableAgent.getAttributeSet(0));
    }
}
