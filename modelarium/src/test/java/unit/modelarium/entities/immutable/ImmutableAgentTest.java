package unit.modelarium.entities.immutable;

import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.agents.immutable.ImmutableAgent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static unit.modelarium.entities.immutable.ImmutableEntityTestHelpers.agentWithCounter;
import static unit.modelarium.entities.immutable.ImmutableEntityTestHelpers.singlePropertyAgentSet;

public class ImmutableAgentTest {
    @Test
    public void testName() {
        MutableAgent agent = agentWithCounter("Alice");
        ImmutableAgent immutableAgent = new ImmutableAgent(agent);

        assertEquals("Alice", immutableAgent.name());
    }

    @Test
    public void testAttributeSetCount() {
        MutableAgentAttributeSet firstAttributeSet = singlePropertyAgentSet("a", "food", "hunger");
        MutableAgentAttributeSet secondAttributeSet = singlePropertyAgentSet("a", "health", "hp");
        MutableAgent agent = new MutableAgent("a", List.of(firstAttributeSet, secondAttributeSet));
        ImmutableAgent immutableAgent = new ImmutableAgent(agent);

        assertEquals(2, immutableAgent.attributeSetCount());
    }

    @Test
    public void testAttributeCount() {
        MutableAgent agent = agentWithCounter("a");
        ImmutableAgent immutableAgent = new ImmutableAgent(agent);

        assertEquals(agent.attributeCount(), immutableAgent.attributeCount());
    }

    @Test
    public void testGetAttributeSetByName() {
        MutableAgent agent = agentWithCounter("a");
        ImmutableAgent immutableAgent = new ImmutableAgent(agent);

        assertNotNull(immutableAgent.getAttributeSet("stats"));
    }

    @Test
    public void testGetAttributeSetByIndex() {
        MutableAgent agent = agentWithCounter("a");
        ImmutableAgent immutableAgent = new ImmutableAgent(agent);

        assertNotNull(immutableAgent.getAttributeSet(0));
    }
}
