package modelarium.entities.attributes.properties.functional;

import modelarium.entities.contexts.AgentContext;

@FunctionalInterface
public interface AgentPropertyGetterFunction<T> {
    T get(AgentContext context, T propertyValue);
}
