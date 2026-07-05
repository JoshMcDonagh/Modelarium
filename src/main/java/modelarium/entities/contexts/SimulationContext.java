package modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.clock.MutableClock;
import modelarium.entities.Entity;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.exceptions.AgentNotFoundException;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;
import modelarium.exceptions.SimulationInterruptedException;
import modelarium.internal.Internal;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.utils.Cloners;

import java.util.List;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

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
    private final MutableClock clock;
    private final RequestResponseController requestResponseController;
    private final RequestResponseInterface requestResponseInterface;
    private final Environment localEnvironment;
    private final RandomGenerator randomGenerator;

    private AttributeSet<?,?> attributeSet = null;
    private Attribute<?> attribute = null;

    @Internal
    public SimulationContext(
            Entity<?,?,?,?> entity,
            AgentSet localAgentSet,
            Config config,
            ContextCache cache,
            MutableClock clock,
            RequestResponseController requestResponseController,
            Environment localEnvironment,
            RandomGenerator randomGenerator
    ) {
        this.entity = entity;
        this.localAgentSet = localAgentSet;
        this.config = config;
        this.cache = cache;
        this.clock = clock;
        this.requestResponseController = requestResponseController;
        this.requestResponseInterface = requestResponseController.getInterface(entity().name());
        this.localEnvironment = localEnvironment;
        this.randomGenerator = randomGenerator;
    }

    public Clock getClock() {
        return clock;
    }

    public boolean doesAgentExistInThisCore(String agentName) {
        return localAgentSet.doesAgentExist(agentName);
    }

    public RandomGenerator getRandomGenerator() {
        return randomGenerator;
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

    private void createAgentContext(Agent agent) {
        agent.createContext(
                localAgentSet,
                config,
                cache,
                clock,
                requestResponseController,
                localEnvironment,
                randomGenerator
        );
    }

    private void createAgentContexts(AgentSet agentSet) {
        for (Agent agent : agentSet)
            createAgentContext(agent);
    }

    private void createAgentContexts(List<Agent> agentList) {
        for (Agent agent : agentList)
            createAgentContext(agent);
    }

    public void addAgent(Agent agent) {
        createAgentContext(agent);
        localAgentSet.add(agent);
    }

    public void addAgents(AgentSet agentSet) {
        createAgentContexts(agentSet);
        localAgentSet.add(agentSet);
    }

    public void addAgents(List<Agent> agentList) {
        createAgentContexts(agentList);
        localAgentSet.add(agentList);
    }

    public void addAgentDeepCopy(Agent agent) {
        addAgent(Cloners.standard().deepClone(agent));
    }

    public void addAgentsDeepCopy(AgentSet agentSet) {
        addAgents(Cloners.standard().deepClone(agentSet));
    }

    public void addAgentsDeepCopy(List<Agent> agentList) {
        addAgents(Cloners.standard().deepClone(agentList));
    }

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
        cache.addFilteredAgents(filter, filteredAgentSet);

        return filteredAgentSet.getAsImmutable();
    }
}
