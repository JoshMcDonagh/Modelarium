package modelarium.entities.contexts;

import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

public class AgentContext extends Context {
    private final Environment localEnvironment;

    public AgentContext(
            Agent entity,
            AgentSet localAgentSet,
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
        } catch (Exception e) {
            return null;
        }

        // Cache the result
        cache().addEnvironment(requestedEnvironment);

        return requestedEnvironment;
    }
}
