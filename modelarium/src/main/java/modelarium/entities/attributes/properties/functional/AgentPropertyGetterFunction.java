package modelarium.entities.attributes.properties.functional;

import modelarium.entities.contexts.AgentContext;

/**
 * Functional interface for defining the getter logic of a functional agent property.
 *
 * @param <T> the type of value the property carries
 */
@FunctionalInterface
public interface AgentPropertyGetterFunction<T> {

    /**
     * Returns the property's value.
     *
     * @param context the context the property can use in its getter logic
     * @param propertyValue the property's currently stored value
     * @return the value the property should report
     */
    T get(AgentContext context, T propertyValue);
}
