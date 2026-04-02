package modelarium.contexts;

import modelarium.Config;
import modelarium.Entity;
import modelarium.agents.sets.AgentSet;
import modelarium.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.multithreading.utils.WorkerCache;

public class EnvironmentContext extends Context {
    public EnvironmentContext(
            Entity entity,
            AgentSet localAgentSet,
            Config config,
            WorkerCache cache,
            RequestResponseInterface requestResponseInterface
    ) {
        super(entity, localAgentSet, config, cache, requestResponseInterface);
    }

    @Override
    public Environment getThis() {
        return (Environment) entity();
    }

    @Override
    public Environment getEnvironment() {
        throw new UnsupportedOperationException("Context requester is already an Environment - use 'getThis()' instead");
    }
}
