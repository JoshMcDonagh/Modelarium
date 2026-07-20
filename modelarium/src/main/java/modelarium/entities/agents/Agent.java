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
import java.util.random.RandomGenerator;

/**
 * Class for representing an agent in the model.
 *
 * <p>This class is an entity that owns {@link AgentAttributeSet} instances and uses an
 * {@link AgentSimulationContext} for its behaviour and interactions.
 */
public final class Agent extends Entity<AgentSimulationContext, AgentContext, AgentAttributeSet, AttributeSetLog<AgentSimulationContext>> {

    /**
     * Constructs a new agent with the specified name and attribute sets.
     *
     * @param name the name of the agent, used to identify it within the model
     * @param attributeSets the attribute sets the agent will own
     */
    public Agent(String name, List<AgentAttributeSet> attributeSets) {
        super(name, attributeSets);
    }

    /**
     * Creates the {@link AgentSimulationContext} instance this agent will use.
     *
     * @param agentSet the local agent set the context will provide access to
     * @param config the shared model config
     * @param contextCache the cache the context can use for agents and the environment
     * @param clock the clock the context will provide access to
     * @param requestResponseController the request/response controller the context will need for inter-entity
     *                                  interaction
     * @param localEnvironment the local environment the context will provide access to
     * @param randomGenerator the random generator the context will provide access to
     * @return a new {@link AgentSimulationContext} instance for this agent
     */
    @Override
    protected AgentSimulationContext makeContextInstance(
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            MutableClock clock,
            RequestResponseController requestResponseController,
            Environment localEnvironment,
            RandomGenerator randomGenerator
    ) {
        return new AgentSimulationContext(
                this,
                agentSet,
                config,
                contextCache,
                clock,
                requestResponseController,
                localEnvironment,
                randomGenerator
        );
    }

    /**
     * Retrieves an event attribute from one of this agent's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the event
     * @param eventName the name of the event to retrieve
     * @return the {@link AgentEvent} with the specified name
     */
    public AgentEvent getEvent(String attributeSetName, String eventName) {
        return getAttributeSet(attributeSetName).getEvent(eventName);
    }

    /**
     * Retrieves a routine attribute from one of this agent's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the routine
     * @param routineName the name of the routine to retrieve
     * @return the {@link AgentRoutine} with the specified name
     */
    public AgentRoutine getRoutine(String attributeSetName, String routineName) {
        return getAttributeSet(attributeSetName).getRoutine(routineName);
    }

    /**
     * Retrieves a property attribute from one of this agent's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the property
     * @param propertyName the name of the property to retrieve
     * @return the {@link AgentProperty} with the specified name
     */
    public AgentProperty<?> getProperty(String attributeSetName, String propertyName) {
        return getAttributeSet(attributeSetName).getProperty(propertyName);
    }
}
