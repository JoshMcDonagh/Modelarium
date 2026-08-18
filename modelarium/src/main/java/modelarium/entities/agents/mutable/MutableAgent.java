package modelarium.entities.agents.mutable;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.ImmutableEntity;
import modelarium.entities.MutableEntity;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.immutable.ImmutableAgent;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Class for representing an agent in the model.
 *
 * <p>This class is an entity that owns {@link MutableAgentAttributeSet} instances and uses an
 * {@link AgentSimulationContext} for its behaviour and interactions.
 */
public final class MutableAgent extends MutableEntity<AgentSimulationContext, AgentContext, MutableAgentAttributeSet, AttributeSetLog<AgentSimulationContext>> implements Agent {
    private boolean isDead = false;
    private ImmutableAgent immutableVersion = null;

    /**
     * Constructs a new agent with the specified name and attribute sets.
     *
     * @param name the name of the agent, used to identify it within the model
     * @param attributeSets the attribute sets the agent will own
     */
    public MutableAgent(String name, List<MutableAgentAttributeSet> attributeSets) {
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
            MutableAgentSet agentSet,
            Config config,
            ContextCache contextCache,
            MutableClock clock,
            RequestResponseController requestResponseController,
            MutableEnvironment localEnvironment,
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
     * Retrieves an attribute set by index.
     *
     * @param attributeSetIndex the index of the attribute set to retrieve
     * @return the attribute set at the specified index
     */
    public MutableAgentAttributeSet getAttributeSet(int attributeSetIndex) {
        return super.getAttributeSet(attributeSetIndex);
    }

    /**
     * Retrieves an attribute set by name.
     *
     * @param attributeSetName the name of the attribute set to retrieve
     * @return the attribute set with the specified name
     */
    public MutableAgentAttributeSet getAttributeSet(String attributeSetName) {
        return super.getAttributeSet(attributeSetName);
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

    /**
     * Sets the agent to the removed state (killed) so that it no longer functions or contributes to the simulation.
     */
    public void kill() {
        isDead = true;
    }

    /**
     * Returns whether the agent is dead or not.
     *
     * @return the boolean value of whether the agent is dead (true) or not (false)
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Runs each of this agent's attribute sets for the current tick unless it is dead.
     */
    @Override
    public void run() {
        if (isDead)
            return;

        super.run();
    }

    /**
     * Creates and returns an immutable version of this agent.
     *
     * @return the new {@link ImmutableAgent} instance
     */
    @Override
    public ImmutableAgent getAsImmutable() {
        if (immutableVersion == null)
            immutableVersion = new ImmutableAgent(this);
        return immutableVersion;
    }
}
