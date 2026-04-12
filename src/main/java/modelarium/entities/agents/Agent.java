package modelarium.entities.agents;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.Entity;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

import java.util.List;

public class Agent extends Entity<AgentContext, AgentAttributeSet, AttributeSetLog<AgentContext>> {
    public Agent(String name, List<AgentAttributeSet> attributeSets) {
        super(name, attributeSets);
    }

    @Override
    protected AgentContext makeContextInstance(
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            Clock clock,
            RequestResponseInterface requestResponseInterface,
            Environment localEnvironment
    ) {
        return new AgentContext(
                this,
                agentSet,
                config,
                contextCache,
                clock,
                requestResponseInterface,
                localEnvironment
        );
    }

    @Override
    public Agent clone() {
        return getCloner().deepClone(this);
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
