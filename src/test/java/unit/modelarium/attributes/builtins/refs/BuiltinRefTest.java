package unit.modelarium.attributes.builtins.refs;

import modelarium.attributes.functional.properties.FunctionalProperty;
import modelarium.attributes.builtins.refs.LiteralDoubleRef;
import modelarium.attributes.builtins.refs.PropertyDoubleRef;
import org.junit.jupiter.api.Test;
import unit.modelarium.attributes.builtins.BuiltinTestSupport;

import static org.junit.jupiter.api.Assertions.*;

class BuiltinRefTest {

    @Test
    void literalDoubleRefReturnsLiteralValue() {
        LiteralDoubleRef ref = new LiteralDoubleRef(7.25);

        assertEquals(7.25, ref.resolve(null));
    }

    @Test
    void propertyDoubleRefReadsValueFromTargetProperty() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 42.0))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        PropertyDoubleRef ref = new PropertyDoubleRef("core", "value");

        assertEquals(42.0, ref.resolve(element));
    }

    @Test
    void propertyDoubleRefThrowsWhenElementIsNull() {
        PropertyDoubleRef ref = new PropertyDoubleRef("core", "value");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ref.resolve(null));

        assertEquals("ModelElement is null", exception.getMessage());
    }

    @Test
    void propertyDoubleRefThrowsWhenAttributeSetMissing() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("other")
        );
        BuiltinTestSupport.attachWithoutClock(element);

        PropertyDoubleRef ref = new PropertyDoubleRef("core", "value");

        assertThrows(NullPointerException.class, () -> ref.resolve(element));
    }

    @Test
    void propertyDoubleRefThrowsWhenPropertyMissing() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core")
        );
        BuiltinTestSupport.attachWithoutClock(element);

        PropertyDoubleRef ref = new PropertyDoubleRef("core", "value");

        assertThrows(NullPointerException.class, () -> ref.resolve(element));
    }

    @Test
    void propertyDoubleRefThrowsWhenPropertyWrongType() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableBooleanProperty("value", true))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        PropertyDoubleRef ref = new PropertyDoubleRef("core", "value");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ref.resolve(element));

        assertEquals("Property 'value' is not a Double", exception.getMessage());
    }

    @Test
    void propertyDoubleRefThrowsWhenPropertyValueIsNull() {
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

        PropertyDoubleRef ref = new PropertyDoubleRef("core", "value");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ref.resolve(element));

        assertEquals("Property 'value' has null value", exception.getMessage());
    }
}