package modelarium.entities.contexts;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.environments.Environment;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;
import modelarium.exceptions.EnvironmentNotFoundException;
import modelarium.exceptions.SimulationInterruptedException;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

public class AgentContext extends Context {
    private final Environment localEnvironment;

    public AgentContext(
            Agent entity,
            MutableAgentSet localAgentSet,
            Config config,
            ContextCache cache,
            Clock clock,
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
    public Attribute<AgentContext> getThisAttribute() {
        return (Attribute<AgentContext>) attribute();
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
                    + "' could not be found");
        }

        // Cache the result
        cache().addEnvironment(requestedEnvironment);

        return requestedEnvironment;
    }
}
