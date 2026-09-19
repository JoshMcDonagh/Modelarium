package unit.modelarium.entities.attributes;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.functional.FunctionalAgentEvent;
import modelarium.entities.attributes.events.functional.FunctionalEnvironmentEvent;
import modelarium.entities.attributes.properties.functional.FunctionalAgentProperty;
import modelarium.entities.attributes.properties.functional.FunctionalEnvironmentProperty;
import modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine;
import modelarium.entities.attributes.routines.functional.FunctionalEnvironmentRoutine;
import modelarium.exceptions.MissingAttributeFunctionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AttributeTest {
    @Test
    public void testPropertyName() {
        FunctionalAgentProperty<Integer> property = new FunctionalAgentProperty<>(
                "hp", true, AttributeAccessLevel.PUBLIC, Integer.class,
                (context, value) -> value,
                (context, oldValue, incomingValue) -> incomingValue,
                (context, value) -> value
        );

        assertEquals("hp", property.name());
    }

    @Test
    public void testPropertyIsLoggedTrue() {
        FunctionalAgentProperty<Integer> property = new FunctionalAgentProperty<>(
                "a", true, AttributeAccessLevel.PUBLIC, Integer.class, null, null, null);

        assertTrue(property.isLogged());
    }

    @Test
    public void testPropertyIsLoggedFalse() {
        FunctionalAgentProperty<Integer> property = new FunctionalAgentProperty<>(
                "a", false, AttributeAccessLevel.PUBLIC, Integer.class, null, null, null);

        assertFalse(property.isLogged());
    }

    @Test
    public void testPropertyType() {
        FunctionalAgentProperty<Double> property = new FunctionalAgentProperty<>(
                "x", true, AttributeAccessLevel.PUBLIC, Double.class, null, null, null);

        assertEquals(Double.class, property.type());
    }

    @Test
    public void testPropertyAccessLevel() {
        FunctionalAgentProperty<Integer> publicProperty = new FunctionalAgentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class, null, null, null);
        FunctionalAgentProperty<Integer> privateProperty = new FunctionalAgentProperty<>(
                "y", false, AttributeAccessLevel.PRIVATE, Integer.class, null, null, null);

        assertEquals(AttributeAccessLevel.PUBLIC, publicProperty.accessLevel());
        assertEquals(AttributeAccessLevel.PRIVATE, privateProperty.accessLevel());
    }

    @Test
    public void testPropertyGet_MissingGetterFunction_MissingAttributeFunctionException() {
        FunctionalAgentProperty<Integer> property = new FunctionalAgentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class, null, null, null);

        assertThrows(MissingAttributeFunctionException.class, property::get);
    }

    @Test
    public void testPropertySet_MissingSetterFunction_MissingAttributeFunctionException() {
        FunctionalAgentProperty<Integer> property = new FunctionalAgentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class, null, null, null);

        assertThrows(MissingAttributeFunctionException.class, () -> property.set(42));
    }

    @Test
    public void testPropertyRun_MissingRunFunction() {
        FunctionalAgentProperty<Integer> property = new FunctionalAgentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class,
                (context, value) -> value,
                (context, oldValue, incomingValue) -> incomingValue,
                null
        );

        assertDoesNotThrow(() -> property.run());
    }

    @Test
    public void testEventName() {
        FunctionalAgentEvent event = new FunctionalAgentEvent(
                "onHungry", true, AttributeAccessLevel.PUBLIC,
                context -> {}, context -> true);

        assertEquals("onHungry", event.name());
    }

    @Test
    public void testEventIsTriggered_MissingIsTriggeredFunction_MissingAttributeFunctionException() {
        FunctionalAgentEvent event = new FunctionalAgentEvent(
                "e", false, AttributeAccessLevel.PUBLIC, context -> {}, null);

        assertThrows(MissingAttributeFunctionException.class, event::isTriggered);
    }

    @Test
    public void testEventRun_MissingRunFunction_MissingAttributeFunctionException() {
        FunctionalAgentEvent event = new FunctionalAgentEvent(
                "e", false, AttributeAccessLevel.PUBLIC, null, context -> true);

        assertThrows(MissingAttributeFunctionException.class, event::run);
    }

    @Test
    public void testRoutineName() {
        FunctionalAgentRoutine routine = new FunctionalAgentRoutine(
                "daily", AttributeAccessLevel.PUBLIC, context -> {});

        assertEquals("daily", routine.name());
    }

    @Test
    public void testRoutineIsLoggedFalse() {
        FunctionalAgentRoutine routine = new FunctionalAgentRoutine(
                "r", AttributeAccessLevel.PUBLIC, context -> {});

        assertFalse(routine.isLogged());
    }

    @Test
    public void testRoutineRun_MissingRunFunction_MissingAttributeFunctionException() {
        FunctionalAgentRoutine routine = new FunctionalAgentRoutine(
                "r", AttributeAccessLevel.PUBLIC, null);

        assertThrows(MissingAttributeFunctionException.class, routine::run);
    }


    @Test
    public void testPropertySetAndGet() {
        FunctionalAgentProperty<Integer> property = new FunctionalAgentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class,
                (context, value) -> value,
                (context, oldValue, incomingValue) -> incomingValue,
                null
        );

        property.set(41);

        assertEquals(41, property.get());
    }

    @Test
    public void testEventIsTriggeredAndRun() {
        int[] runCount = {0};

        FunctionalAgentEvent event = new FunctionalAgentEvent(
                "x", false, AttributeAccessLevel.PUBLIC,
                (context) -> runCount[0]++,
                (context) -> true
        );

        assertTrue(event.isTriggered());

        event.run();

        assertEquals(1, runCount[0]);
    }

    @Test
    public void testRoutineRun() {
        int[] runCount = {0};

        FunctionalAgentRoutine routine = new FunctionalAgentRoutine(
                "x", AttributeAccessLevel.PUBLIC,
                (context) -> runCount[0]++
        );

        routine.run();

        assertEquals(1, runCount[0]);
    }

    @Test
    public void testEnvironmentPropertySetAndGet() {
        FunctionalEnvironmentProperty<Integer> property = new FunctionalEnvironmentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class,
                (context, value) -> value,
                (context, oldValue, incomingValue) -> incomingValue,
                null
        );

        property.set(41);

        assertEquals(41, property.get());
    }

    @Test
    public void testEnvironmentPropertyRun() {
        FunctionalEnvironmentProperty<Integer> property = new FunctionalEnvironmentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class,
                (context, value) -> value,
                (context, oldValue, incomingValue) -> incomingValue,
                (context, value) -> value == null ? 1 : value + 1
        );

        property.run();

        assertEquals(1, property.get());
    }

    @Test
    public void testEnvironmentPropertyGet_MissingGetterFunction_MissingAttributeFunctionException() {
        FunctionalEnvironmentProperty<Integer> property = new FunctionalEnvironmentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class, null, null, null);

        assertThrows(MissingAttributeFunctionException.class, property::get);
    }

    @Test
    public void testEnvironmentPropertySet_MissingSetterFunction_MissingAttributeFunctionException() {
        FunctionalEnvironmentProperty<Integer> property = new FunctionalEnvironmentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class, null, null, null);

        assertThrows(MissingAttributeFunctionException.class, () -> property.set(42));
    }

    @Test
    public void testEnvironmentEventIsTriggeredAndRun() {
        int[] runCount = {0};

        FunctionalEnvironmentEvent event = new FunctionalEnvironmentEvent(
                "x", false, AttributeAccessLevel.PUBLIC,
                (context) -> runCount[0]++,
                (context) -> true
        );

        assertTrue(event.isTriggered());

        event.run();

        assertEquals(1, runCount[0]);
    }

    @Test
    public void testEnvironmentEventIsTriggered_MissingIsTriggeredFunction_MissingAttributeFunctionException() {
        FunctionalEnvironmentEvent event = new FunctionalEnvironmentEvent(
                "x", false, AttributeAccessLevel.PUBLIC, null, null);

        assertThrows(MissingAttributeFunctionException.class, event::isTriggered);
    }

    @Test
    public void testEnvironmentEventRun_MissingRunFunction_MissingAttributeFunctionException() {
        FunctionalEnvironmentEvent event = new FunctionalEnvironmentEvent(
                "x", false, AttributeAccessLevel.PUBLIC, null, null);

        assertThrows(MissingAttributeFunctionException.class, event::run);
    }

    @Test
    public void testEnvironmentRoutineRun() {
        int[] runCount = {0};

        FunctionalEnvironmentRoutine routine = new FunctionalEnvironmentRoutine(
                "x", AttributeAccessLevel.PUBLIC,
                (context) -> runCount[0]++
        );

        routine.run();

        assertEquals(1, runCount[0]);
    }

    @Test
    public void testEnvironmentRoutineRun_MissingRunFunction_MissingAttributeFunctionException() {
        FunctionalEnvironmentRoutine routine = new FunctionalEnvironmentRoutine(
                "x", AttributeAccessLevel.PUBLIC, null);

        assertThrows(MissingAttributeFunctionException.class, routine::run);
    }
}
