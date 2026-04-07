package modelarium.entities.attributes.functional.routines;

import modelarium.entities.contexts.Context;

@FunctionalInterface
public interface RoutineRunFunction {
    void run(Context context);
}
