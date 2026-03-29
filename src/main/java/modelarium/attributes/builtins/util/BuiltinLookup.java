package modelarium.attributes.builtins.util;

import modelarium.ModelElement;
import modelarium.attributes.AttributeSet;
import modelarium.attributes.Properties;
import modelarium.attributes.Property;

/**
 * Utility methods for resolving properties required by built-in actions, properties, and events.
 *
 * <p>{@code BuiltinLookup} centralises the common lookup and validation logic needed by the built-in
 * attribute classes. It provides methods to retrieve required properties from a model element and
 * to enforce expected types such as {@code Double} and {@code Boolean}.</p>
 *
 * <p>If a required model element, attribute set, property collection, property, or property value
 * is missing or of the wrong type, these methods throw an {@link IllegalStateException}. This keeps
 * failures explicit and close to the point of misuse.</p>
 *
 * <p>This class is an internal support utility for the built-ins package rather than a primary API
 * type for general model construction.</p>
 */
public final class BuiltinLookup {
    private BuiltinLookup() {}

    public static Property<?> getRequiredProperty(
            ModelElement element,
            String attributeSetName,
            String propertyName
    ) {
        if (element == null)
            throw new IllegalStateException("ModelElement is null");

        AttributeSet attributeSet = element.getAttributeSetCollection().get(attributeSetName);
        if (attributeSet == null)
            throw new IllegalStateException("AttributeSet not found: " + attributeSetName);

        Properties properties = attributeSet.getProperties();
        if (properties == null)
            throw new IllegalStateException("AttributeSet has no Properties collection");

        Property<?> property = properties.get(propertyName);
        if (property == null)
            throw new IllegalStateException("Property not found: " + propertyName);

        return property;
    }

    @SuppressWarnings("unchecked")
    public static Property<Double> getRequiredDoubleProperty(
            ModelElement element,
            String attributeSetName,
            String propertyName
    ) {
        Property<?> property = getRequiredProperty(element, attributeSetName, propertyName);

        if (!Double.class.equals(property.getType()))
            throw new IllegalStateException("Property '" + propertyName + "' is not a Double");

        return (Property<Double>) property;
    }

    @SuppressWarnings("unchecked")
    public static Property<Boolean> getRequiredBooleanProperty(
            ModelElement element,
            String attributeSetName,
            String propertyName
    ) {
        Property<?> property = getRequiredProperty(element, attributeSetName, propertyName);

        if (!Boolean.class.equals(property.getType()))
            throw new IllegalStateException("Property '" + propertyName + "' is not a Boolean");

        return (Property<Boolean>) property;
    }

    public static double getRequiredDoublePropertyValue(
            ModelElement element,
            String attributeSetName,
            String propertyName
    ) {
        Double value = getRequiredDoubleProperty(element, attributeSetName, propertyName).get();

        if (value == null)
            throw new IllegalStateException("Property '" + propertyName + "' has null value");

        return value;
    }
}
