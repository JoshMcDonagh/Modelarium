package modelarium.entities.attributes.routines;

import modelarium.entities.attributes.AgentAttribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.AgentContext;

/**
 * Abstract class for representing a routine belonging to an agent.
 *
 * <p>Subclasses define the behaviour of the routine using an {@link AgentContext}.
 */
public non-sealed abstract class AgentRoutine extends Routine<AgentContext> implements AgentAttribute {

    /**
     * Constructs a new agent routine with the specified name and access level.
     *
     * @param name the name of the routine, used to identify it within its attribute set
     * @param accessLevel the access level of the routine, determining whether other entities may read it
     */
    public AgentRoutine(String name, AttributeAccessLevel accessLevel) {
        super(name, accessLevel);
    }
}
