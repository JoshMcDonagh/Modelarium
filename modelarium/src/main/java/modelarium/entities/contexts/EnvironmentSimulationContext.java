package modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.attributes.sets.mutable.AttributeBase;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.entities.environments.ImmutableEnvironment;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.random.RandomGenerator;

/**
 * Class for providing the environment and its attributes with access to relevant simulation resources.
 *
 * <p>This class is the concrete {@link EnvironmentContext} implementation the model creates for the environment,
 * combining the shared behaviour of {@link SimulationContext} with environment-specific entity access.
 */
public final class EnvironmentSimulationContext extends SimulationContext implements EnvironmentContext {

    /**
     * Constructs a new simulation context for the environment.
     *
     * @param entity the environment this context belongs to
     * @param localAgentSet the local agent set the context will provide access to
     * @param config the shared model config
     * @param cache the cache the context can use for agents and the environment
     * @param clock the clock the context will provide access to
     * @param requestResponseController the request/response controller the context will need for inter-entity
     *                                  interaction
     * @param localEnvironment the local environment the context will provide access to
     * @param randomGenerator the random generator the context will provide access to
     */
    public EnvironmentSimulationContext(
            MutableEnvironment entity,
            MutableAgentSet localAgentSet,
            Config config,
            ContextCache cache,
            MutableClock clock,
            RequestResponseController requestResponseController,
            MutableEnvironment localEnvironment,
            RandomGenerator randomGenerator
    ) {
        super(entity, localAgentSet, config, cache, clock, requestResponseController, localEnvironment, randomGenerator);
    }

    /**
     * Returns the environment this context belongs to.
     *
     * @return the owning {@link MutableEnvironment} instance
     */
    @Override
    public MutableEnvironment getThisEntity() {
        return (MutableEnvironment) entity();
    }

    /**
     * Returns the attribute set currently being run on the owning environment.
     *
     * @return the current {@link MutableEnvironmentAttributeSet} instance
     */
    @Override
    public MutableEnvironmentAttributeSet getThisAttributeSet() {
        return (MutableEnvironmentAttributeSet) attributeSet();
    }

    /**
     * Returns the attribute currently being run on the owning environment.
     *
     * @return the current attribute instance
     */
    @Override
    public AttributeBase<EnvironmentSimulationContext> getThisAttribute() {
        return (AttributeBase<EnvironmentSimulationContext>) attribute();
    }

    /**
     * Unsupported for the environment's own context.
     *
     * <p>The context requester is already the environment, so {@link #getThisEntity()} should be used instead.
     *
     * @return this method never returns normally
     */
    @Override
    public ImmutableEnvironment getEnvironment() {
        throw new UnsupportedOperationException("Context requester is already an Environment - use 'getThisEntity()' instead");
    }
}
