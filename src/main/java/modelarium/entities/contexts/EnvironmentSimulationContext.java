package modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.SimulationClock;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

public final class EnvironmentSimulationContext extends SimulationContext implements EnvironmentContext {
    public EnvironmentSimulationContext(
            Environment entity,
            AgentSet localAgentSet,
            Config config,
            ContextCache cache,
            SimulationClock clock,
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
    public Attribute<EnvironmentSimulationContext> getThisAttribute() {
        return (Attribute<EnvironmentSimulationContext>) attribute();
    }

    @Override
    public ImmutableEnvironment getEnvironment() {
        throw new UnsupportedOperationException("Context requester is already an Environment - use 'getThisEntity()' instead");
    }
}
