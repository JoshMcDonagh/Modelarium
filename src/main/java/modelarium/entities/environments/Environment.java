package modelarium.entities.environments;

import modelarium.clock.SimulationClock;
import modelarium.Config;
import modelarium.entities.Entity;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

import java.util.List;

public final class Environment extends Entity<EnvironmentSimulationContext, EnvironmentAttributeSet, AttributeSetLog<EnvironmentSimulationContext>> {
    public Environment(String name, List<EnvironmentAttributeSet> attributeSets) {
        super(name, attributeSets);
    }

    @Override
    protected EnvironmentSimulationContext makeContextInstance(
            MutableAgentSet agentSet,
            Config config,
            ContextCache contextCache,
            SimulationClock clock,
            RequestResponseInterface requestResponseInterface,
            Environment localEnvironment
    ) {
        return new EnvironmentSimulationContext(
                this,
                agentSet,
                config,
                contextCache,
                clock,
                requestResponseInterface
        );
    }

    @Override
    public Environment clone() {
        return getCloner().deepClone(this);
    }

    public EnvironmentEvent getEvent(String attributeSetName, String eventName) {
        return getAttributeSet(attributeSetName).getEvent(eventName);
    }

    public EnvironmentRoutine getRoutine(String attributeSetName, String routineName) {
        return getAttributeSet(attributeSetName).getRoutine(routineName);
    }

    public EnvironmentProperty<?> getProperty(String attributeSetName, String propertyName) {
        return getAttributeSet(attributeSetName).getProperty(propertyName);
    }
}
