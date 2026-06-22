package unit.modelarium.entities.immutable;

import helpers.TestAttributes;
import helpers.TestFixtures;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;
import modelarium.entities.immutable.ImmutableEnvironment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the immutable entity wrappers.
 *
 * <p>These wrappers present a read-only view over mutable entities.
 * Structural queries (name, counts) should delegate correctly.
 */
public class ImmutableEntityTest {

    // ---- ImmutableAgent ----

    @Test
    void immutableAgent_nameIsPreserved() {
        Agent agent = TestFixtures.agentWithCounter("Alice");
        ImmutableAgent immutable = new ImmutableAgent(agent);

        assertEquals("Alice", immutable.getName());
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

    // ---- ImmutableEnvironment ----

    @Test
    void immutableEnvironment_nameIsPreserved() {
        Environment env = TestFixtures.environmentWithTickCounter();
        ImmutableEnvironment immutable = new ImmutableEnvironment(env);

        assertEquals("env", immutable.getName());
    }

    @Test
    void immutableEnvironment_attributeSetCountIsCorrect() {
        Environment env = TestFixtures.environmentWithTickCounter();
        ImmutableEnvironment immutable = new ImmutableEnvironment(env);

        assertEquals(1, immutable.attributeSetCount());
    }

    // ---- ImmutableAgentSet ----

    @Test
    void immutableAgentSet_getByName() {
        Agent a = TestFixtures.emptyAgent("A");
        AgentSet set = new AgentSet(List.of(a));
        ImmutableAgentSet immutable = set.getAsImmutable();

        ImmutableAgent result = immutable.get("A");
        assertEquals("A", result.getName());
    }

    @Test
    void immutableAgentSet_getByIndex() {
        Agent a = TestFixtures.emptyAgent("A");
        Agent b = TestFixtures.emptyAgent("B");
        AgentSet set = new AgentSet(List.of(a, b));
        ImmutableAgentSet immutable = set.getAsImmutable();

        assertEquals("A", immutable.get(0).getName());
        assertEquals("B", immutable.get(1).getName());
    }

    @Test
    void immutableAgentSet_isIterable() {
        Agent a = TestFixtures.emptyAgent("A");
        Agent b = TestFixtures.emptyAgent("B");
        AgentSet set = new AgentSet(List.of(a, b));
        ImmutableAgentSet immutable = set.getAsImmutable();

        int count = 0;
        for (ImmutableAgent agent : immutable)
            count++;

        assertEquals(2, count);
    }
}
