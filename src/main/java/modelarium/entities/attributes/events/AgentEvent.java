package modelarium.entities.attributes.events;

import modelarium.entities.attributes.AgentAttribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.AgentContext;

/**
 * Abstract class for representing an event belonging to an agent.
 *
 * <p>Subclasses define the trigger condition and behaviour of the event using an {@link AgentContext}.
 */
public non-sealed abstract class AgentEvent extends Event<AgentContext> implements AgentAttribute {

    /**
     * Constructs a new agent event with the specified name, logging flag and access level.
     *
     * @param name the name of the event, used to identify it within its attribute set
     * @param isLogged whether the event's trigger state is logged as the model progresses
     * @param accessLevel the access level of the event, determining whether other entities may read it
     */
    public AgentEvent(String name, boolean isLogged, AttributeAccessLevel accessLevel) {
        super(name, isLogged, accessLevel);
    }
}
