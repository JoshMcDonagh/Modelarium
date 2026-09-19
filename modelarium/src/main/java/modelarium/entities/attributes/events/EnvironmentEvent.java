package modelarium.entities.attributes.events;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.EnvironmentAttribute;
import modelarium.entities.contexts.EnvironmentContext;

/**
 * Abstract class for representing an event belonging to the environment.
 *
 * <p>Subclasses define the trigger condition and behaviour of the event using an {@link EnvironmentContext}.
 */
public non-sealed abstract class EnvironmentEvent extends Event<EnvironmentContext> implements EnvironmentAttribute {

    /**
     * Constructs a new environment event with the specified name, logging flag and access level.
     *
     * @param name the name of the event, used to identify it within its attribute set
     * @param isLogged whether the event's trigger state is logged as the model progresses
     * @param accessLevel the access level of the event, determining whether other entities may read it
     */
    public EnvironmentEvent(String name, boolean isLogged, AttributeAccessLevel accessLevel) {
        super(name, isLogged, accessLevel);
    }
}
