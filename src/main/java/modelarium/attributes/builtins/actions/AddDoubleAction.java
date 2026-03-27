package modelarium.attributes.builtins.actions;

import modelarium.ModelElement;
import modelarium.attributes.Property;
import modelarium.attributes.builtins.refs.DoubleValueRef;
import modelarium.attributes.builtins.util.BuiltinLookup;

public class AddDoubleAction implements Action {
    private final String attributeSetName;
    private final String propertyName;
    private final DoubleValueRef delta;

    public AddDoubleAction(String attributeSetName, String propertyName, DoubleValueRef delta) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.delta = delta;
    }

    @Override
    public void apply(ModelElement element) {
        Property<Double> property = BuiltinLookup.getRequiredDoubleProperty(element, attributeSetName, propertyName);
        property.set(property.get() + delta.resolve(element));
    }
}
