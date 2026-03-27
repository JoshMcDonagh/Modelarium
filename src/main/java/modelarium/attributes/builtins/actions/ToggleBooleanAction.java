package modelarium.attributes.builtins.actions;

import modelarium.ModelElement;
import modelarium.attributes.Property;
import modelarium.attributes.builtins.util.BuiltinLookup;

public class ToggleBooleanAction implements Action {
    private final String attributeSetName;
    private final String propertyName;

    public ToggleBooleanAction(String attributeSetName, String propertyName) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
    }

    @Override
    public void apply(ModelElement element) {
        Property<Boolean> property = BuiltinLookup.getRequiredBooleanProperty(element, attributeSetName, propertyName);
        property.set(!property.get());
    }
}
