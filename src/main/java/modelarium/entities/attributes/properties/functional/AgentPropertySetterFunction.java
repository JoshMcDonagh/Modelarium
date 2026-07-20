package modelarium.entities.attributes.properties.functional;

import modelarium.entities.contexts.AgentContext;

/**
 * Functional interface for defining the setter logic of a functional agent property.
 *
 * @param <T> the type of value the property carries
 */
@FunctionalInterface
public interface AgentPropertySetterFunction<T> {

    /**
     * Sets the property's value.
     *
     * @param context the context the property can use in its setter logic
     * @param currentPropertyValue the property's currently stored value
     * @param newPropertyValue the value the property is being set to
     * @return the value the property should store after the set
     */
    T set(AgentContext context, T currentPropertyValue, T newPropertyValue);
}
