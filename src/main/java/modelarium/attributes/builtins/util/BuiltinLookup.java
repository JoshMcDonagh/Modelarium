package modelarium.attributes.builtins.util;

import modelarium.Entity;

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

    /**
     * Returns the required property from the supplied model element.
     *
     * @param element the model element containing the property
     * @param attributeSetName the name of the attribute set containing the property
     * @param propertyName the name of the property
     * @return the resolved property
     * @throws IllegalStateException if the model element is {@code null}, the
     * attribute set cannot be found, the properties collection is missing, or the
     * property cannot be found
     */
    public static Property<?> getRequiredProperty(
            Entity element,
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

    /**
     * Returns the required property as a {@code Double} property.
     *
     * @param element the model element containing the property
     * @param attributeSetName the name of the attribute set containing the property
     * @param propertyName the name of the property
     * @return the resolved property as a {@code Double} property
     * @throws IllegalStateException if the property cannot be found or is not of
     * type {@code Double}
     */
    @SuppressWarnings("unchecked")
    public static Property<Double> getRequiredDoubleProperty(
            Entity element,
            String attributeSetName,
            String propertyName
    ) {
        Property<?> property = getRequiredProperty(element, attributeSetName, propertyName);

        if (!Double.class.equals(property.getType()))
            throw new IllegalStateException("Property '" + propertyName + "' is not a Double");

        return (Property<Double>) property;
    }

    /**
     * Returns the required property as a {@code Boolean} property.
     *
     * @param element the model element containing the property
     * @param attributeSetName the name of the attribute set containing the property
     * @param propertyName the name of the property
     * @return the resolved property as a {@code Boolean} property
     * @throws IllegalStateException if the property cannot be found or is not of
     * type {@code Boolean}
     */
    @SuppressWarnings("unchecked")
    public static Property<Boolean> getRequiredBooleanProperty(
            Entity element,
            String attributeSetName,
            String propertyName
    ) {
        Property<?> property = getRequiredProperty(element, attributeSetName, propertyName);

        if (!Boolean.class.equals(property.getType()))
            throw new IllegalStateException("Property '" + propertyName + "' is not a Boolean");

        return (Property<Boolean>) property;
    }

    /**
     * Returns the current value of a required {@code Double} property.
     *
     * @param element the model element containing the property
     * @param attributeSetName the name of the attribute set containing the property
     * @param propertyName the name of the property
     * @return the property's current non-null value
     * @throws IllegalStateException if the property cannot be found, is not of
     * type {@code Double}, or currently has a {@code null} value
     */
    public static double getRequiredDoublePropertyValue(
            Entity element,
            String attributeSetName,
            String propertyName
    ) {
        Double value = getRequiredDoubleProperty(element, attributeSetName, propertyName).get();

        if (value == null)
            throw new IllegalStateException("Property '" + propertyName + "' has null value");

        return value;
    }
}
