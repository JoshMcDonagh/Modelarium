package unit.modelarium.attributes.builtins.util;

import modelarium.entities.attributes.builtins.util.BuiltinLookup;
import modelarium.entities.attributes.functional.properties.FunctionalProperty;
import org.junit.jupiter.api.Test;
import unit.modelarium.attributes.builtins.BuiltinTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuiltinLookupTest {

    @Test
    void getRequiredPropertyReturnsPropertyWhenPresent() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 12.5))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        var property = BuiltinLookup.getRequiredProperty(element, "core", "value");

        assertEquals("value", property.getName());
        assertEquals(Double.class, property.getType());
        assertEquals(12.5, property.get());
    }

    @Test
    void getRequiredPropertyThrowsWhenElementIsNull() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> BuiltinLookup.getRequiredProperty(null, "core", "value")
        );

        assertEquals("ModelElement is null", exception.getMessage());
    }

    @Test
    void getRequiredPropertyThrowsWhenAttributeSetMissing() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("other")
        );
        BuiltinTestSupport.attachWithoutClock(element);

        assertThrows(
                NullPointerException.class,
                () -> BuiltinLookup.getRequiredProperty(element, "core", "value")
        );
    }

    @Test
    void getRequiredPropertyThrowsWhenPropertyMissing() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core")
        );
        BuiltinTestSupport.attachWithoutClock(element);

        assertThrows(
                NullPointerException.class,
                () -> BuiltinLookup.getRequiredProperty(element, "core", "value")
        );
    }

    @Test
    void getRequiredDoublePropertyThrowsWhenPropertyIsNotDouble() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableBooleanProperty("value", true))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> BuiltinLookup.getRequiredDoubleProperty(element, "core", "value")
        );

        assertEquals("Property 'value' is not a Double", exception.getMessage());
    }

    @Test
    void getRequiredBooleanPropertyThrowsWhenPropertyIsNotBoolean() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 1.0))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> BuiltinLookup.getRequiredBooleanProperty(element, "core", "value")
        );

        assertEquals("Property 'value' is not a Boolean", exception.getMessage());
    }

    @Test
    void getRequiredDoublePropertyValueThrowsWhenValueIsNull() {
        FunctionalProperty<Double> value = new FunctionalProperty<>(
                "value",
                true,
                Double.class,
                (element, currentValue) -> currentValue,
                (element, currentValue, newValue) -> newValue,
                (element, currentValue) -> currentValue
        );

        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core", value)
        );
        BuiltinTestSupport.attachWithoutClock(element);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> BuiltinLookup.getRequiredDoublePropertyValue(element, "core", "value")
        );

        assertEquals("Property 'value' has null value", exception.getMessage());
    }
}