package modelarium.attributes.builtins.actions;

import modelarium.ModelElement;
import modelarium.attributes.Property;
import modelarium.attributes.builtins.refs.DoubleValueRef;

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
        Property<?> property = element.getAttributeSetCollection().get(attributeSetName).getProperties().get(propertyName);

        if (property == null)
            throw new IllegalStateException("Property not found: " + propertyName + " in " + attributeSetName);

        if (!Double.class.equals(property.getType()))
            throw new IllegalStateException("Property is not Double: " + propertyName + " in " + attributeSetName);

        @SuppressWarnings("unchecked")
        Property<Double> doubleProperty = (Property<Double>) property;
        Double currentValue = doubleProperty.get();
        doubleProperty.set(currentValue + delta.resolve(element));
    }
}
