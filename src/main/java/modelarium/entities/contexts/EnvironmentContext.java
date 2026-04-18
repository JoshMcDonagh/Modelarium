package modelarium.entities.contexts;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

public class EnvironmentContext extends Context {
    public EnvironmentContext(
            Environment entity,
            MutableAgentSet localAgentSet,
            Config config,
            ContextCache cache,
            Clock clock,
            RequestResponseInterface requestResponseInterface
    ) {
        super(entity, localAgentSet, config, cache, clock, requestResponseInterface);
    }

    @Override
    public Environment getThisEntity() {
        return (Environment) entity();
    }

    @Override
    public EnvironmentAttributeSet getThisAttributeSet() {
        return (EnvironmentAttributeSet) attributeSet();
    }

    @Override
    public Attribute<EnvironmentContext> getThisAttribute() {
        return (Attribute<EnvironmentContext>) attribute();
    }

    @Override
    public Environment getEnvironment() {
        throw new UnsupportedOperationException("Context requester is already an Environment - use 'getThis()' instead");
    }
}
