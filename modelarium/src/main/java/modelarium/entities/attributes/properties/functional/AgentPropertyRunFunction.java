package modelarium.entities.attributes.properties.functional;

import modelarium.entities.contexts.AgentContext;

/**
 * Functional interface for defining the behaviour of a functional agent property.
 *
 * @param <T> the type of value the property carries
 */
@FunctionalInterface
public interface AgentPropertyRunFunction<T> {

    /**
     * Runs the property's behaviour.
     *
     * @param context the context the property can use in its behaviour
     * @param propertyValue the property's currently stored value
     * @return the value the property should store after the behaviour has run
     */
    T run(AgentContext context, T propertyValue);
}
