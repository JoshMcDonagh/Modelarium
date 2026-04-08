package modelarium.entities.attributes.properties.functional;

import modelarium.entities.contexts.AgentContext;

@FunctionalInterface
public interface AgentPropertyRunFunction<T> {
    T run(AgentContext context, T propertyValue);
}
