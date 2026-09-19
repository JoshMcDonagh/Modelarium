package modelarium.entities.attributes.events.functional;

import modelarium.entities.contexts.EnvironmentContext;

/**
 * Functional interface for defining the trigger condition of a functional environment event.
 *
 */
@FunctionalInterface
public interface EnvironmentEventIsTriggeredFunction {

    /**
     * Determines whether the event's trigger condition is met.
     *
     * @param context the context the event can use in its trigger logic
     * @return true if the event is triggered, false otherwise
     */
    boolean isTriggered(EnvironmentContext context);
}
