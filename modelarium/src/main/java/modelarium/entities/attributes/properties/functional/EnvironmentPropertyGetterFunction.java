package modelarium.entities.attributes.properties.functional;

import modelarium.entities.contexts.EnvironmentContext;

/**
 * Functional interface for defining the getter logic of a functional environment property.
 *
 * @param <T> the type of value the property carries
 */
@FunctionalInterface
public interface EnvironmentPropertyGetterFunction<T> {

    /**
     * Returns the property's value.
     *
     * @param context the context the property can use in its getter logic
     * @param propertyValue the property's currently stored value
     * @return the value the property should report
     */
    T get(EnvironmentContext context, T propertyValue);
}
