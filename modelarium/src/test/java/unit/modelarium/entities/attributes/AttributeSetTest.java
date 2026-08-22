package unit.modelarium.entities.attributes;

import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.functional.FunctionalEnvironmentEvent;
import modelarium.entities.attributes.routines.functional.FunctionalEnvironmentRoutine;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import modelarium.exceptions.AttributeAccessException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.attributes.AttributeTestHelpers.*;

public class AttributeSetTest {
    @Test
    public void testName() {
        MutableAgentAttributeSet attributeSet = singlePropertyAgentSet("owner", "food", "hunger");

        assertEquals("food", attributeSet.name());
    }

    @Test
    public void testSize() {
        MutableAgentAttributeSet attributeSet = agentAttributeSet(
                "owner",
                "s",
                new AgentCounterProperty("a"),
                new AgentCounterProperty("b")
        );

        assertEquals(2, attributeSet.size());
    }

    @Test
    public void testSize_Empty() {
        MutableAgentAttributeSet attributeSet = agentAttributeSet("owner", "empty");

        assertEquals(0, attributeSet.size());
    }

    @Test
    public void testGetProperty_PublicAccessLevel() {
        MutableAgentAttributeSet attributeSet = agentAttributeSet("owner", "s", new AgentCounterProperty("counter"));

        assertDoesNotThrow(() -> attributeSet.getProperty("counter"));
    }

    @Test
    public void testGetProperty_PrivateAccessLevel_AttributeAccessException() {
        MutableAgentAttributeSet attributeSet = agentAttributeSet("owner", "s", new PrivateCounterProperty("secret"));

        assertThrows(AttributeAccessException.class, () -> attributeSet.getProperty("secret"));
    }

    @Test
    public void testGetEvent() {
        MutableAgentAttributeSet attributeSet = agentAttributeSetFromEvents(
                "owner",
                "food",
                new AlwaysTriggeredAgentEvent("eatFood")
        );

        assertDoesNotThrow(() -> attributeSet.getEvent("eatFood"));
    }

    @Test
    public void testGetEvent_GivenPropertyName_AttributeAccessException() {
        MutableAgentAttributeSet attributeSet = agentAttributeSet("owner", "s", new AgentCounterProperty("hp"));

        assertThrows(AttributeAccessException.class, () -> attributeSet.getEvent("hp"));
    }

    @Test
    public void testGetRoutine() {
        MutableAgentAttributeSet attributeSet = agentAttributeSetFromRoutines(
                "owner",
                "sim",
                new EmptyAgentRoutine("tick")
        );

        assertDoesNotThrow(() -> attributeSet.getRoutine("tick"));
    }

    @Test
    public void testEnvironmentAttributeSetName() {
        MutableEnvironmentAttributeSet attributeSet = emptyEnvironmentAttributeSet("env", "weather");

        assertEquals("weather", attributeSet.name());
    }

    @Test
    public void testEnvironmentAttributeSetSize() {
        MutableEnvironmentAttributeSet attributeSet = environmentAttributeSet(
                "env",
                "timing",
                new EnvironmentTickProperty("tick")
        );

        assertEquals(1, attributeSet.size());
    }


    @SuppressWarnings("unchecked")
    private MutableEnvironmentAttributeSet mixedEnvironmentAttributeSet() {
        FunctionalEnvironmentEvent event0 = new FunctionalEnvironmentEvent(
                "Event_0", false, AttributeAccessLevel.PUBLIC, (context) -> {}, (context) -> true);
        FunctionalEnvironmentEvent event1 = new FunctionalEnvironmentEvent(
                "Event_1", false, AttributeAccessLevel.PUBLIC, (context) -> {}, (context) -> true);
        EnvironmentTickProperty property0 = new EnvironmentTickProperty("Property_0");
        FunctionalEnvironmentRoutine routine0 = new FunctionalEnvironmentRoutine(
                "Routine_0", AttributeAccessLevel.PUBLIC, (context) -> {});

        return new MutableEnvironmentAttributeSet(
                "testAttributeSetName",
                (List<Attribute>) (List<?>) List.of(event0, event1, property0, routine0)
        );
    }

    @Test
    public void testEnvironmentGet() {
        MutableEnvironmentAttributeSet attributeSet = mixedEnvironmentAttributeSet();

        assertEquals("Property_0", attributeSet.get(2).name());
        assertEquals("Event_1", attributeSet.get("Event_1").name());
    }

    @Test
    public void testEnvironmentGetEvent() {
        MutableEnvironmentAttributeSet attributeSet = mixedEnvironmentAttributeSet();

        assertEquals("Event_1", attributeSet.getEvent(1).name());
        assertEquals("Event_0", attributeSet.getEvent("Event_0").name());
    }

    @Test
    public void testEnvironmentGetRoutine() {
        MutableEnvironmentAttributeSet attributeSet = mixedEnvironmentAttributeSet();

        assertEquals("Routine_0", attributeSet.getRoutine(0).name());
        assertEquals("Routine_0", attributeSet.getRoutine("Routine_0").name());
    }

    @Test
    public void testEnvironmentGetProperty() {
        MutableEnvironmentAttributeSet attributeSet = mixedEnvironmentAttributeSet();

        assertEquals("Property_0", attributeSet.getProperty(0).name());
        assertEquals("Property_0", attributeSet.getProperty("Property_0").name());
    }

    @Test
    public void testRun() {
        AgentCounterProperty property = new AgentCounterProperty("Property_0");
        UnloggedAgentCounterProperty unloggedProperty = new UnloggedAgentCounterProperty("Property_1");
        ToggleableAgentEvent triggeredEvent = new ToggleableAgentEvent("Event_0", true);
        CountingAgentRoutine routine = new CountingAgentRoutine("Routine_0");

        MutableAgentAttributeSet attributeSet = agentAttributeSetFromAttributes(
                "TestOwner", "testAttributeSetName", property, unloggedProperty, triggeredEvent, routine);
        attributeSet.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        attributeSet.setContext(agentSimulationContext(attributeSet));

        attributeSet.run();

        assertEquals(List.of(1.0), attributeSet.getLog().getValues("Property_0"));
        assertEquals(List.of(true), attributeSet.getLog().getValues("Event_0"));
        assertEquals(1, triggeredEvent.runCount());
        assertEquals(1, routine.runCount());
    }

    @Test
    public void testRun_UnloggedAttributeIsNotRecorded() {
        AgentCounterProperty property = new AgentCounterProperty("Property_0");
        UnloggedAgentCounterProperty unloggedProperty = new UnloggedAgentCounterProperty("Property_1");

        MutableAgentAttributeSet attributeSet = agentAttributeSetFromAttributes(
                "TestOwner", "testAttributeSetName", property, unloggedProperty);
        attributeSet.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        attributeSet.setContext(agentSimulationContext(attributeSet));

        attributeSet.run();

        assertFalse(attributeSet.getLog().getAttributeNamesList().contains("Property_1"));
        assertNull(attributeSet.getLog().getValues("Property_1"));
    }

    @Test
    public void testRun_EventNotTriggered() {
        ToggleableAgentEvent untriggeredEvent = new ToggleableAgentEvent("Event_0", false);

        MutableAgentAttributeSet attributeSet = agentAttributeSetFromAttributes(
                "TestOwner", "testAttributeSetName", untriggeredEvent);
        attributeSet.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        attributeSet.setContext(agentSimulationContext(attributeSet));

        attributeSet.run();

        assertEquals(List.of(false), attributeSet.getLog().getValues("Event_0"));
        assertEquals(0, untriggeredEvent.runCount());
    }

    @Test
    public void testSetContext_SecondCallIgnored() {
        AgentCounterProperty property = new AgentCounterProperty("Property_0");

        MutableAgentAttributeSet attributeSet = agentAttributeSetFromAttributes(
                "TestOwner", "testAttributeSetName", property);
        attributeSet.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        attributeSet.setContext(agentSimulationContext(attributeSet));

        assertDoesNotThrow(() -> attributeSet.setContext(agentSimulationContext(attributeSet)));
        assertDoesNotThrow(attributeSet::run);
    }

    @Test
    public void testSetLogDatabaseFactory_SecondCallIgnored() {
        MutableAgentAttributeSet attributeSet = singlePropertyAgentSet(
                "TestOwner", "testAttributeSetName", "Property_0");

        attributeSet.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        AttributeSetLog<?> firstLog = attributeSet.getLog();

        attributeSet.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());

        assertSame(firstLog, attributeSet.getLog());
    }
}
