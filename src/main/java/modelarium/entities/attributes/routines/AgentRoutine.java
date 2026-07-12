package modelarium.entities.attributes.routines;

import modelarium.entities.attributes.AgentAttribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.AgentContext;

public non-sealed abstract class AgentRoutine extends Routine<AgentContext> implements AgentAttribute {
    public AgentRoutine(String name, AttributeAccessLevel accessLevel) {
        super(name, accessLevel);
    }
}
