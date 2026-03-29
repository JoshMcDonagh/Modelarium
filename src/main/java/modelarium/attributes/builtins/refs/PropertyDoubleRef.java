package modelarium.attributes.builtins.refs;

import modelarium.ModelElement;
import modelarium.attributes.Property;
import modelarium.attributes.builtins.util.BuiltinLookup;

/**
 * A {@link DoubleValueRef} that resolves to the current value of a {@code Double} property on a
 * {@link modelarium.ModelElement}.
 *
 * <p>The target property is identified by attribute set name and property name. Resolution performs
 * a lookup at runtime, allowing built-in actions, properties, and events to depend dynamically on
 * the model element's current state.</p>
 *
 * <p>This is useful when, for example, a threshold, increment, or assigned value should come from
 * another property rather than from a fixed literal.</p>
 */
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
