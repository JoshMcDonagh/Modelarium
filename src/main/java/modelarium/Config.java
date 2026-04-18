package modelarium;

import modelarium.entities.agents.generators.AgentGenerator;
import modelarium.entities.environments.EnvironmentGenerator;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import modelarium.scheduler.InOrderScheduler;
import modelarium.scheduler.Scheduler;

import java.util.Objects;

public record Config(
        int populationSize,
        int tickCount,
        int threadCount,
        long threadTimeoutSeconds,
        boolean areThreadsSynced,
        AgentGenerator agentGenerator,
        EnvironmentGenerator environmentGenerator,
        Scheduler scheduler,
        AttributeSetLogDatabaseFactory runLogDatabaseFactory
) {
    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    public static class ConfigBuilder {
        private int populationSize = 100;
        private int tickCount = 100;
        private int threadCount = 2;
        private long threadTimeoutSeconds = 60;
        private boolean areThreadsSynced = true;
        private AgentGenerator agentGenerator;
        private EnvironmentGenerator environmentGenerator;
        private Scheduler scheduler = new InOrderScheduler();
        private AttributeSetLogDatabaseFactory runLogDatabaseFactory = new MemoryBasedAttributeSetLogDatabaseFactory();

        public ConfigBuilder populationSize(int populationSize) {
            this.populationSize = populationSize;
            return this;
        }

        public ConfigBuilder tickCount(int tickCount) {
            this.tickCount = tickCount;
            return this;
        }

        public ConfigBuilder threadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public ConfigBuilder threadTimeoutSeconds(long threadTimeoutSeconds) {
            this.threadTimeoutSeconds = threadTimeoutSeconds;
            return this;
        }

        public ConfigBuilder areThreadsSynced(boolean areThreadsSynced) {
            this.areThreadsSynced = areThreadsSynced;
            return this;
        }

        public ConfigBuilder agentGenerator(AgentGenerator agentGenerator) {
            this.agentGenerator = agentGenerator;
            return this;
        }

        public ConfigBuilder environmentGenerator(EnvironmentGenerator environmentGenerator) {
            this.environmentGenerator = environmentGenerator;
            return this;
        }

        public ConfigBuilder scheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public ConfigBuilder runLogDatabaseFactory(AttributeSetLogDatabaseFactory runLogDatabaseFactory) {
            this.runLogDatabaseFactory = runLogDatabaseFactory;
            return this;
        }

        public Config build() {
            Objects.requireNonNull(agentGenerator, "agentGenerator must be set");
            Objects.requireNonNull(environmentGenerator, "environmentGenerator must be set");

            if (populationSize <= 0)
                throw new IllegalArgumentException("populationSize must be greater than 0");

            if (tickCount <= 0)
                throw new IllegalArgumentException("tickCount must be greater than 0");

            if (threadCount <= 0)
                throw new IllegalArgumentException("threadCount must be greater than 0");

            return new Config(
                    populationSize,
                    tickCount,
                    threadCount,
                    threadTimeoutSeconds,
                    areThreadsSynced,
                    agentGenerator,
                    environmentGenerator,
                    scheduler,
                    runLogDatabaseFactory
            );
        }
    }
}
