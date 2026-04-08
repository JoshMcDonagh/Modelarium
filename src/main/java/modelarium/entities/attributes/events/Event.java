package modelarium.entities.attributes.events;

import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.Context;

abstract class Event<C extends Context> extends Attribute<C> {
    public Event(String name, boolean isLogged, AttributeAccessLevel accessLevel) {
        super(name, isLogged, accessLevel);
    }

    public boolean isTriggered() {
        return isTriggered(context());
    }

    @Override
    public void run() {
        run(context());
    }

    protected abstract boolean isTriggered(C context);

    protected abstract void run(C context);
}

