package modelarium.attributes.builtins.refs;

import modelarium.ModelElement;
import modelarium.attributes.Property;
import modelarium.attributes.builtins.util.BuiltinLookup;

public class PropertyDoubleRef implements DoubleValueRef {
    private final String attributeSetName;
    private final String propertyName;

    public PropertyDoubleRef(String attributeSetName, String propertyName) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
    }

    @Override
    public double resolve(ModelElement element) {
        return BuiltinLookup.getRequiredDoublePropertyValue(element, attributeSetName, propertyName);
    }
}
