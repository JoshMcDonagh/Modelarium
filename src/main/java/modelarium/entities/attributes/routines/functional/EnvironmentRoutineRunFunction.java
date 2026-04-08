package modelarium.entities.attributes.routines.functional;

import modelarium.entities.contexts.EnvironmentContext;

@FunctionalInterface
public interface EnvironmentRoutineRunFunction {
    void run(EnvironmentContext context);
}
