package modelarium.entities.attributes.events.functional;

import modelarium.entities.contexts.AgentContext;

/**
 * Functional interface for defining the behaviour of a functional agent event.
 *
 */
@FunctionalInterface
public interface AgentEventRunFunction {

    /**
     * Runs the event's behaviour.
     *
     * @param context the context the event can use in its behaviour
     */
    void run(AgentContext context);
}
