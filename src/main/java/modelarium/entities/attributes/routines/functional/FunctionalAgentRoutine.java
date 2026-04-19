package modelarium.entities.attributes.routines.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.exceptions.MissingAttributeFunctionException;

public class FunctionalAgentRoutine extends AgentRoutine {
    private final AgentRoutineRunFunction runLogic;

    public FunctionalAgentRoutine(String name, AttributeAccessLevel accessLevel, AgentRoutineRunFunction runLogic) {
        super(name, accessLevel);
        this.runLogic = runLogic;
    }

    @Override
    protected void run(AgentContext context) {
        if (runLogic == null)
            throw new MissingAttributeFunctionException("No run logic function provided for '" + name() +"'");

        runLogic.run(context);
    }
}
