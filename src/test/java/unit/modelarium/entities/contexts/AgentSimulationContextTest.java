package unit.modelarium.entities.contexts;

import helpers.TestAttributes;
import helpers.TestFixtures;
import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.contexts.AgentSimulationContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

public class AgentSimulationContextTest {
    @Test
    public void testGetThisEntity() {
        Agent agent = TestFixtures.emptyAgent("Alice");
        Agent otherAgent = TestFixtures.emptyAgent("Bob");
        AgentSet agentSet = TestFixtures.agentSet(agent, otherAgent);
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        AgentSimulationContext context = TestFixtures.agentSimulationContext(agent, agentSet, config);

        assertSame(agent, context.getThisEntity());
    }

    @Test
    public void testGetThisAttributeSet() {
        Agent agent = TestFixtures.emptyAgent("Alice");
        Agent otherAgent = TestFixtures.emptyAgent("Bob");
        AgentSet agentSet = TestFixtures.agentSet(agent, otherAgent);
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        AgentAttributeSet set = TestAttributes.singlePropertyAgentSet("owner", "food", "hunger");
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithAttributeSet(agent, agentSet, config, set);

        assertSame(set, context.getThisAttributeSet());
    }

    @Test
    public void testGetThisAttribute() {
        Agent agent = TestFixtures.emptyAgent("Alice");
        Agent otherAgent = TestFixtures.emptyAgent("Bob");
        AgentSet agentSet = TestFixtures.agentSet(agent, otherAgent);
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Attribute<?> attribute = new TestAttributes.CounterProperty("a");
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithAttribute(agent, agentSet, config, attribute);

        assertSame(attribute, context.getThisAttribute());
    }

    @Test
    public void testGetEnvironmentWithUnsyncedThreads() {
        fail("Test not implemented...");
    }

    @Test
    public void testGetEnvironmentWithSyncedThreadsIsCached() {
        fail("Test not implemented...");
    }

    @Test
    public void testGetEnvironmentWithSyncedThreadsIsNotCached() {
        fail("Test not implemented...");
    }

    @Test
    public void testGetClock() {
        fail("Test not implemented...");
    }

    @Test
    public void testDoesAgentExistInThisCoreTrue() {
        fail("Test not implemented...");
    }

    @Test
    public void testDoesAgentExistInThisCoreFalse() {
        fail("Test not implemented...");
    }

    @Test
    public void testGetRandom() {
        fail("Test not implemented...");
    }
}
