package modelarium.entities.attributes.routines;

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

public abstract class EnvironmentRoutine extends Routine<EnvironmentContext> {
    public EnvironmentRoutine(String name, AttributeAccessLevel accessLevel) {
        super(name, accessLevel);
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
