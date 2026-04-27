package modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.multithreading.requestresponse.RequestResponseController;

public final class EnvironmentSimulationContext extends SimulationContext implements EnvironmentContext {
    public EnvironmentSimulationContext(
            Environment entity,
            AgentSet localAgentSet,
            Config config,
            ContextCache cache,
            MutableClock clock,
            RequestResponseController requestResponseController,
            Environment localEnvironment
    ) {
        super(entity, localAgentSet, config, cache, clock, requestResponseController, localEnvironment);
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
    public Attribute<EnvironmentSimulationContext> getThisAttribute() {
        return (Attribute<EnvironmentSimulationContext>) attribute();
    }

    @Override
    public ImmutableEnvironment getEnvironment() {
        throw new UnsupportedOperationException("Context requester is already an Environment - use 'getThisEntity()' instead");
    }
}
