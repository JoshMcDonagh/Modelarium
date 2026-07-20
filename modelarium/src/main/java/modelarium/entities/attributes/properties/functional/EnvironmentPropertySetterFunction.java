package modelarium.entities.attributes.properties.functional;

import modelarium.entities.contexts.EnvironmentContext;

/**
 * Functional interface for defining the setter logic of a functional environment property.
 *
 * @param <T> the type of value the property carries
 */
@FunctionalInterface
public interface EnvironmentPropertySetterFunction<T> {

    /**
     * Sets the property's value.
     *
     * @param context the context the property can use in its setter logic
     * @param currentPropertyValue the property's currently stored value
     * @param newPropertyValue the value the property is being set to
     * @return the value the property should store after the set
     */
    T set(EnvironmentContext context, T currentPropertyValue, T newPropertyValue);
}
