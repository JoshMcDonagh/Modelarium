package modelarium;

import modelarium.agents.AgentGenerator;
import modelarium.attributes.results.databases.AttributeSetRunLogDatabase;
import modelarium.environments.EnvironmentGenerator;
import modelarium.results.Results;
import modelarium.scheduler.ModelScheduler;

public record ModelConfig(
        int numOfAgents,
        int numOfCores,
        int numOfTicksToRun,
        int numOfWarmUpTicks,
        AgentGenerator agentGenerator,
        EnvironmentGenerator environmentGenerator,
        ModelScheduler modelScheduler,
        AttributeSetRunLogDatabase runLogDatabase,
        boolean areProcessesSynced,
        boolean doAgentStoresHoldAgentClones,
        boolean isCacheUsed,
        Class<? extends Results> resultsClass,
        Results results
) {}
