package modelarium.entities.attributes.routines.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.exceptions.MissingAttributeFunctionException;

public class FunctionalEnvironmentRoutine extends EnvironmentRoutine {
    private final EnvironmentRoutineRunFunction runLogic;

    public FunctionalEnvironmentRoutine(
            String name,
            AttributeAccessLevel accessLevel,
            EnvironmentRoutineRunFunction runLogic
    ) {
        super(name, accessLevel);
        this.runLogic = runLogic;
    }

    @Override
    protected void run(EnvironmentContext context) {
        if (runLogic == null)
            throw new MissingAttributeFunctionException("No run logic function provided for '" + name() +"'");

        runLogic.run(context);
    }
}
