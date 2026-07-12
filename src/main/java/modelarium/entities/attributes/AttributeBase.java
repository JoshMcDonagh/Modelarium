package modelarium.entities.attributes;

import modelarium.entities.attributes.events.Event;
import modelarium.entities.attributes.properties.Property;
import modelarium.entities.attributes.routines.Routine;
import modelarium.entities.contexts.Context;

public sealed abstract class AttributeBase<C extends Context> implements Attribute permits Property, Event, Routine {
    private final String name;
    private final boolean isLogged;
    private final AttributeAccessLevel accessLevel;

    private C context = null;

    public AttributeBase(String name, boolean isLogged, AttributeAccessLevel accessLevel) {
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
            throw new IllegalStateException("Context already set");

        this.context = context;
    }

    public AttributeAccessLevel accessLevel() {
        return accessLevel;
    }
}
