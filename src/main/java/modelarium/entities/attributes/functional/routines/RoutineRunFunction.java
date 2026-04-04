package modelarium.entities.attributes.functional.routines;

import modelarium.entities.Entity;

@FunctionalInterface
public interface RoutineRunFunction {
    void run(Entity owner);
}
