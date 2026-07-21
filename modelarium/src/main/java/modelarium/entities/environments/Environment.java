package modelarium.entities.environments;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.Entity;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Class for representing the environment of the model.
 *
 * <p>This class is an entity that owns {@link EnvironmentAttributeSet} instances and uses an
 * {@link EnvironmentSimulationContext} for its behaviour and interactions.
 */
public final class Environment extends Entity<EnvironmentSimulationContext, EnvironmentContext, EnvironmentAttributeSet, AttributeSetLog<EnvironmentSimulationContext>> {

    /**
     * Constructs a new environment with the specified name and attribute sets.
     *
     * @param name the name of the environment, used to identify it within the model
     * @param attributeSets the attribute sets the environment will own
     */
    public Environment(String name, List<EnvironmentAttributeSet> attributeSets) {
        super(name, attributeSets);
    }

    /**
     * Constructs a new environment with an attribute sets.
     *
     * @param attributeSets the attribute sets the environment will own
     */
    public Environment(List<EnvironmentAttributeSet> attributeSets) {
        super("environment", attributeSets);
    }

    /**
     * Creates the {@link EnvironmentSimulationContext} instance this environment will use.
     *
     * @param agentSet the local agent set the context will provide access to
     * @param config the shared model config
     * @param contextCache the cache the context can use for agents and the environment
     * @param clock the clock the context will provide access to
     * @param requestResponseController the request/response controller the context will need for inter-entity
     *                                  interaction
     * @param localEnvironment the local environment the context will provide access to
     * @param randomGenerator the random generator the context will provide access to
     * @return a new {@link EnvironmentSimulationContext} instance for this environment
     */
    @Override
    protected EnvironmentSimulationContext makeContextInstance(
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            MutableClock clock,
            RequestResponseController requestResponseController,
            Environment localEnvironment,
            RandomGenerator randomGenerator
    ) {
        return new EnvironmentSimulationContext(
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
     * Retrieves an event attribute from one of this environment's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the event
     * @param eventName the name of the event to retrieve
     * @return the {@link EnvironmentEvent} with the specified name
     */
    public EnvironmentEvent getEvent(String attributeSetName, String eventName) {
        return getAttributeSet(attributeSetName).getEvent(eventName);
    }

    /**
     * Retrieves a routine attribute from one of this environment's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the routine
     * @param routineName the name of the routine to retrieve
     * @return the {@link EnvironmentRoutine} with the specified name
     */
    public EnvironmentRoutine getRoutine(String attributeSetName, String routineName) {
        return getAttributeSet(attributeSetName).getRoutine(routineName);
    }

    /**
     * Retrieves a property attribute from one of this environment's attribute sets.
     *
     * @param attributeSetName the name of the attribute set containing the property
     * @param propertyName the name of the property to retrieve
     * @return the {@link EnvironmentProperty} with the specified name
     */
    public EnvironmentProperty<?> getProperty(String attributeSetName, String propertyName) {
        return getAttributeSet(attributeSetName).getProperty(propertyName);
    }
}
