package modelarium.entities.agents;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.Entity;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.List;

public final class Agent extends Entity<AgentSimulationContext, AgentContext, AgentAttributeSet, AttributeSetLog<AgentSimulationContext>> {
    public Agent(String name, List<AgentAttributeSet> attributeSets) {
        super(name, attributeSets);
    }

    @Override
    protected AgentSimulationContext makeContextInstance(
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            MutableClock clock,
            RequestResponseController requestResponseController,
            Environment localEnvironment
    ) {
        return new AgentSimulationContext(
                this,
                agentSet,
                config,
                contextCache,
                clock,
                requestResponseController,
                localEnvironment
        );
    }

    public AgentEvent getEvent(String attributeSetName, String eventName) {
        return getAttributeSet(attributeSetName).getEvent(eventName);
    }

    public AgentRoutine getRoutine(String attributeSetName, String routineName) {
        return getAttributeSet(attributeSetName).getRoutine(routineName);
    }

    public AgentProperty<?> getProperty(String attributeSetName, String propertyName) {
        return getAttributeSet(attributeSetName).getProperty(propertyName);
    }
}
