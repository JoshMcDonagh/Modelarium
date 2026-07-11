package unit.modelarium.entities.immutable;

import helpers.TestAttributes;
import helpers.TestFixtures;
import modelarium.entities.agents.Agent;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.immutable.ImmutableAgent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImmutableAgentTest {
    @Test
    void immutableAgent_nameIsPreserved() {
        Agent agent = TestFixtures.agentWithCounter("Alice");
        ImmutableAgent immutable = new ImmutableAgent(agent);

        assertEquals("Alice", immutable.name());
    }

    @Test
    void immutableAgent_attributeSetCountIsCorrect() {
        AgentAttributeSet s1 = TestAttributes.singlePropertyAgentSet("a", "food", "hunger");
        AgentAttributeSet s2 = TestAttributes.singlePropertyAgentSet("a", "health", "hp");
        Agent agent = new Agent("a", List.of(s1, s2));
        ImmutableAgent immutable = new ImmutableAgent(agent);

        assertEquals(2, immutable.attributeSetCount());
    }

    @Test
    void immutableAgent_totalAttributeCount() {
        Agent agent = TestFixtures.agentWithCounter("a");
        ImmutableAgent immutable = new ImmutableAgent(agent);

        assertEquals(agent.attributeCount(), immutable.attributeCount());
    }

    @Test
    void immutableAgent_getAttributeSetByName() {
        Agent agent = TestFixtures.agentWithCounter("a");
        ImmutableAgent immutable = new ImmutableAgent(agent);

        assertNotNull(immutable.getAttributeSet("stats"));
    }

    @Test
    void immutableAgent_getAttributeSetByIndex() {
        Agent agent = TestFixtures.agentWithCounter("a");
        ImmutableAgent immutable = new ImmutableAgent(agent);

        assertNotNull(immutable.getAttributeSet(0));
    }
}
