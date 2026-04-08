package modelarium.entities.attributes.routines.functional;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;

public class FunctionalAgentRoutine extends AgentRoutine {
    private final AgentRoutineRunFunction runLogic;

    public FunctionalAgentRoutine(String name, AttributeAccessLevel accessLevel, AgentRoutineRunFunction runLogic) {
        super(name, accessLevel);
        this.runLogic = runLogic;
    }

    @Override
    protected void run(AgentContext context) {
        runLogic.run(context);
    }
}
