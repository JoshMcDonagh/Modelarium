package modelarium;

import modelarium.entities.agents.generators.AgentGenerator;
import modelarium.entities.environments.EnvironmentGenerator;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import modelarium.scheduler.InOrderScheduler;
import modelarium.scheduler.Scheduler;

import java.time.Duration;
import java.util.Objects;

/**
 * Record for containing the configuration settings of a model.
 *
 * <p>This record is responsible for storing the configurations needed for running a model. It includes a static builder
 * class ({@link ConfigBuilder}) that can be used to assist in the construction of an instance of this record.
 *
 * @param populationSize the number of agents the model will contain
 * @param tickCount the number of time steps the model will perform
 * @param threadCount the number of worker threads the model will use
 * @param threadTimeout the maximum duration a thread should wait for a response from another thread
 * @param areThreadsSynced whether the threads are synchronised or not, determining if interaction of entities over
 *                         multiple threads is possible
 * @param agentGenerator an instance of an agent generator class which is used by the model to generate agents
 * @param environmentGenerator an instance of an environment generator class which is used by the model to generate an
 *                             environment
 * @param scheduler an instance of a scheduler class which is used by the model to order the running of each agent as
 *                  the model progresses
 * @param runLogDatabaseFactory an instance of an attribute set log database factory which the model uses to create a
 *                              database that can store attribute logs as the model progresses
 * @param seed the seed value used for the core random generator used by the model
 */
public record Config(
        int populationSize,
        int tickCount,
        int threadCount,
        Duration threadTimeout,
        boolean areThreadsSynced,
        AgentGenerator agentGenerator,
        EnvironmentGenerator environmentGenerator,
        Scheduler scheduler,
        AttributeSetLogDatabaseFactory runLogDatabaseFactory,
        long seed
) {
    /**
     * Creates a new instance of {@link ConfigBuilder} that can assist in the construction of a {@link Config} record.
     *
     * @return a new {@link ConfigBuilder} instance
     */
    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    /**
     * Class for assisting in the constructing of a {@link Config} record.
     */
    public static class ConfigBuilder {
        private int populationSize = 100;
        private int tickCount = 100;
        private int threadCount = 2;
        private Duration threadTimeout = Duration.ofSeconds(60);
        private boolean areThreadsSynced = true;
        private AgentGenerator agentGenerator;
        private EnvironmentGenerator environmentGenerator;
        private Scheduler scheduler = new InOrderScheduler();
        private AttributeSetLogDatabaseFactory runLogDatabaseFactory = new MemoryBasedAttributeSetLogDatabaseFactory();
        private long seed = System.nanoTime();

        /**
         * Sets the model's population size.
         *
         * @param populationSize the number of agents the model will contain
         * @return this updated {@link ConfigBuilder} instance
         */
        public ConfigBuilder populationSize(int populationSize) {
            this.populationSize = populationSize;
            return this;
        }

        /**
         * Sets the number of ticks in the model.
         *
         * @param tickCount the number of time steps the model will perform
         * @return this updated {@link ConfigBuilder} instance
         */
        public ConfigBuilder tickCount(int tickCount) {
            this.tickCount = tickCount;
            return this;
        }

        /**
         * Sets the number of worker threads the model will use to run.
         *
         * @param threadCount the number of worker threads the model will use
         * @return this updated {@link ConfigBuilder} instance
         */
        public ConfigBuilder threadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        /**
         * (Optional) Sets the duration of time needed before a thread's request to another thread times out.
         *
         * <p>If this method is not used, the thread timeout duration is set to 60 seconds by default.
         *
         * @param threadTimeout the maximum duration a thread should wait for a response from another thread
         * @return this updated {@link ConfigBuilder} instance
         */
        public ConfigBuilder threadTimeout(Duration threadTimeout) {
            this.threadTimeout = threadTimeout;
            return this;
        }

        /**
         * Sets whether the model's threads are synchronised or not.
         *
         * @param areThreadsSynced whether the threads are synchronised or not, determining if interaction of entities
         *                         over multiple threads is possible
         * @return this updated {@link ConfigBuilder} instance
         */
        public ConfigBuilder areThreadsSynced(boolean areThreadsSynced) {
            this.areThreadsSynced = areThreadsSynced;
            return this;
        }

        /**
         * Sets the {@link AgentGenerator} instance the model will use to generate agents.
         *
         * @param agentGenerator an instance of an agent generator class which is used by the model to generate agents
         * @return this updated {@link ConfigBuilder} instance
         */
        public ConfigBuilder agentGenerator(AgentGenerator agentGenerator) {
            this.agentGenerator = agentGenerator;
            return this;
        }

        /**
         * Sets the {@link EnvironmentGenerator} instance the model will use to generate the environment.
         *
         * @param environmentGenerator an instance of an environment generator class which is used by the model to generate an
         *                             environment
         * @return this updated {@link ConfigBuilder} instance
         */
        public ConfigBuilder environmentGenerator(EnvironmentGenerator environmentGenerator) {
            this.environmentGenerator = environmentGenerator;
            return this;
        }

        /**
         * (Optional) Sets the {@link Scheduler} instance the model will use to sequence the running of agents at each
         * time step.
         *
         * <p>If this method is not used, the model is set by default to use a newly created instance of {@link InOrderScheduler}
         * by default.
         *
         * @param scheduler an instance of a scheduler class which is used by the model to order the running of each agent as
         *                  the model progresses
         * @return this updated {@link ConfigBuilder} instance
         */
        public ConfigBuilder scheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        /**
         * (Optional) Sets the {@link AttributeSetLogDatabaseFactory} instance the model will use to generate the
         * database for storing the attribute logs of each entity.
         *
         * <p>If this method is not used, the model's attribute set log database factory is set to a newly created
         * instance of {@link MemoryBasedAttributeSetLogDatabaseFactory} by default.
         *
         * @param runLogDatabaseFactory an instance of an attribute set log database factory which the model uses to create a
         *                              database that can store attribute logs as the model progresses
         * @return this updated {@link ConfigBuilder} instance
         */
        public ConfigBuilder runLogDatabaseFactory(AttributeSetLogDatabaseFactory runLogDatabaseFactory) {
            this.runLogDatabaseFactory = runLogDatabaseFactory;
            return this;
        }

        /**
         * (Optional) Sets the seed value the model will use to generate its core random generator which model components and
         * entities can then use.
         *
         * <p>If this method is not used, the seed value is set to the current time in nanoseconds by default.
         *
         * @param seed the seed value used for the core random generator used by the model
         * @return this updated {@link ConfigBuilder} instance
         */
        public ConfigBuilder seed(long seed) {
            this.seed = seed;
            return this;
        }

        /**
         * Creates a new instance of {@link Config} based on the configuration settings provided using the other
         * methods.
         *
         * @return a new {@link Config} instance
         */
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
                    threadTimeout,
                    areThreadsSynced,
                    agentGenerator,
                    environmentGenerator,
                    scheduler,
                    runLogDatabaseFactory,
                    seed
            );
        }
    }
}
