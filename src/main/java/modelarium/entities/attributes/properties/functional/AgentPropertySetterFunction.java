package modelarium.entities.attributes.properties.functional;

import modelarium.entities.contexts.AgentContext;

@FunctionalInterface
public interface AgentPropertySetterFunction<T> {
    T set(AgentContext context, T currentPropertyValue, T newPropertyValue);
}
