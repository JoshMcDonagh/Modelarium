package unit.modelarium.entities.agents.mutable;

import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.agents.mutable.AgentTestHelpers.*;

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
        MutableAgentAttributeSet firstAttributeSet = singlePropertyAgentSet("agent", "food", "hunger");
        MutableAgentAttributeSet secondAttributeSet = singlePropertyAgentSet("agent", "health", "hp");
        Agent agent = new Agent("agent", List.of(firstAttributeSet, secondAttributeSet));

        assertEquals(2, agent.attributeSetCount());
    }

    @Test
    public void testGetAttributeSetByIndex() {
        MutableAgentAttributeSet attributeSet = singlePropertyAgentSet("agent", "food", "hunger");
        Agent agent = new Agent("agent", List.of(attributeSet));

        assertSame(attributeSet, agent.getAttributeSet(0));
    }

    @Test
    public void testGetAttributeSetByName() {
        MutableAgentAttributeSet attributeSet = singlePropertyAgentSet("agent", "food", "hunger");
        Agent agent = new Agent("agent", List.of(attributeSet));

        assertSame(attributeSet, agent.getAttributeSet("food"));
    }

    @Test
    public void testAttributeCount() {
        MutableAgentAttributeSet firstAttributeSet = agentAttributeSet("agent", "s1", new AgentCounterProperty("a"));
        MutableAgentAttributeSet secondAttributeSet = agentAttributeSet("agent", "s2", new AgentCounterProperty("b"));
        Agent agent = new Agent("agent", List.of(firstAttributeSet, secondAttributeSet));

        assertEquals(2, agent.attributeCount());
    }


    @Test
    public void testGetEvent() {
        MutableAgentAttributeSet attributeSet = agentAttributeSetFromAttributes(
                "agent", "behaviour", new AlwaysTriggeredAgentEvent("act"));
        Agent agent = new Agent("agent", List.of(attributeSet));

        assertEquals("act", agent.getEvent("behaviour", "act").name());
    }

    @Test
    public void testGetRoutine() {
        MutableAgentAttributeSet attributeSet = agentAttributeSetFromAttributes(
                "agent", "behaviour", new EmptyAgentRoutine("tick"));
        Agent agent = new Agent("agent", List.of(attributeSet));

        assertEquals("tick", agent.getRoutine("behaviour", "tick").name());
    }

    @Test
    public void testGetProperty() {
        MutableAgentAttributeSet attributeSet = singlePropertyAgentSet("agent", "food", "hunger");
        Agent agent = new Agent("agent", List.of(attributeSet));

        assertEquals("hunger", agent.getProperty("food", "hunger").name());
    }

    @Test
    public void testCreateContext() {
        MutableAgentAttributeSet attributeSet = singlePropertyAgentSet("agent", "food", "hunger");
        Agent agent = new Agent("agent", List.of(attributeSet));

        createContextFor(agent);

        assertNotNull(agent.context());
        assertSame(agent, agent.context().getThisEntity());
    }

    @Test
    public void testCreateContext_CalledTwice_IllegalStateException() {
        Agent agent = new Agent("agent", List.of(singlePropertyAgentSet("agent", "food", "hunger")));
        createContextFor(agent);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> createContextFor(agent));
        assertEquals("Context already created", exception.getMessage());
    }

    @Test
    public void testRun_RecordsLoggedValues() {
        MutableAgentAttributeSet attributeSet = singlePropertyAgentSet("agent", "food", "hunger");
        Agent agent = new Agent("agent", List.of(attributeSet));
        agent.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        createContextFor(agent);

        agent.run();

        assertEquals(List.of(1.0), agent.getAttributeSet(0).getLog().getValues("hunger"));
    }

    @Test
    public void testIsDead_InitiallyFalse() {
        Agent agent = emptyAgent("A");

        assertFalse(agent.isDead());
    }

    @Test
    public void testKill_MarksAgentDead() {
        Agent agent = emptyAgent("A");

        agent.kill();

        assertTrue(agent.isDead());
    }

    @Test
    public void testKill_CalledTwice_RemainsDead() {
        Agent agent = emptyAgent("A");

        agent.kill();
        agent.kill();

        assertTrue(agent.isDead());
    }

    @Test
    public void testRun_AfterKill_DoesNotRunOrAddAnotherLogEntry() {
        AgentCounterProperty property = new AgentCounterProperty("counter");
        MutableAgentAttributeSet attributeSet = agentAttributeSet("A", "state", property);
        Agent agent = new Agent("A", List.of(attributeSet));
        agent.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        createContextFor(agent);

        agent.run();
        agent.kill();
        agent.run();

        assertEquals(1.0, property.get());
        assertEquals(List.of(1.0), attributeSet.getLog().getValues("counter"));
    }

    private static class ContextCapturingRoutine extends AgentRoutine {
        private MutableAgentAttributeSet observedAttributeSet;
        private Object observedAttribute;

        private ContextCapturingRoutine(String name) {
            super(name, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected void run(AgentContext context) {
            observedAttributeSet = context.getThisAttributeSet();
            observedAttribute = context.getThisAttribute();
        }
    }

    @Test
    public void testRun_AttributeSeesCorrectCurrentAttributeAndAttributeSet() {
        ContextCapturingRoutine routine = new ContextCapturingRoutine("capture");
        MutableAgentAttributeSet attributeSet = new MutableAgentAttributeSet(
                "behaviour",
                List.<Attribute>of(routine)
        );
        Agent agent = new Agent("A", List.of(attributeSet));
        agent.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        createContextFor(agent);

        agent.run();

        assertSame(attributeSet, routine.observedAttributeSet);
        assertSame(routine, routine.observedAttribute);
    }
}
