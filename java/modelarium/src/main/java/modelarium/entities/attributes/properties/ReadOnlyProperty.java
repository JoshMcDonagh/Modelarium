package modelarium.entities.attributes.properties;

import modelarium.entities.attributes.ReadOnlyAttribute;
import modelarium.internal.Internal;
import modelarium.python.PythonObject;
import modelarium.python.entities.attributes.properties.functional.PythonFunctionalProperty;
import modelarium.utils.Cloners;

/**
 * Provides a read-only view of a typed property attribute.
 *
 * @param <T> the type of value carried by the property
 */
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
     * Returns the string representation of the property value.
     *
     * @return the property's value as a string
     */
    @Override
    public String toString() {
        return getMutableAttribute().toString();
    }

    /**
     * Returns this property's current value.
     *
     * @return the property's value
     */
    public T get() {
        return Cloners.standard().deepClone(getMutableAttribute()).get();
    }

    /**
     * Returns the Python type associated with this property.
     *
     * <p>This method is intended for internal use and is only supported when the
     * wrapped mutable property is backed by a Python functional property.
     *
     * @return the stored Python type for this property
     * @throws UnsupportedOperationException if this property is not backed by a Python functional property
     */
    @Internal
    public PythonObject getPythonType() {
        if (getMutableAttribute() instanceof PythonFunctionalProperty pythonProperty)
            return pythonProperty.getPythonType();

        throw new UnsupportedOperationException("This is not a Python-backed property");
    }
}
