package unit.modelarium.entities.attributes;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.functional.FunctionalAgentEvent;
import modelarium.entities.attributes.properties.functional.FunctionalAgentProperty;
import modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine;
import modelarium.exceptions.MissingAttributeFunctionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for attribute types and their functional implementations.
 *
 * <p>Since Property/Event/Routine are context-dependent, these tests exercise
 * the structural aspects (name, isLogged, accessLevel, type) and verify that
 * functional variants throw appropriately when lambdas are missing.
 */
public class AttributeTest {

    // ---- FunctionalAgentProperty ----

    @Test
    void property_nameIsAssigned() {
        FunctionalAgentProperty<Integer> prop = new FunctionalAgentProperty<>(
                "hp", true, AttributeAccessLevel.PUBLIC, Integer.class,
                (ctx, val) -> val,
                (ctx, old, incoming) -> incoming,
                (ctx, val) -> val
        );

        assertEquals("hp", prop.name());
    }

    @Test
    void property_isLoggedFlag() {
        FunctionalAgentProperty<Integer> logged = new FunctionalAgentProperty<>(
                "a", true, AttributeAccessLevel.PUBLIC, Integer.class, null, null, null);
        FunctionalAgentProperty<Integer> unlogged = new FunctionalAgentProperty<>(
                "b", false, AttributeAccessLevel.PUBLIC, Integer.class, null, null, null);

        assertTrue(logged.isLogged());
        assertFalse(unlogged.isLogged());
    }

    @Test
    void property_typeIsRecorded() {
        FunctionalAgentProperty<Double> prop = new FunctionalAgentProperty<>(
                "x", true, AttributeAccessLevel.PUBLIC, Double.class, null, null, null);

        assertEquals(Double.class, prop.type());
    }

    @Test
    void property_accessLevelIsRecorded() {
        FunctionalAgentProperty<Integer> pub = new FunctionalAgentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class, null, null, null);
        FunctionalAgentProperty<Integer> priv = new FunctionalAgentProperty<>(
                "y", false, AttributeAccessLevel.PRIVATE, Integer.class, null, null, null);

        assertEquals(AttributeAccessLevel.PUBLIC, pub.accessLevel());
        assertEquals(AttributeAccessLevel.PRIVATE, priv.accessLevel());
    }

    @Test
    void property_throwsWhenGetterMissing() {
        FunctionalAgentProperty<Integer> prop = new FunctionalAgentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class,
                null, null, null);

        // get() calls get(context), which needs the getter lambda — should throw
        assertThrows(MissingAttributeFunctionException.class, prop::get);
    }

    @Test
    void property_throwsWhenSetterMissing() {
        FunctionalAgentProperty<Integer> prop = new FunctionalAgentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class,
                null, null, null);

        assertThrows(MissingAttributeFunctionException.class, () -> prop.set(42));
    }

    @Test
    void property_runIsNoOpWhenRunLogicIsNull() {
        FunctionalAgentProperty<Integer> prop = new FunctionalAgentProperty<>(
                "x", false, AttributeAccessLevel.PUBLIC, Integer.class,
                (ctx, val) -> val, (ctx, old, incoming) -> incoming, null);

        // Should not throw — null runLogic is treated as no-op
        assertDoesNotThrow(() -> prop.run());
    }

    // ---- FunctionalAgentEvent ----

    @Test
    void event_nameIsAssigned() {
        FunctionalAgentEvent event = new FunctionalAgentEvent(
                "onHungry", true, AttributeAccessLevel.PUBLIC,
                ctx -> {}, ctx -> true);

        assertEquals("onHungry", event.name());
    }

    @Test
    void event_throwsWhenTriggerLogicMissing() {
        FunctionalAgentEvent event = new FunctionalAgentEvent(
                "e", false, AttributeAccessLevel.PUBLIC, ctx -> {}, null);

        assertThrows(MissingAttributeFunctionException.class, event::isTriggered);
    }

    @Test
    void event_throwsWhenRunLogicMissing() {
        FunctionalAgentEvent event = new FunctionalAgentEvent(
                "e", false, AttributeAccessLevel.PUBLIC, null, ctx -> true);

        assertThrows(MissingAttributeFunctionException.class, event::run);
    }

    // ---- FunctionalAgentRoutine ----

    @Test
    void routine_nameIsAssigned() {
        FunctionalAgentRoutine routine = new FunctionalAgentRoutine(
                "daily", AttributeAccessLevel.PUBLIC, ctx -> {});

        assertEquals("daily", routine.name());
    }

    @Test
    void routine_isNeverLogged() {
        // Routines are constructed with isLogged=false (hardcoded in Routine)
        FunctionalAgentRoutine routine = new FunctionalAgentRoutine(
                "r", AttributeAccessLevel.PUBLIC, ctx -> {});

        assertFalse(routine.isLogged(), "Routines should never be logged.");
    }

    @Test
    void routine_throwsWhenRunLogicMissing() {
        FunctionalAgentRoutine routine = new FunctionalAgentRoutine(
                "r", AttributeAccessLevel.PUBLIC, null);

        assertThrows(MissingAttributeFunctionException.class, routine::run);
    }
}
