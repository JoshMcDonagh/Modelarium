package modelarium.entities.attributes.events.functional;

import modelarium.entities.contexts.EnvironmentContext;

@FunctionalInterface
public interface EnvironmentEventRunFunction {
    void run(EnvironmentContext context);
}
