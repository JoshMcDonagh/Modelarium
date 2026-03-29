package unit.modelarium.attributes.builtins.actions;

import modelarium.attributes.builtins.actions.AddDoubleAction;
import modelarium.attributes.builtins.actions.SetBooleanAction;
import modelarium.attributes.builtins.actions.SetDoubleAction;
import modelarium.attributes.builtins.actions.ToggleBooleanAction;
import modelarium.attributes.builtins.refs.LiteralDoubleRef;
import modelarium.attributes.builtins.refs.PropertyDoubleRef;
import org.junit.jupiter.api.Test;
import unit.modelarium.attributes.builtins.BuiltinTestSupport;

import static org.junit.jupiter.api.Assertions.*;

class BuiltinActionTest {

    @Test
    void addDoubleActionAddsResolvedDeltaToTargetProperty() {
        AddDoubleAction seedAction = new AddDoubleAction("core", "value", new LiteralDoubleRef(2.5));

        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 10.0))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        seedAction.apply(element);

        var value = element.getAttributeSetCollection()
                .get("core")
                .getProperties()
                .get("value");

        assertEquals(12.5, value.get());
    }

    @Test
    void setDoubleActionSetsLiteralValueOnTargetProperty() {
        SetDoubleAction seedAction = new SetDoubleAction("core", "value", new LiteralDoubleRef(7.25));

        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 10.0))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        seedAction.apply(element);

        var value = element.getAttributeSetCollection()
                .get("core")
                .getProperties()
                .get("value");

        assertEquals(7.25, value.get());
    }

    @Test
    void setDoubleActionSetsResolvedPropertyValueOnTargetProperty() {
        SetDoubleAction seedAction = new SetDoubleAction(
                "core",
                "value",
                new PropertyDoubleRef("sourceSet", "source")
        );

        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableDoubleProperty("value", 10.0)),
                BuiltinTestSupport.attributeSet("sourceSet",
                        BuiltinTestSupport.mutableDoubleProperty("source", 42.5))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        seedAction.apply(element);

        var value = element.getAttributeSetCollection()
                .get("core")
                .getProperties()
                .get("value");

        assertEquals(42.5, value.get());
    }

    @Test
    void setBooleanActionSetsTargetProperty() {
        SetBooleanAction seedAction = new SetBooleanAction("core", "flag", true);

        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableBooleanProperty("flag", false))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        seedAction.apply(element);

        var flag = element.getAttributeSetCollection()
                .get("core")
                .getProperties()
                .get("flag");

        assertEquals(true, flag.get());
    }

    @Test
    void toggleBooleanActionFlipsTargetProperty() {
        ToggleBooleanAction seedAction = new ToggleBooleanAction("core", "flag");

        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableBooleanProperty("flag", false))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        seedAction.apply(element);

        var flag = element.getAttributeSetCollection()
                .get("core")
                .getProperties()
                .get("flag");

        assertEquals(true, flag.get());

        seedAction.apply(element);

        assertEquals(false, flag.get());
    }

    @Test
    void addDoubleActionThrowsWhenTargetAttributeSetMissing() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("other")
        );
        BuiltinTestSupport.attachWithoutClock(element);

        AddDoubleAction action = new AddDoubleAction("core", "value", new LiteralDoubleRef(1.0));

        assertThrows(NullPointerException.class, () -> action.apply(element));
    }

    @Test
    void setDoubleActionThrowsWhenTargetPropertyMissing() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core")
        );
        BuiltinTestSupport.attachWithoutClock(element);

        SetDoubleAction action = new SetDoubleAction("core", "value", new LiteralDoubleRef(1.0));

        assertThrows(NullPointerException.class, () -> action.apply(element));
    }

    @Test
    void toggleBooleanActionThrowsWhenTargetPropertyWrongType() {
        var element = BuiltinTestSupport.elementWith(
                BuiltinTestSupport.attributeSet("core",
                        BuiltinTestSupport.mutableDoubleProperty("flag", 1.0))
        );
        BuiltinTestSupport.attachWithoutClock(element);

        ToggleBooleanAction action = new ToggleBooleanAction("core", "flag");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> action.apply(element));

        assertEquals("Property 'flag' is not a Boolean", exception.getMessage());
    }
}