package unit.modelarium.entities.attributes;

import helpers.TestAttributes;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.exceptions.AttributeAccessException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AgentAttributeSet} and {@link EnvironmentAttributeSet}.
 *
 * <p>Covers construction, naming, size queries, and access-level enforcement.
 * Run-time behaviour (execution of attributes within the tick loop) is tested
 * via integration tests, since it requires a live context.
 */
public class AttributeSetTest {

    // ---- Construction and metadata ----

    @Test
    void testNameIsAssigned() {
        AgentAttributeSet set = TestAttributes.singlePropertyAgentSet("owner", "food", "hunger");
        assertEquals("food", set.name());
    }

    @Test
    void testSizeReflectsAttributeCount() {
        TestAttributes.CounterProperty p1 = new TestAttributes.CounterProperty("a");
        TestAttributes.CounterProperty p2 = new TestAttributes.CounterProperty("b");
        AgentAttributeSet set = TestAttributes.agentAttributeSet("owner", "s", p1, p2);

        assertEquals(2, set.size());
    }

    @Test
    void testEmptySetHasZeroSize() {
        @SuppressWarnings("unchecked")
        AgentAttributeSet set = new AgentAttributeSet("owner", "empty",
                (List<Attribute<AgentSimulationContext>>) (List<?>) List.of());

        assertEquals(0, set.size());
    }

    // ---- Access control ----

    @Test
    void testPublicPropertyIsAccessible() {
        TestAttributes.CounterProperty prop = new TestAttributes.CounterProperty("counter");
        AgentAttributeSet set = TestAttributes.agentAttributeSet("owner", "s", prop);

        // getProperty is package-private on AttributeSet but public on AgentAttributeSet
        assertDoesNotThrow(() -> set.getProperty("counter"));
    }

    @Test
    void testPrivatePropertyThrowsOnAccess() {
        TestAttributes.PrivateCounterProperty prop = new TestAttributes.PrivateCounterProperty("secret", 0.0);

        @SuppressWarnings("unchecked")
        AgentAttributeSet set = new AgentAttributeSet("owner", "s",
                (List<Attribute<AgentSimulationContext>>) (List<?>) List.of(prop));

        assertThrows(AttributeAccessException.class, () -> set.getProperty("secret"),
                "Accessing a PRIVATE attribute should throw.");
    }

    // ---- Type-specific retrieval ----

    @Test
    void testGetEventByName() {
        TestAttributes.ThresholdEvent event = new TestAttributes.ThresholdEvent("eatFood", 0.5);
        AgentAttributeSet set = TestAttributes.agentAttributeSetFromEvents("owner", "food", event);

        assertDoesNotThrow(() -> set.getEvent("eatFood"));
    }

    @Test
    void testGetRoutineByName() {
        TestAttributes.InvocationCountingRoutine routine = new TestAttributes.InvocationCountingRoutine("tick");
        AgentAttributeSet set = TestAttributes.agentAttributeSetFromRoutines("owner", "sim", routine);

        assertDoesNotThrow(() -> set.getRoutine("tick"));
    }

    @Test
    void testGetEventThrowsWhenGivenAProperty() {
        TestAttributes.CounterProperty prop = new TestAttributes.CounterProperty("hp");
        AgentAttributeSet set = TestAttributes.agentAttributeSet("owner", "s", prop);

        assertThrows(AttributeAccessException.class, () -> set.getEvent("hp"),
                "Requesting an event by a property name should throw.");
    }

    // ---- Environment attribute sets ----

    @Test
    void testEnvironmentSetNameIsAssigned() {
        EnvironmentAttributeSet set = TestAttributes.emptyEnvironmentAttributeSet("env", "weather");
        assertEquals("weather", set.name());
    }

    @Test
    void testEnvironmentSetWithProperty() {
        TestAttributes.EnvironmentTickProperty prop = new TestAttributes.EnvironmentTickProperty("tick");
        EnvironmentAttributeSet set = TestAttributes.environmentAttributeSet("env", "timing", prop);

        assertEquals(1, set.size());
    }
}
