package modelarium.entities.attributes.properties;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.Entity;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

public abstract class AgentProperty<T> extends Property<T,AgentContext> {
    public AgentProperty(String name, boolean isLogged, AttributeAccessLevel accessLevel, Class<T> type) {
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

        AgentContext context = new AgentContext(
                (Agent) entity,
                (AgentAttributeSet) attributeSet,
                this,
                agentSet,
                config,
                contextCache,
                clock,
                requestResponseInterface,
                localEnvironment
        );

        setContext(context);
    }
}
