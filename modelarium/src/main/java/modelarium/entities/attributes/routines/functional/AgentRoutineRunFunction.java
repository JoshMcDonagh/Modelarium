package modelarium.entities.attributes.routines.functional;

import modelarium.entities.contexts.AgentContext;

/**
 * Functional interface for defining the behaviour of a functional agent routine.
 *
 */
@FunctionalInterface
public interface AgentRoutineRunFunction {

    /**
     * Runs the routine's behaviour.
     *
     * @param context the context the routine can use in its behaviour
     */
    void run(AgentContext context);
}
