package modelarium.entities.attributes.properties;

import modelarium.entities.attributes.ReadOnlyAttribute;

public final class ReadOnlyProperty<T> extends ReadOnlyAttribute<Property<T, ?>> {
    /**
     * Constructs a new immutable attribute wrapping the specified mutable attribute.
     *
     * @param attribute the mutable attribute to provide a read-only view of
     */
    ReadOnlyProperty(Property<T, ?> attribute) {
        super(attribute);
    }

    /**
     * Returns the class of the value this property carries.
     *
     * @return the property's value type
     */
    public Class<T> type() {
        return getMutableAttribute().type();
    }

    /**
     * Returns this property's current value.
     *
     * @return the property's value
     */
    public T get() {
        return getMutableAttribute().get();
    }
}
