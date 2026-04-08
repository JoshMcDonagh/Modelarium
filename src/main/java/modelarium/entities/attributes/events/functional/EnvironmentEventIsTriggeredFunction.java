package modelarium.entities.attributes.events.functional;

import modelarium.entities.contexts.EnvironmentContext;

@FunctionalInterface
public interface EnvironmentEventIsTriggeredFunction {
    boolean isTriggered(EnvironmentContext context);
}
