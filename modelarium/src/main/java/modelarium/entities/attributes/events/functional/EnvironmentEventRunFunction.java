package modelarium.entities.attributes.events.functional;

import modelarium.entities.contexts.EnvironmentContext;

/**
 * Functional interface for defining the behaviour of a functional environment event.
 *
 */
@FunctionalInterface
public interface EnvironmentEventRunFunction {

    /**
     * Runs the event's behaviour.
     *
     * @param context the context the event can use in its behaviour
     */
    void run(EnvironmentContext context);
}
