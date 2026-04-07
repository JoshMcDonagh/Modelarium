package unit.modelarium.attributes.builtins.properties;

import modelarium.entities.attributes.builtins.properties.AddDoubleProperty;
import modelarium.entities.attributes.builtins.properties.ClampDoubleProperty;
import modelarium.entities.attributes.builtins.properties.ConstantDoubleProperty;
import modelarium.entities.attributes.builtins.properties.CopyDoubleProperty;
import modelarium.entities.attributes.builtins.refs.LiteralDoubleRef;
import org.junit.jupiter.api.Test;
import unit.modelarium.attributes.builtins.BuiltinTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuiltinPropertyTest {

    @Test
    void addDoublePropertyAddsResolvedDeltaOnRun() {
        AddDoubleProperty property = new AddDoubleProperty("value", 10.0, new LiteralDoubleRef(2.5));
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core", property)
        );
        BuiltinTestSupport.attachWithoutClock(element);

        property.run();

        assertEquals(12.5, property.get());
    }

    @Test
    void clampDoublePropertyClampsWithinBounds() {
        ClampDoubleProperty property = new ClampDoubleProperty(
                "value",
                12.0,
                new LiteralDoubleRef(0.0),
                new LiteralDoubleRef(10.0)
        );
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core", property)
        );
        BuiltinTestSupport.attachWithoutClock(element);

        property.run();

        assertEquals(10.0, property.get());
    }

    @Test
    void clampDoublePropertyThrowsWhenMinGreaterThanMax() {
        ClampDoubleProperty property = new ClampDoubleProperty(
                "value",
                5.0,
                new LiteralDoubleRef(10.0),
                new LiteralDoubleRef(0.0)
        );
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core", property)
        );
        BuiltinTestSupport.attachWithoutClock(element);

        IllegalStateException exception = assertThrows(IllegalStateException.class, property::run);

        assertEquals("ClampDoubleProperty min cannot be greater than max", exception.getMessage());
    }

    @Test
    void constantDoublePropertyCannotBeSet() {
        ConstantDoubleProperty property = new ConstantDoubleProperty("constant", 7.5);

        UnsupportedOperationException exception =
                assertThrows(UnsupportedOperationException.class, () -> property.set(3.0));

        assertEquals("ConstantDoubleProperty cannot be modified after construction", exception.getMessage());
        assertEquals(7.5, property.get());
    }

    @Test
    void copyDoublePropertyReadsValueFromSourceProperty() {
        var source = BuiltinTestSupport.mutableDoubleProperty("source", 42.0);
        CopyDoubleProperty seedCopy = new CopyDoubleProperty("copy", "sourceSet", "source");

        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("sourceSet", source),
                BuiltinTestSupport.attributeSet("derivedSet", seedCopy)
        );
        BuiltinTestSupport.attachWithoutClock(element);

        CopyDoubleProperty copy = (CopyDoubleProperty) element
                .getAttributeSetCollection()
                .get("derivedSet")
                .getProperties()
                .get("copy");

        assertEquals(42.0, copy.get());
    }

    @Test
    void copyDoublePropertyThrowsWhenNotAssociatedWithElement() {
        CopyDoubleProperty copy = new CopyDoubleProperty("sourceSet", "source");

        IllegalStateException exception = assertThrows(IllegalStateException.class, copy::get);

        assertEquals("CopyDoubleProperty has no associated ModelElement", exception.getMessage());
    }

    @Test
    void copyDoublePropertyThrowsWhenSourcePropertyMissing() {
        CopyDoubleProperty seedCopy = new CopyDoubleProperty("copy", "sourceSet", "missing");

        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("sourceSet"),
                BuiltinTestSupport.attributeSet("derivedSet", seedCopy)
        );
        BuiltinTestSupport.attachWithoutClock(element);

        CopyDoubleProperty copy = (CopyDoubleProperty) element
                .getAttributeSetCollection()
                .get("derivedSet")
                .getProperties()
                .get("copy");

        IllegalStateException exception = assertThrows(IllegalStateException.class, copy::get);

        assertEquals("Source property not found: missing in sourceSet", exception.getMessage());
    }
}