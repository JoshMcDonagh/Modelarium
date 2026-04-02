package modelarium.contexts;

import modelarium.Config;
import modelarium.Entity;
import modelarium.agents.sets.AgentSet;
import modelarium.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

public class EnvironmentContext extends Context {
    public EnvironmentContext(
            Entity entity,
            AgentSet localAgentSet,
            Config config,
            ContextCache cache,
            RequestResponseInterface requestResponseInterface
    ) {
        super(entity, localAgentSet, config, cache, requestResponseInterface);
    }

    @Override
    public Environment getThisEntity() {
        return (Environment) entity();
    }

    @Override
    public Environment getEnvironment() {
        throw new UnsupportedOperationException("Context requester is already an Environment - use 'getThis()' instead");
    }
}
