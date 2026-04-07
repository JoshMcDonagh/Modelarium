package modelarium;

import modelarium.entities.agents.generators.AgentGenerator;
import modelarium.entities.environments.EnvironmentGenerator;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.scheduler.Scheduler;

public record Config(
        int populationSize,
        int tickCount,
        int threadCount,
        boolean areThreadsSynced,
        AgentGenerator agentGenerator,
        EnvironmentGenerator environmentGenerator,
        Scheduler scheduler,
        AttributeSetLogDatabaseFactory runLogDatabaseFactory
) {}
