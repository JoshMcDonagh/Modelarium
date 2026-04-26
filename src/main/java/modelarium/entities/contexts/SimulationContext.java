package modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.clock.SimulationClock;
import modelarium.entities.Entity;
import modelarium.entities.agents.Agent;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.exceptions.AgentNotFoundException;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;
import modelarium.exceptions.SimulationInterruptedException;
import modelarium.internal.Internal;
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
public sealed abstract class SimulationContext implements Context permits AgentSimulationContext, EnvironmentSimulationContext {
    private final Entity<?,?,?,?> entity;
    private final AgentSet localAgentSet;
    private final Config config;
    private final ContextCache cache;
    private final SimulationClock clock;
    private final RequestResponseInterface requestResponseInterface;

    private AttributeSet<?,?> attributeSet = null;
    private Attribute<?> attribute = null;

    @Internal
    public SimulationContext(
            Entity<?,?,?,?> entity,
            AgentSet localAgentSet,
            Config config,
            ContextCache cache,
            SimulationClock clock,
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

    @Internal
    public void setCurrentAttributeSet(AttributeSet<?,?> attributeSet) {
        this.attributeSet = attributeSet;
    }

    @Internal
    public void setCurrentAttribute(Attribute<?> attribute) {
        this.attribute = attribute;
    }

    protected Entity<?,?,?,?> entity() {
        return entity;
    }

    protected AttributeSet<?,?> attributeSet() {
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

    public abstract Entity<?,?,?,?> getThisEntity();

    public abstract AttributeSet<?,?> getThisAttributeSet();

    public abstract Attribute<?> getThisAttribute();

    public abstract ImmutableEnvironment getEnvironment();

    public ImmutableAgent getAgent(String targetAgentName) {
        // Check local agent set
        if (doesAgentExistInThisCore(targetAgentName))
            return new ImmutableAgent(localAgentSet.get(targetAgentName));

        // Check cache if enabled
        if (cache.doesAgentExist(targetAgentName))
            return new ImmutableAgent(cache.getAgent(targetAgentName));

        // If not synchronised, cannot retrieve further
        if (!config.areThreadsSynced())
            throw new AgentNotFoundException("Agent '" + targetAgentName + "' requested by '" + entity.name()
                    + "' not found in this thread (threads are not synced)");

        // Request from coordinator
        try {
            Agent requestedAgent = requestResponseInterface.getAgentFromCoordinator(entity.name(), targetAgentName);
            cache.addAgent(requestedAgent);
            return new ImmutableAgent(requestedAgent);
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

        AgentSet filteredAgentSet;

        if (config.areThreadsSynced()) {
            // Request filtered agents from the coordinator
            try {
                filteredAgentSet = requestResponseInterface.getFilteredAgentsFromCoordinator(entity.name(), filter);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SimulationInterruptedException("Interrupted while retrieving filtered agents requested by " +
                        "'" + entity.name() + "'", e);
            } catch (CoordinatorTimeoutException | CoordinatorErrorException e) {
                throw new AgentNotFoundException("Failed to retrieve filtered agents requested by '" + entity.name()
                        + "' from the coordinator", e);
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
