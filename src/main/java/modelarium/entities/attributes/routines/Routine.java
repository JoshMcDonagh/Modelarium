package modelarium.entities.attributes.routines;

import modelarium.entities.attributes.AttributeBase;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.Context;

/**
 * Abstract class for representing an attribute whose behaviour runs unconditionally every tick.
 *
 * <p>Routines carry no value and are never logged; they exist purely to perform behaviour. This class is extended by
 * {@link AgentRoutine} and {@link EnvironmentRoutine}.
 *
 * @param <C> the type of context this routine is given
 */
public sealed abstract class Routine<C extends Context> extends AttributeBase<C> permits AgentRoutine, EnvironmentRoutine {

    /**
     * Constructs a new routine with the specified name and access level.
     *
     * @param name the name of the routine, used to identify it within its attribute set
     * @param accessLevel the access level of the routine, determining whether other entities may read it
     */
    Routine(String name, AttributeAccessLevel accessLevel) {
        super(name, false, accessLevel);
    }

    /**
     * Runs this routine's behaviour for the current tick.
     */
    @Override
    public void run() {
        run(context());
    }

    /**
     * Runs this routine's behaviour. Must be implemented by subclasses.
     *
     * @param context the context the routine can use in its behaviour
     */
    protected abstract void run(C context);
}
