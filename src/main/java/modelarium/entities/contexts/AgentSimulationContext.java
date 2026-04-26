package modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.SimulationClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.environments.Environment;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;
import modelarium.exceptions.EnvironmentNotFoundException;
import modelarium.exceptions.SimulationInterruptedException;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

public final class AgentSimulationContext extends SimulationContext implements AgentContext {
    private final Environment localEnvironment;

    public AgentSimulationContext(
            Agent entity,
            AgentSet localAgentSet,
            Config config,
            ContextCache cache,
            SimulationClock clock,
            RequestResponseInterface requestResponseInterface,
            Environment localEnvironment
    ) {
        super(entity, localAgentSet, config, cache, clock, requestResponseInterface);
        this.localEnvironment = localEnvironment;
    }

    @Override
    public Agent getThisEntity() {
        return (Agent) entity();
    }

    @Override
    public AgentAttributeSet getThisAttributeSet() {
        return (AgentAttributeSet) attributeSet();
    }

    @Override
    public Attribute<AgentSimulationContext> getThisAttribute() {
        // noinspection unchecked
        return (Attribute<AgentSimulationContext>) attribute();
    }

    @Override
    public Environment getEnvironment() {
        if (!config().areThreadsSynced())
            return localEnvironment;

        // Return cached environment if available
        if (cache().doesEnvironmentExist())
            return cache().getEnvironment();

        // Request environment from coordinator
        Environment requestedEnvironment;
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
