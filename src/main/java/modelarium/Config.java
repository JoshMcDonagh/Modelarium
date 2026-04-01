package modelarium;

import modelarium.agents.generators.AgentGenerator;
import modelarium.logging.databases.AttributeSetRunLogDatabase;
import modelarium.environments.EnvironmentGenerator;
import modelarium.results.Results;
import modelarium.scheduler.ModelScheduler;

public record Config(
        int numOfAgents,
        int numOfCores,
        int numOfTicksToRun,
        int numOfWarmUpTicks,
        AgentGenerator agentGenerator,
        EnvironmentGenerator environmentGenerator,
        ModelScheduler modelScheduler,
        AttributeSetRunLogDatabase runLogDatabase,
        boolean areProcessesSynced,
        Class<? extends Results> resultsClass,
        Results results
) {}
