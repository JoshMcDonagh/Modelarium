package modelarium.entities.attributes.properties;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.Entity;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

public abstract class EnvironmentProperty<T> extends Property<T,EnvironmentContext> {
    public EnvironmentProperty(String name, boolean isLogged, AttributeAccessLevel accessLevel, Class<T> type) {
        super(name, isLogged, accessLevel, type);
    }

    @Override
    public void createContext(
            Entity<?,?,?> entity,
            AttributeSet<?> attributeSet,
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            Clock clock,
            RequestResponseInterface requestResponseInterface,
            Environment localEnvironment
    ) {
        if (context() != null)
            return;

        EnvironmentContext context = new EnvironmentContext(
                (Environment) entity,
                (EnvironmentAttributeSet) attributeSet,
                this,
                agentSet,
                config,
                contextCache,
                clock,
                requestResponseInterface
        );

        setContext(context);
    }
}
