package modelarium.entities.attributes;

import modelarium.entities.contexts.Context;

public abstract class Attribute<C extends Context> {
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
