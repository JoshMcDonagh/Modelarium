package modelarium.entities.attributes.events.functional;

import modelarium.entities.contexts.AgentContext;

@FunctionalInterface
public interface AgentEventRunFunction {
    void run(AgentContext context);
}
