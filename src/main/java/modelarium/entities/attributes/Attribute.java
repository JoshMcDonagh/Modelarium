package modelarium.entities.attributes;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.Entity;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

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

    public abstract void createContext(
            Entity<?,?,?> entity,
            AttributeSet<?> attributeSet,
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            Clock clock,
            RequestResponseInterface requestResponseInterface,
            Environment localEnvironment
    );

    protected void setContext(C context) {
        this.context = context;
    }

    public AttributeAccessLevel accessLevel() {
        return accessLevel;
    }

    protected abstract void run();
}
