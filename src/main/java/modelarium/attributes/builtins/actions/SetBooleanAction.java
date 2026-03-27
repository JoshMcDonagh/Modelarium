package modelarium.attributes.builtins.actions;

import modelarium.ModelElement;
import modelarium.attributes.Property;
import modelarium.attributes.builtins.util.BuiltinLookup;

public class SetBooleanAction implements Action {
    private final String attributeSetName;
    private final String propertyName;
    private final boolean value;

    public SetBooleanAction(String attributeSetName, String propertyName, boolean value) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public void apply(ModelElement element) {
        Property<Boolean> property = BuiltinLookup.getRequiredBooleanProperty(element, attributeSetName, propertyName);
        property.set(value);
    }
}
