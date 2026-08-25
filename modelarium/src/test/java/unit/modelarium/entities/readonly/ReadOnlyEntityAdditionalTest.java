package unit.modelarium.entities.readonly;

import modelarium.entities.readonly.ReadOnlyAgent;
import modelarium.entities.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.Environment;
import modelarium.entities.readonly.ReadOnlyEnvironment;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReadOnlyEntityAdditionalTest {

    @Test
    public void testReadOnlyAgent_IsDeadReflectsMutableAgent() {
        Agent agent = new Agent("A", List.of());
        ReadOnlyAgent readOnlyAgent = agent.getAsImmutable();

        assertFalse(readOnlyAgent.isDead());

        agent.kill();

        assertTrue(readOnlyAgent.isDead());
    }

    @Test
    public void testReadOnlyAgent_GetEventRoutineAndProperty() {
        TestAgentProperty property = new TestAgentProperty("value");
        property.set(12);
        TestAgentEvent event = new TestAgentEvent("event");
        TestAgentRoutine routine = new TestAgentRoutine("routine");
        AgentAttributeSet set = new AgentAttributeSet(
                "state",
                List.<Attribute>of(property, event, routine)
        );
        Agent agent = new Agent("A", List.of(set));
        ReadOnlyAgent readOnlyAgent = agent.getAsImmutable();

        assertEquals("event", readOnlyAgent.getEvent("state", "event").name());
        assertEquals("routine", readOnlyAgent.getRoutine("state", "routine").name());
        assertEquals("value", readOnlyAgent.getProperty("state", "value").name());
        assertEquals(Integer.class, readOnlyAgent.getProperty("state", "value").type());
        assertEquals(12, readOnlyAgent.getProperty("state", "value").get());
        assertTrue(readOnlyAgent.getProperty("state", "value").isLogged());
        assertEquals(AttributeAccessLevel.PUBLIC, readOnlyAgent.getProperty("state", "value").accessLevel());
    }

    @Test
    public void testReadOnlyAgent_GetLogDelegatesToMutableAgent() {
        TestAgentProperty property = new TestAgentProperty("value");
        AgentAttributeSet set = new AgentAttributeSet("state", List.<Attribute>of(property));
        Agent agent = new Agent("A", List.of(set));
        agent.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        set.getLog().record("value", 4);

        assertEquals("A", agent.getAsImmutable().getLog().getEntityName());
        assertEquals(List.of(4), agent.getAsImmutable().getLog().get("state").getValues("value"));
    }

    @Test
    public void testReadOnlyEvent_IsTriggeredDoesNotMutateOriginalEvent() {
        TestAgentEvent event = new TestAgentEvent("event");

        boolean triggered = event.getAsImmutable().isTriggered();

        assertTrue(triggered);
        assertEquals(0, event.triggerChecks);
    }

    @Test
    public void testReadOnlyEnvironment_AttributeCountAndTypedAccessors() {
        TestEnvironmentProperty property = new TestEnvironmentProperty("temperature");
        property.set(21);
        TestEnvironmentEvent event = new TestEnvironmentEvent("event");
        TestEnvironmentRoutine routine = new TestEnvironmentRoutine("routine");
        EnvironmentAttributeSet set = new EnvironmentAttributeSet(
                "state",
                List.<Attribute>of(property, event, routine)
        );
        Environment environment = new Environment("world", List.of(set));
        ReadOnlyEnvironment readOnlyEnvironment = environment.getAsImmutable();

        assertEquals(3, readOnlyEnvironment.attributeCount());
        assertNotNull(readOnlyEnvironment.getAttributeSet("state"));
        assertNotNull(readOnlyEnvironment.getAttributeSet(0));
        assertEquals("event", readOnlyEnvironment.getEvent("state", "event").name());
        assertEquals("routine", readOnlyEnvironment.getRoutine("state", "routine").name());
        assertEquals(21, readOnlyEnvironment.getProperty("state", "temperature").get());
    }

    @Test
    public void testReadOnlyEnvironment_GetLogDelegatesToMutableEnvironment() {
        TestEnvironmentProperty property = new TestEnvironmentProperty("temperature");
        EnvironmentAttributeSet set = new EnvironmentAttributeSet(
                "state",
                List.<Attribute>of(property)
        );
        Environment environment = new Environment("world", List.of(set));
        environment.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        set.getLog().record("temperature", 18);

        assertEquals("world", environment.getAsImmutable().getLog().getEntityName());
        assertEquals(
                List.of(18),
                environment.getAsImmutable().getLog().get("state").getValues("temperature")
        );
    }

    private static class TestAgentProperty extends AgentProperty<Integer> {
        private int value;

        private TestAgentProperty(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void set(AgentContext context, Integer value) {
            this.value = value;
        }

        @Override
        protected Integer get(AgentContext context) {
            return value;
        }
    }

    private static class TestAgentEvent extends AgentEvent {
        private int triggerChecks = 0;

        private TestAgentEvent(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected boolean isTriggered(AgentContext context) {
            triggerChecks++;
            return true;
        }

        @Override
        protected void run(AgentContext context) {}
    }

    private static class TestAgentRoutine extends AgentRoutine {
        private TestAgentRoutine(String name) {
            super(name, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected void run(AgentContext context) {}
    }

    private static class TestEnvironmentProperty extends EnvironmentProperty<Integer> {
        private int value;

        private TestEnvironmentProperty(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void set(EnvironmentContext context, Integer value) {
            this.value = value;
        }

        @Override
        protected Integer get(EnvironmentContext context) {
            return value;
        }
    }

    private static class TestEnvironmentEvent extends EnvironmentEvent {
        private TestEnvironmentEvent(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected boolean isTriggered(EnvironmentContext context) {
            return true;
        }

        @Override
        protected void run(EnvironmentContext context) {}
    }

    private static class TestEnvironmentRoutine extends EnvironmentRoutine {
        private TestEnvironmentRoutine(String name) {
            super(name, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected void run(EnvironmentContext context) {}
    }
}
