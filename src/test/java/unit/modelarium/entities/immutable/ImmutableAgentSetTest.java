package unit.modelarium.entities.immutable;

import helpers.TestFixtures;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImmutableAgentSetTest {
    @Test
    void immutableAgentSet_getByName() {
        Agent a = TestFixtures.emptyAgent("A");
        AgentSet set = new AgentSet(List.of(a));
        ImmutableAgentSet immutable = set.getAsImmutable();

        ImmutableAgent result = immutable.get("A");
        assertEquals("A", result.name());
    }

    @Test
    void immutableAgentSet_getByIndex() {
        Agent a = TestFixtures.emptyAgent("A");
        Agent b = TestFixtures.emptyAgent("B");
        AgentSet set = new AgentSet(List.of(a, b));
        ImmutableAgentSet immutable = set.getAsImmutable();

        assertEquals("A", immutable.get(0).name());
        assertEquals("B", immutable.get(1).name());
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
