package unit.modelarium.entities.agents.immutable;

import modelarium.entities.readonly.ReadOnlyAgent;
import modelarium.entities.Agent;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static unit.modelarium.entities.ReadOnlyEntityTestHelpers.agentWithCounter;
import static unit.modelarium.entities.ReadOnlyEntityTestHelpers.singlePropertyAgentSet;

public class ReadOnlyAgentTest {
    @Test
    public void testName() {
        Agent agent = agentWithCounter("Alice");
        ReadOnlyAgent immutableAgent = new ReadOnlyAgent(agent);

        assertEquals("Alice", immutableAgent.name());
    }

    @Test
    public void testAttributeSetCount() {
        AgentAttributeSet firstAttributeSet = singlePropertyAgentSet("a", "food", "hunger");
        AgentAttributeSet secondAttributeSet = singlePropertyAgentSet("a", "health", "hp");
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
