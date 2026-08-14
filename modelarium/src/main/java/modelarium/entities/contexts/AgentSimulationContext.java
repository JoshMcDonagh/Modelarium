package modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.AttributeBase;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.entities.environments.ImmutableEnvironment;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;
import modelarium.exceptions.EnvironmentNotFoundException;
import modelarium.exceptions.SimulationInterruptedException;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.random.RandomGenerator;

/**
 * Class for providing an agent and its attributes with access to relevant simulation resources.
 *
 * <p>This class is the concrete {@link AgentContext} implementation the model creates for each agent, combining the
 * shared behaviour of {@link SimulationContext} with agent-specific entity and environment access.
 */
public final class AgentSimulationContext extends SimulationContext implements AgentContext {

    /** The environment local to the owning agent's core */
    private final MutableEnvironment localEnvironment;

    /**
     * Constructs a new simulation context for the given agent.
     *
     * @param entity the agent the context belongs to
     * @param localAgentSet the local agent set the context will provide access to
     * @param config the shared model config
     * @param cache the cache the context can use for agents and the environment
     * @param clock the clock the context will provide access to
     * @param requestResponseController the request/response controller the context will need for inter-entity
     *                                  interaction
     * @param localEnvironment the local environment the context will provide access to
     * @param randomGenerator the random generator the context will provide access to
     */
    public AgentSimulationContext(
            MutableAgent entity,
            MutableAgentSet localAgentSet,
            Config config,
            ContextCache cache,
            MutableClock clock,
            RequestResponseController requestResponseController,
            MutableEnvironment localEnvironment,
            RandomGenerator randomGenerator
    ) {
        super(entity, localAgentSet, config, cache, clock, requestResponseController, localEnvironment, randomGenerator);
        this.localEnvironment = localEnvironment;
    }

    /**
     * Returns the agent this context belongs to.
     *
     * @return the owning {@link MutableAgent} instance
     */
    @Override
    public MutableAgent getThisEntity() {
        return (MutableAgent) entity();
    }

    /**
     * Returns the attribute set currently being run on the owning agent.
     *
     * @return the current {@link MutableAgentAttributeSet} instance
     */
    @Override
    public MutableAgentAttributeSet getThisAttributeSet() {
        return (MutableAgentAttributeSet) attributeSet();
    }

    /**
     * Returns the attribute currently being run on the owning agent.
     *
     * @return the current attribute instance
     */
    @Override
    public AttributeBase<AgentSimulationContext> getThisAttribute() {
        // noinspection unchecked
        return (AttributeBase<AgentSimulationContext>) attribute();
    }

    /**
     * Returns the model's environment.
     *
     * <p>If the model's threads are not synchronised, the core's local environment is returned. Otherwise the
     * environment is taken from the cache if present, or requested from the co-ordinator and cached for the
     * remainder of the tick.
     *
     * @return a read-only view of the model's {@link MutableEnvironment}
     */
    @Override
    public ImmutableEnvironment getEnvironment() {
        if (!config().areThreadsSynced())
            return new ImmutableEnvironment(localEnvironment);

        // Return cached environment if available
        if (cache().doesEnvironmentExist())
            return cache().getEnvironment();

        // Request environment from coordinator
        ImmutableEnvironment requestedEnvironment;
        try {
            requestedEnvironment = requestResponseInterface().getEnvironmentFromCoordinator(entity().name());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimulationInterruptedException("Interrupted while fetching environment requested by '"
                    + entity().name() + "'", e);
        } catch (CoordinatorTimeoutException | CoordinatorErrorException e) {
            throw new EnvironmentNotFoundException("Environment requested by '" + entity().name()
                    + "' could not be found", e);
        }

        // Cache the result
        cache().addEnvironment(requestedEnvironment);

        return requestedEnvironment;
    }
}
