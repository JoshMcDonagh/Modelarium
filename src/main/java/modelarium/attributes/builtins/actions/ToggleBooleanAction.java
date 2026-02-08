package modelarium.attributes.builtins.actions;

import modelarium.ModelElement;
import modelarium.attributes.Property;

public class ToggleBooleanAction implements Action {
    private final String attributeSetName;
    private final String propertyName;

    public ToggleBooleanAction(String attributeSetName, String propertyName) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
    }

    @Override
    public void apply(ModelElement element) {
        Property<?> property = element.getAttributeSetCollection().get(attributeSetName).getProperties().get(propertyName);

        if (property == null)
            throw new IllegalStateException("Property not found: " + propertyName + " in " + attributeSetName);

        if (!Boolean.class.equals(property.getType()))
            throw new IllegalStateException("Property is not Boolean: " + propertyName + " in " + attributeSetName);

        @SuppressWarnings("unchecked")
        Property<Boolean> booleanProperty = (Property<Boolean>) property;
        booleanProperty.set(!booleanProperty.get());
    }
}
