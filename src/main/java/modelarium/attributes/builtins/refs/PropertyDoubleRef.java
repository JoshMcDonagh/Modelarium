package modelarium.attributes.builtins.refs;

import modelarium.ModelElement;
import modelarium.attributes.Property;

public class PropertyDoubleRef implements DoubleValueRef {
    private final String attributeSetName;
    private final String propertyName;

    public PropertyDoubleRef(String attributeSetName, String propertyName) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
    }

    @Override
    public double resolve(ModelElement element) {
        if (element == null)
            throw new IllegalStateException("Cannot resolve property ref without a ModelElement");

        Property<?> property = element.getAttributeSetCollection().get(attributeSetName).getProperties().get(propertyName);

        if (property == null)
            throw new IllegalStateException("Property not found: " + propertyName + " in " + attributeSetName);

        Object value = property.get();
        if (!(value instanceof Double doubleValue))
            throw new IllegalStateException("Property '" + propertyName + " in " + attributeSetName + "' is not a Double");

        return doubleValue;
    }
}
