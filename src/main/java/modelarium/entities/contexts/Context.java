package modelarium.entities.contexts;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.Entity;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.ImmutableAgentSet;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.exceptions.AgentNotFoundException;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;
import modelarium.exceptions.SimulationInterruptedException;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

import java.util.function.Predicate;

/**
 * Provides a model element (either an agent or the environment) with access
 * to relevant simulation resources such as the local environment, other agents,
 * and shared utilities, including communication and caching systems.
 *
 * <p>This class abstracts access logic based on model settings, including:
 * <ul>
 *     <li>Local access versus coordinated inter-thread access</li>
 *     <li>Optional caching of agents and environments</li>
 *     <li>Safe agent filtering with predicate functions</li>
 *     <li>The associated model clock</li>
 * </ul>
 */
public abstract class Context {
    private final Entity<?,?,?> entity;
    private final MutableAgentSet localAgentSet;
    private final Config config;
    private final ContextCache cache;
    private final Clock clock;
    private final RequestResponseInterface requestResponseInterface;

    private AttributeSet<?> attributeSet = null;
    private Attribute<?> attribute = null;

    public Context(
            Entity<?,?,?> entity,
            MutableAgentSet localAgentSet,
            Config config,
            ContextCache cache,
            Clock clock,
            RequestResponseInterface requestResponseInterface
    ) {
        this.entity = entity;
        this.localAgentSet = localAgentSet;
        this.config = config;
        this.cache = cache;
        this.clock = clock;
        this.requestResponseInterface = requestResponseInterface;
    }

    public Clock getClock() {
        return clock;
    }

    public boolean doesAgentExistInThisCore(String agentName) {
        return localAgentSet.doesAgentExist(agentName);
    }

    public void setCurrentAttributeSet(AttributeSet<?> attributeSet) {
        this.attributeSet = attributeSet;
    }

    public void setCurrentAttribute(Attribute<?> attribute) {
        this.attribute = attribute;
    }

    protected Entity<?,?,?> entity() {
        return entity;
    }

    protected AttributeSet<?> attributeSet() {
        return attributeSet;
    }

    protected Attribute<?> attribute() {
        return attribute;
    }

    protected Config config() {
        return config;
    }

    protected ContextCache cache() {
        return cache;
    }

    protected RequestResponseInterface requestResponseInterface() {
        return requestResponseInterface;
    }

    public abstract Entity<?,?,?> getThisEntity();

    public abstract AttributeSet<?> getThisAttributeSet();

    public abstract Attribute<?> getThisAttribute();

    public abstract Environment getEnvironment();

    public Agent getAgent(String targetAgentName) {
        // Check local agent set
        if (doesAgentExistInThisCore(targetAgentName))
            return localAgentSet.get(targetAgentName);

        // Check cache if enabled
        if (cache.doesAgentExist(targetAgentName))
            return cache.getAgent(targetAgentName);

        // If not synchronised, cannot retrieve further
        if (!config.areThreadsSynced())
            throw new AgentNotFoundException("Agent '" + targetAgentName + "' requested by '" + entity.name()
                    + "' not found in this thread (threads are not synced)");

        // Request from coordinator
        try {
            Agent requestedAgent = requestResponseInterface.getAgentFromCoordinator(entity.name(), targetAgentName);
            cache.addAgent(requestedAgent);
            return requestedAgent;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimulationInterruptedException("Interrupted while fetching agent '" + targetAgentName + "'", e);
        } catch (CoordinatorTimeoutException | CoordinatorErrorException e) {
            throw new AgentNotFoundException("Agent '" + targetAgentName + "' requested by '" + entity.name()
                    + "' not found", e);
        }
    }

    public ImmutableAgentSet getFilteredAgents(Predicate<Agent> filter) {
        // Return cached filtered result if available
        if (cache.doesAgentFilterExist(filter))
            return cache.getFilteredAgents(filter).getAsImmutable();

        MutableAgentSet filteredAgentSet;

        if (config.areThreadsSynced()) {
            // Request filtered agents from the coordinator
            try {
                filteredAgentSet = requestResponseInterface.getFilteredAgentsFromCoordinator(entity.name(), filter);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SimulationInterruptedException("Interrupted while retrieving filtered agents requested by " +
                        "'" + entity.name() + "'", e);
            } catch (CoordinatorTimeoutException | CoordinatorErrorException e) {
                throw new  AgentNotFoundException("Failed to retrieve filtered agents requested by '" + entity.name()
                        + "' from this thread (threads are not synced)", e);
            }
        } else {
            // Use only local agent set
            filteredAgentSet = localAgentSet.getFilteredAgents(filter);
        }

        // Cache the result for future access
        cache.addAgentFilter(filter);
        cache.addAgents(filteredAgentSet);

        return filteredAgentSet.getAsImmutable();
    }
}
