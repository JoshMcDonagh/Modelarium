package modelarium.attributes.builtins.actions;

import modelarium.ModelElement;
import modelarium.attributes.Property;
import modelarium.attributes.builtins.refs.DoubleValueRef;

public class SetDoubleAction implements Action {
    private final String attributeSetName;
    private final String propertyName;
    private final DoubleValueRef value;

    public SetDoubleAction(String attributeSetName, String propertyName, DoubleValueRef value) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public void apply(ModelElement element) {
        Property<?> property = element.getAttributeSetCollection().get(attributeSetName).getProperties().get(propertyName);

        if (property == null)
            throw new IllegalStateException("Property not found: " + propertyName + " in " + attributeSetName);

        if (!Double.class.equals(property.getType()))
            throw new IllegalStateException("Property is not Double: " + propertyName + " in " + attributeSetName);

        @SuppressWarnings("unchecked")
        Property<Double> doubleProperty = (Property<Double>) property;
        doubleProperty.set(value.resolve(element));
    }
}
