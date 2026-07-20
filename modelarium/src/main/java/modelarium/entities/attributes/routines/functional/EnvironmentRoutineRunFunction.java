package modelarium.entities.attributes.routines.functional;

import modelarium.entities.contexts.EnvironmentContext;

/**
 * Functional interface for defining the behaviour of a functional environment routine.
 *
 */
@FunctionalInterface
public interface EnvironmentRoutineRunFunction {

    /**
     * Runs the routine's behaviour.
     *
     * @param context the context the routine can use in its behaviour
     */
    void run(EnvironmentContext context);
}
