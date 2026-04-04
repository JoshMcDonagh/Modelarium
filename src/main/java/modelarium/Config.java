package modelarium;

import modelarium.agents.generators.AgentGenerator;
import modelarium.environments.EnvironmentGenerator;
import modelarium.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.results.Results;
import modelarium.scheduler.Scheduler;

public record Config(
        int populationSize,
        int epochs,
        int threadCount,
        AgentGenerator agentGenerator,
        EnvironmentGenerator environmentGenerator,
        Scheduler scheduler,
        AttributeSetLogDatabaseFactory runLogDatabaseFactory,
        boolean areProcessesSynced,
        Class<? extends Results> resultsClass,
        Results results
) {}
