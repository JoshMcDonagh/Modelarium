package modelarium.entities.attributes.events;

import modelarium.entities.attributes.sets.mutable.AttributeBase;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.Context;

/**
 * Abstract class for representing an attribute whose behaviour only runs when a trigger condition is met.
 *
 * <p>Each tick, an event's trigger condition is checked, its behaviour is run if the condition holds, and (if the
 * event is logged) the trigger state is recorded. This class is extended by {@link AgentEvent} and
 * {@link EnvironmentEvent}.
 *
 * @param <C> the type of context this event is given
 */
public sealed abstract class Event<C extends Context> extends AttributeBase<C> permits AgentEvent, EnvironmentEvent {

    /**
     * Constructs a new event with the specified name, logging flag and access level.
     *
     * @param name the name of the event, used to identify it within its attribute set
     * @param isLogged whether the event's trigger state is logged as the model progresses
     * @param accessLevel the access level of the event, determining whether other entities may read it
     */
    Event(String name, boolean isLogged, AttributeAccessLevel accessLevel) {
        super(name, isLogged, accessLevel);
    }

    /**
     * Returns whether this event's trigger condition is currently met.
     *
     * @return true if the event is triggered, false otherwise
     */
    public boolean isTriggered() {
        return isTriggered(context());
    }

    /**
     * Runs this event's behaviour for the current tick.
     */
    @Override
    public void run() {
        run(context());
    }

    /**
     * Determines whether this event's trigger condition is met. Must be implemented by subclasses.
     *
     * @param context the context the event can use in its trigger logic
     * @return true if the event is triggered, false otherwise
     */
    protected abstract boolean isTriggered(C context);

    /**
     * Runs this event's behaviour. Must be implemented by subclasses.
     *
     * @param context the context the event can use in its behaviour
     */
    protected abstract void run(C context);
}
