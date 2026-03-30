package modelarium.attributes.builtins.refs;

import modelarium.Entity;
import modelarium.attributes.builtins.util.BuiltinLookup;

/**
 * A {@link DoubleValueRef} that resolves to the current value of a {@code Double} property on a
 * {@link Entity}.
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

    /**
     * Creates a value reference that reads from a target {@code Double} property.
     *
     * @param attributeSetName the name of the attribute set containing the property
     * @param propertyName the name of the property
     */
    public PropertyDoubleRef(String attributeSetName, String propertyName) {
        this.attributeSetName = attributeSetName;
        this.propertyName = propertyName;
    }

    /**
     * Resolves and returns the current value of the referenced property.
     *
     * @param element the model element from which to read the property value
     * @return the current value of the referenced property
     * @throws IllegalStateException if the property cannot be found, is not a
     * {@code Double} property, or has a null value
     */
    @Override
    public double resolve(Entity element) {
        return BuiltinLookup.getRequiredDoublePropertyValue(element, attributeSetName, propertyName);
    }
}
