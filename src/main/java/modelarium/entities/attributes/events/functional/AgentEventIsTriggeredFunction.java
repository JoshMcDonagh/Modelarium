package modelarium.entities.attributes.events.functional;

import modelarium.entities.contexts.AgentContext;

@FunctionalInterface
public interface AgentEventIsTriggeredFunction {
    boolean isTriggered(AgentContext context);
}
