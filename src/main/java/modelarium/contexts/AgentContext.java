package modelarium.contexts;

import modelarium.Clock;
import modelarium.Config;
import modelarium.Entity;
import modelarium.agents.Agent;
import modelarium.agents.sets.AgentSet;
import modelarium.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

public class AgentContext extends Context {
    private final Environment localEnvironment;

    public AgentContext(
            Entity entity,
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
    public Environment getEnvironment() {
        if (!config().areProcessesSynced())
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
