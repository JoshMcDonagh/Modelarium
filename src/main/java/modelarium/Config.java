package modelarium;

import modelarium.agents.generators.AgentGenerator;
import modelarium.logging.databases.AttributeSetRunLogDatabase;
import modelarium.environments.EnvironmentGenerator;
import modelarium.results.Results;
import modelarium.scheduler.Scheduler;

public record Config(
        int populationSize,
        int epochs,
        int threadCount,
        AgentGenerator agentGenerator,
        EnvironmentGenerator environmentGenerator,
        Scheduler scheduler,
        AttributeSetRunLogDatabase runLogDatabase,
        boolean areProcessesSynced,
        Class<? extends Results> resultsClass,
        Results results
) {}
