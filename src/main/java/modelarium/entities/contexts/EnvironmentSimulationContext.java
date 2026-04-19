package modelarium.entities.contexts;

import modelarium.clock.SimulationClock;
import modelarium.Config;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

public final class EnvironmentSimulationContext extends SimulationContext implements EnvironmentContext {
    public EnvironmentSimulationContext(
            Environment entity,
            MutableAgentSet localAgentSet,
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
    public Environment getEnvironment() {
        throw new UnsupportedOperationException("Context requester is already an Environment - use 'getThisEntity()' instead");
    }
}
