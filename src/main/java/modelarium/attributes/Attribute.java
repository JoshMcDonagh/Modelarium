package modelarium.attributes;

import modelarium.Entity;

public abstract class Attribute {
    private final String name;
    private final boolean isLogged;
    private final AttributeAccessLevel accessLevel;

    private Entity owner;

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

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    protected Entity owner() {
        return owner;
    }

    public AttributeAccessLevel accessLevel() {
        return accessLevel;
    }

    public abstract void run();
}
