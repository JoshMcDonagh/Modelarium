package modelarium.entities.attributes;

import modelarium.entities.attributes.events.Event;
import modelarium.entities.attributes.properties.Property;
import modelarium.entities.attributes.routines.Routine;
import modelarium.entities.contexts.EntityContext;

public sealed abstract class Attribute<C extends EntityContext> permits Property, Event, Routine {
    private final String name;
    private final boolean isLogged;
    private final AttributeAccessLevel accessLevel;

    private C context = null;

    public Attribute(String name, boolean isLogged, AttributeAccessLevel accessLevel) {
        this.name = name;
        this.isLogged = isLogged;
        this.accessLevel = accessLevel;
    }

    public String name() {
        return name;
    }

    public boolean isLogged() {
        return isLogged;
    }

    protected C context() {
        return context;
    }

    void setContext(C context) {
        if (this.context != null)
            return;

        this.context = context;
    }

    public AttributeAccessLevel accessLevel() {
        return accessLevel;
    }

    protected abstract void run();
}
