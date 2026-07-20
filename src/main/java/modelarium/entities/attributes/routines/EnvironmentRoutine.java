package modelarium.entities.attributes.routines;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.EnvironmentAttribute;
import modelarium.entities.contexts.EnvironmentContext;

/**
 * Abstract class for representing a routine belonging to the environment.
 *
 * <p>Subclasses define the behaviour of the routine using an {@link EnvironmentContext}.
 */
public non-sealed abstract class EnvironmentRoutine extends Routine<EnvironmentContext> implements EnvironmentAttribute {

    /**
     * Constructs a new environment routine with the specified name and access level.
     *
     * @param name the name of the routine, used to identify it within its attribute set
     * @param accessLevel the access level of the routine, determining whether other entities may read it
     */
    public EnvironmentRoutine(String name, AttributeAccessLevel accessLevel) {
        super(name, accessLevel);
    }
}
