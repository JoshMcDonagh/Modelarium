package modelarium.entities.attributes.routines;

import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.EntityContext;

public sealed abstract class Routine<C extends EntityContext> extends Attribute<C> permits AgentRoutine, EnvironmentRoutine {
    Routine(String name, AttributeAccessLevel accessLevel) {
        super(name, false, accessLevel);
    }

    @Override
    public void run() {
        run(context());
    }

    protected abstract void run(C context);
}
