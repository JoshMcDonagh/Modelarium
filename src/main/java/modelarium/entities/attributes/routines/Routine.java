package modelarium.entities.attributes.routines;

import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.Context;

public abstract class Routine<C extends Context> extends Attribute<C> {
    Routine(String name, AttributeAccessLevel accessLevel) {
        super(name, false, accessLevel);
    }

    @Override
    public void run() {
        run(context());
    }

    protected abstract void run(C context);
}
