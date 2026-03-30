package unit.modelarium.attributes.builtins.events;

import modelarium.Entity;
import modelarium.attributes.builtins.actions.AddDoubleAction;
import modelarium.attributes.builtins.actions.SetBooleanAction;
import modelarium.attributes.builtins.events.ThresholdCrossingEvent;
import modelarium.attributes.builtins.events.ThresholdDirection;
import modelarium.attributes.builtins.events.TimerEvent;
import modelarium.attributes.builtins.refs.LiteralDoubleRef;
import modelarium.attributes.builtins.refs.PropertyDoubleRef;
import org.junit.jupiter.api.Test;
import unit.modelarium.attributes.builtins.BuiltinTestSupport;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuiltinEventTest {

    @Test
    void timerEventTriggersOnExactOffsetTick() {
        TimerEvent seedEvent = new TimerEvent("timer", 10, 3, List.of());

        var element = BuiltinTestSupport.elementWith(
                new modelarium.attributes.AttributeSet(
                        "core",
                        events(seedEvent),
                        new modelarium.attributes.Properties(),
                        new modelarium.attributes.Events()
                )
        );
        BuiltinTestSupport.attachClock(element, 3);

        TimerEvent event = (TimerEvent) element.getAttributeSetCollection()
                .get("core")
                .getPreEvents()
                .get("timer");

        assertTrue(event.isTriggered());
    }

    @Test
    void timerEventTriggersAgainOnLaterPeriodTick() {
        TimerEvent seedEvent = new TimerEvent("timer", 10, 3, List.of());

        var element = BuiltinTestSupport.elementWith(
                new modelarium.attributes.AttributeSet(
                        "core",
                        events(seedEvent),
                        new modelarium.attributes.Properties(),
                        new modelarium.attributes.Events()
                )
        );
        BuiltinTestSupport.attachClock(element, 13);

        TimerEvent event = (TimerEvent) element.getAttributeSetCollection()
                .get("core")
                .getPreEvents()
                .get("timer");

        assertTrue(event.isTriggered());
    }

    @Test
    void timerEventDoesNotTriggerBeforeOffset() {
        TimerEvent seedEvent = new TimerEvent("timer", 10, 3, List.of());

        var element = BuiltinTestSupport.elementWith(
                new modelarium.attributes.AttributeSet(
                        "core",
                        events(seedEvent),
                        new modelarium.attributes.Properties(),
                        new modelarium.attributes.Events()
                )
        );
        BuiltinTestSupport.attachClock(element, 2);

        TimerEvent event = (TimerEvent) element.getAttributeSetCollection()
                .get("core")
                .getPreEvents()
                .get("timer");

        assertFalse(event.isTriggered());
    }

    @Test
    void timerEventDoesNotTriggerBetweenPeriods() {
        TimerEvent seedEvent = new TimerEvent("timer", 10, 3, List.of());

        var element = BuiltinTestSupport.elementWith(
                new modelarium.attributes.AttributeSet(
                        "core",
                        events(seedEvent),
                        new modelarium.attributes.Properties(),
                        new modelarium.attributes.Events()
                )
        );
        BuiltinTestSupport.attachClock(element, 4);

        TimerEvent event = (TimerEvent) element.getAttributeSetCollection()
                .get("core")
                .getPreEvents()
                .get("timer");

        assertFalse(event.isTriggered());
    }

    @Test
    void timerEventConstructorRejectsPeriodLessThanOne() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TimerEvent("timer", 0, 0, List.of())
        );

        assertEquals("periodTicks must be >= 1", exception.getMessage());
    }

    @Test
    void timerEventConstructorRejectsNegativeOffset() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TimerEvent("timer", 1, -1, List.of())
        );

        assertEquals("offsetTicks must be >= 0", exception.getMessage());
    }

    @Test
    void timerEventRunAppliesAllConfiguredActions() {
        TimerEvent seedEvent = new TimerEvent(
                "timer",
                1,
                0,
                List.of(
                        new AddDoubleAction("core", "value", new LiteralDoubleRef(2.5)),
                        new SetBooleanAction("core", "flag", true)
                )
        );

        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet(
                        "core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 10.0),
                        BuiltinTestSupport.mutableBooleanProperty("flag", false)
                )
        );
        BuiltinTestSupport.attachClock(element, 0);

        TimerEvent event = new TimerEvent(
                "timer",
                1,
                0,
                List.of(
                        new AddDoubleAction("core", "value", new LiteralDoubleRef(2.5)),
                        new SetBooleanAction("core", "flag", true)
                )
        );
        event.setAssociatedModelElement(element);
        event.run();

        var value = element.getAttributeSetCollection().get("core").getProperties().get("value");
        var flag = element.getAttributeSetCollection().get("core").getProperties().get("flag");

        assertEquals(12.5, value.get());
        assertEquals(true, flag.get());
    }

    @Test
    void thresholdCrossingEventFirstCheckReturnsFalse() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 5.0))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        ThresholdCrossingEvent event = new ThresholdCrossingEvent(
                "threshold",
                "core",
                "value",
                new LiteralDoubleRef(10.0),
                ThresholdDirection.RISING_CROSS,
                List.of()
        );
        event.setAssociatedModelElement(element);

        assertFalse(event.isTriggered());
    }

    @Test
    void thresholdCrossingEventTriggersOnRisingCross() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 5.0))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        ThresholdCrossingEvent event = new ThresholdCrossingEvent(
                "threshold",
                "core",
                "value",
                new LiteralDoubleRef(10.0),
                ThresholdDirection.RISING_CROSS,
                List.of()
        );
        event.setAssociatedModelElement(element);

        var value = getDoubleProperty(element, "core", "value");

        assertFalse(event.isTriggered());

        value.set(10.0);
        assertFalse(event.isTriggered());

        value.set(11.0);
        assertTrue(event.isTriggered());

        value.set(12.0);
        assertFalse(event.isTriggered());
    }

    @Test
    void thresholdCrossingEventTriggersOnFallingCross() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 15.0))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        ThresholdCrossingEvent event = new ThresholdCrossingEvent(
                "threshold",
                "core",
                "value",
                new LiteralDoubleRef(10.0),
                ThresholdDirection.FALLING_CROSS,
                List.of()
        );
        event.setAssociatedModelElement(element);

        var value = getDoubleProperty(element, "core", "value");

        assertFalse(event.isTriggered());

        value.set(10.0);
        assertFalse(event.isTriggered());

        value.set(9.0);
        assertTrue(event.isTriggered());

        value.set(8.0);
        assertFalse(event.isTriggered());
    }

    @Test
    void thresholdCrossingEventCanUsePropertyBackedThreshold() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 9.0)),
                BuiltinTestSupport.attributeSet("thresholdSet",
                        BuiltinTestSupport.mutableDoubleProperty("threshold", 10.0))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        ThresholdCrossingEvent event = new ThresholdCrossingEvent(
                "threshold",
                "core",
                "value",
                new PropertyDoubleRef("thresholdSet", "threshold"),
                ThresholdDirection.RISING_CROSS,
                List.of()
        );
        event.setAssociatedModelElement(element);

        var value = getDoubleProperty(element, "core", "value");

        assertFalse(event.isTriggered());

        value.set(11.0);
        assertTrue(event.isTriggered());
    }

    @Test
    void thresholdCrossingEventRunAppliesAllConfiguredActions() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet(
                        "core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 5.0),
                        BuiltinTestSupport.mutableDoubleProperty("target", 1.0),
                        BuiltinTestSupport.mutableBooleanProperty("flag", false)
                )
        );
        BuiltinTestSupport.attachWithoutClock(element);

        ThresholdCrossingEvent event = new ThresholdCrossingEvent(
                "threshold",
                "core",
                "value",
                new LiteralDoubleRef(10.0),
                ThresholdDirection.RISING_CROSS,
                List.of(
                        new AddDoubleAction("core", "target", new LiteralDoubleRef(4.0)),
                        new SetBooleanAction("core", "flag", true)
                )
        );
        event.setAssociatedModelElement(element);

        event.run();

        var target = element.getAttributeSetCollection().get("core").getProperties().get("target");
        var flag = element.getAttributeSetCollection().get("core").getProperties().get("flag");

        assertEquals(5.0, target.get());
        assertEquals(true, flag.get());
    }

    private static modelarium.attributes.Events events(modelarium.attributes.Event... events) {
        modelarium.attributes.Events collection = new modelarium.attributes.Events();
        for (modelarium.attributes.Event event : events)
            collection.add(event);
        return collection;
    }

    @SuppressWarnings("unchecked")
    private static modelarium.attributes.Property<Double> getDoubleProperty(
            Entity element,
            String attributeSetName,
            String propertyName
    ) {
        return (modelarium.attributes.Property<Double>) element.getAttributeSetCollection()
                .get(attributeSetName)
                .getProperties()
                .get(propertyName);
    }
}