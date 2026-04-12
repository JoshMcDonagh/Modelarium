package modelarium.entities.contexts;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

public class EnvironmentContext extends Context {
    public EnvironmentContext(
            Environment entity,
            EnvironmentAttributeSet attributeSet,
            Attribute<EnvironmentContext> attribute,
            AgentSet localAgentSet,
            Config config,
            ContextCache cache,
            Clock clock,
            RequestResponseInterface requestResponseInterface
    ) {
        super(entity, attributeSet, attribute, localAgentSet, config, cache, clock, requestResponseInterface);
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
