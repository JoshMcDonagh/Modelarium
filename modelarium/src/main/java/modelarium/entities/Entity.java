package modelarium.entities;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.entities.readonly.ReadOnlyEntity;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.attributes.sets.AttributeSet;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.internal.Internal;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

/**
 * Abstract class for representing an element of the model that owns attribute sets and can be run each tick.
 *
 * <p>This class is responsible for storing an entity's attribute sets, creating and holding the simulation context
 * the entity's attributes use, and providing access to the entity's attributes and logs. It is extended by
 * {@link Agent} and {@link Environment}.
 *
 * @param <SC> the type of simulation context the entity uses
 * @param <C> the type of context interface the entity's attributes are given
 * @param <AS> the type of attribute set the entity owns
 * @param <ASL> the type of attribute set log the entity produces
 */
public sealed abstract class Entity<SC extends SimulationContext, C extends Context, AS extends AttributeSet<SC,C>, ASL extends AttributeSetLog<SC>> permits Agent, Environment {

    /** The name of this entity, used to identify it within the model */
    private final String name;

    /** The attribute sets this entity owns */
    private final List<AS> attributeSetList;

    /** Maps each attribute set's name to its index in the attribute set list */
    private final Map<String, Integer> attributeSetIndexMap = new HashMap<>();

    /** The simulation context this entity and its attributes use, created once by the model */
    private SC context = null;

    /**
     * Constructs a new entity with the specified name and attribute sets.
     *
     * @param name the name of the entity, used to identify it within the model
     * @param attributeSetList the attribute sets the entity will own
     */
    protected Entity(String name, List<AS> attributeSetList) {
        this.name = name;
        this.attributeSetList = attributeSetList;
        for (int i = 0; i < this.attributeSetList.size(); i++) {
            AS attributeSet = this.attributeSetList.get(i);
            this.attributeSetIndexMap.put(attributeSet.name(), i);
            attributeSet.setOwnerName(name);
        }
    }

    /**
     * Provides each of this entity's attribute sets with the factory used to create its log database.
     *
     * @param databaseFactory the factory the attribute sets will use to create their log databases
     */
    @Internal
    public void setLogDatabaseFactory(AttributeSetLogDatabaseFactory databaseFactory) {
        for (AS attributeSet : attributeSetList)
            attributeSet.setLogDatabaseFactory(databaseFactory);
    }

    /**
     * Creates the simulation context instance for this entity's specific type. Must be implemented by subclasses.
     *
     * @param agentSet the local agent set the context will provide access to
     * @param config the shared model config
     * @param contextCache the cache the context can use for agents and the environment
     * @param clock the clock the context will provide access to
     * @param requestResponseController the request/response controller the context will need for inter-entity
     *                                  interaction
     * @param localEnvironment the local environment the context will provide access to
     * @param randomGenerator the random generator the context will provide access to
     * @return a new simulation context instance for this entity
     */
    protected abstract SC makeContextInstance(
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            Clock clock,
            RequestResponseController requestResponseController,
            Environment localEnvironment,
            RandomGenerator randomGenerator
    );

    /**
     * Creates the simulation context this entity and its attributes will use, and provides it to each of the
     * entity's attribute sets.
     *
     * @param agentSet the local agent set the context will provide access to
     * @param config the shared model config
     * @param contextCache the cache the context can use for agents and the environment
     * @param clock the clock the context will provide access to
     * @param requestResponseController the request/response controller the context will need for inter-entity
     *                                  interaction
     * @param localEnvironment the local environment the context will provide access to
     * @param randomGenerator the random generator the context will provide access to
     */
    @Internal
    public void createContext(
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            Clock clock,
            RequestResponseController requestResponseController,
            Environment localEnvironment,
            RandomGenerator randomGenerator
    ) {
        if (context != null)
            throw new IllegalStateException("Context already created");

        setLogDatabaseFactory(config.runLogDatabaseFactory());

        context = makeContextInstance(
                agentSet,
                config,
                contextCache,
                clock,
                requestResponseController,
                localEnvironment,
                randomGenerator
        );

        for (AS attributeSet : attributeSetList)
            attributeSet.setContext(context);
    }

    @Internal
    public AgentSet getAddedAgents() {
        return context.getAddedAgents();
    }

    @Internal
    public List<String> getKilledAgentNames() {
        return context.getKilledAgentNames();
    }

    /**
     * Returns the simulation context this entity uses.
     *
     * @return the entity's simulation context, or null if it has not yet been created
     */
    public SC context() {
        return context;
    }

    /**
     * Returns the name of this entity.
     *
     * @return the entity's name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the number of attribute sets this entity owns.
     *
     * @return the entity's attribute set count
     */
    public int attributeSetCount() {
        return attributeSetList.size();
    }

    /**
     * Returns the total number of attributes across all of this entity's attribute sets.
     *
     * @return the entity's total attribute count
     */
    public int attributeCount() {
        int count = 0;

        for (AS attributeSet : attributeSetList)
            count += attributeSet.size();

        return count;
    }

    /**
     * Retrieves an attribute set by index.
     *
     * @param attributeSetIndex the index of the attribute set to retrieve
     * @return the attribute set at the specified index
     */
    public AS getAttributeSet(int attributeSetIndex) {
        return attributeSetList.get(attributeSetIndex);
    }

    /**
     * Retrieves an attribute set by name.
     *
     * @param attributeSetName the name of the attribute set to retrieve
     * @return the attribute set with the specified name
     */
    public AS getAttributeSet(String attributeSetName) {
        return getAttributeSet(attributeSetIndexMap.get(attributeSetName));
    }

    /**
     * Returns the log of this entity's attribute values.
     *
     * @return a new {@link EntityLog} instance containing the logs of the entity's attribute sets
     */
    public EntityLog<SC,C,AS,ASL> getLog() {
        return new EntityLog<>(name, attributeSetList);
    }

    /**
     * Runs each of this entity's attribute sets for the current tick.
     */
    public void run() {
        for (AS attributeSet : attributeSetList)
            attributeSet.run();
    }

    /**
     * Creates and returns an immutable version of this entity.
     *
     * @return the new {@link ReadOnlyEntity} instance
     */
    public abstract ReadOnlyEntity<SC,C,AS,ASL> getAsImmutable();

    @Internal
    public void clearPendingAgentChanges() {
        context.clearPendingAgentChanges();
    }
}
