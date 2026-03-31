package modelarium.attributes.functional.routines;

import modelarium.Entity;

@FunctionalInterface
public interface RoutineRunFunction {
    void run(Entity owner);
}
