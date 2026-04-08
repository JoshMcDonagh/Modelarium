package modelarium.entities.attributes.routines;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.AgentContext;

public abstract class AgentRoutine extends Routine<AgentContext> {
    public AgentRoutine(String name, AttributeAccessLevel accessLevel) {
        super(name, accessLevel);
    }
}
