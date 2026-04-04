package modelarium.entities.attributes;

import modelarium.entities.contexts.Context;

public abstract class Attribute {
    private final String name;
    private final boolean isLogged;
    private final AttributeAccessLevel accessLevel;

    private Context context = null;

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

    protected Context context() {
        return context;
    }

    public void setContext(Context context) {
        if (this.context != null)
            return;

        this.context = context;
    }

    public AttributeAccessLevel accessLevel() {
        return accessLevel;
    }

    public abstract void run();
}
