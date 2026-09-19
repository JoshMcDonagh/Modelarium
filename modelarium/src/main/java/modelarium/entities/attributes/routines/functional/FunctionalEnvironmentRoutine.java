package modelarium.entities.attributes.routines.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.exceptions.MissingAttributeFunctionException;

/**
 * Class for representing an environment routine whose behaviour is defined via a functional interface.
 *
 * <p>This class is useful for defining simple routine logic without requiring full subclassing, particularly when
 * integrating with languages like Python.
 */
public class FunctionalEnvironmentRoutine extends EnvironmentRoutine {

    /** The function defining this routine's behaviour */
    private final EnvironmentRoutineRunFunction runLogic;

    /**
     * Constructs a new functional environment routine with the specified behaviour function.
     *
     * @param name the name of the routine, used to identify it within its attribute set
     * @param accessLevel the access level of the routine, determining whether other entities may read it
     * @param runLogic the function defining the routine's behaviour
     */
    public FunctionalEnvironmentRoutine(
            String name,
            AttributeAccessLevel accessLevel,
            EnvironmentRoutineRunFunction runLogic
    ) {
        super(name, accessLevel);
        this.runLogic = runLogic;
    }

    /**
     * Runs this routine's behaviour by applying the user-provided run function.
     *
     * @param context the context the routine can use in its behaviour
     */
    @Override
    protected void run(EnvironmentContext context) {
        if (runLogic == null)
            throw new MissingAttributeFunctionException("No run logic function provided for '" + name() +"'");

        runLogic.run(context);
    }
}
