package helpers;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.EnvironmentGenerator;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

/**
 * Factories for constructing test-ready agents, environments, and configs.
 */
public final class TestFixtures {

    private TestFixtures() {}

    private static final AtomicInteger AGENT_COUNTER = new AtomicInteger(0);

    /**
     * Creates a minimal agent with a single counter property.
     */
    public static Agent agentWithCounter(String name) {
        AgentAttributeSet attrSet = TestAttributes.singlePropertyAgentSet(name, "stats", "counter");
        return new Agent(name, List.of(attrSet));
    }

    /**
     * Creates a bare agent with no attributes.
     */
    public static Agent emptyAgent(String name) {
        return new Agent(name, List.of());
    }

    /**
     * Creates an agent with a unique auto-generated name.
     */
    public static Agent uniqueAgent() {
        return agentWithCounter("agent_" + AGENT_COUNTER.getAndIncrement());
    }

    /**
     * Creates a minimal environment with a single tick-counting property.
     */
    public static Environment environmentWithTickCounter() {
        EnvironmentAttributeSet attrSet = TestAttributes.environmentAttributeSet(
                "env", "timing", new TestAttributes.EnvironmentTickProperty("envTick"));
        return new Environment("env", List.of(attrSet));
    }

    /**
     * Creates a bare environment with no attributes.
     */
    public static Environment emptyEnvironment() {
        return new Environment("env", List.of());
    }

    /**
     * A default agent generator that produces agents with counter properties.
     */
    public static DefaultAgentGenerator counterAgentGenerator() {
        return new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config) {
                return agentWithCounter("agent_" + index++);
            }
        };
    }

    /**
     * An environment generator that produces a minimal environment.
     */
    public static EnvironmentGenerator simpleEnvironmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config) {
                return environmentWithTickCounter();
            }
        };
    }

    /**
     * An environment generator that produces a bare environment.
     */
    public static EnvironmentGenerator emptyEnvironmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config) {
                return emptyEnvironment();
            }
        };
    }

    /**
     * Builds a standard synced config for integration tests.
     */
    public static Config syncedConfig(int population, int ticks, int threads) {
        return Config.builder()
                .populationSize(population)
                .tickCount(ticks)
                .threadCount(threads)
                .areThreadsSynced(true)
                .agentGenerator(counterAgentGenerator())
                .environmentGenerator(simpleEnvironmentGenerator())
                .build();
    }

    /**
     * Builds a standard unsynced config for integration tests.
     */
    public static Config unsyncedConfig(int population, int ticks, int threads) {
        return Config.builder()
                .populationSize(population)
                .tickCount(ticks)
                .threadCount(threads)
                .areThreadsSynced(false)
                .agentGenerator(counterAgentGenerator())
                .environmentGenerator(simpleEnvironmentGenerator())
                .build();
    }

    /**
     * An agent set aof set size.
     */
    public static AgentSet agentSetOfSize(int size) {
        AgentSet agentSet = new AgentSet();

        for (int i = 0; i < size; i++)
            agentSet.add(emptyAgent(String.valueOf(i)));

        return agentSet;
    }

    /**
     * An agent set consisting of given agents.
     */
    public static AgentSet agentSet(Agent... agents) {
        return new AgentSet(List.of(agents));
    }

    /**
     * A context cache.
     */
    public static ContextCache contextCache() {
        return new ContextCache();
    }

    /**
     * A mutable clock constructed using the tick count from a given config.
     */
    public static MutableClock mutableClockFromConfig(Config config) {
        return new MutableClock(config.tickCount());
    }

    /**
     * A request response controller constructed using a given config.
     */
    public static RequestResponseController requestResponseController(Config config) {
        return new RequestResponseController(config);
    }

    /**
     * An empty agent simulation context.
     */
    public static AgentSimulationContext emptyAgentSimulationContext(Config config) {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());

        return new AgentSimulationContext(
                agentSet.get(0),
                agentSet,
                config,
                contextCache(),
                mutableClockFromConfig(config),
                requestResponseController(config),
                emptyEnvironment(),
                new SplittableRandom()
        );
    }

    /**
     * An agent simulation context with a given clock.
     */
    public static AgentSimulationContext agentSimulationContextWithClock(
            Config config,
            MutableClock clock
    ) {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());

        return new AgentSimulationContext(
                agentSet.get(0),
                agentSet,
                config,
                contextCache(),
                clock,
                requestResponseController(config),
                emptyEnvironment(),
                new SplittableRandom()
        );
    }

    /**
     * An agent simulation context with a given agent.
     */
    public static AgentSimulationContext agentSimulationContextWithAgent(
            Agent agent,
            AgentSet agentSet,
            Config config
    ) {
        return new AgentSimulationContext(
                agent,
                agentSet,
                config,
                contextCache(),
                mutableClockFromConfig(config),
                requestResponseController(config),
                emptyEnvironment(),
                new SplittableRandom()
        );
    }

    /**
     * An agent simulation context with a given environment.
     */
    public static AgentSimulationContext agentSimulationContextWithEnvironment(
            Config config,
            Environment environment
    ) {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());

        return new AgentSimulationContext(
                agentSet.get(0),
                agentSet,
                config,
                contextCache(),
                mutableClockFromConfig(config),
                requestResponseController(config),
                environment,
                new SplittableRandom()
        );
    }

    /**
     * An agent simulation context with a given context cache,
     */
    public static AgentSimulationContext agentSimulationContextWithCache(
            Config config,
            ContextCache cache
    ) {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());

        return new AgentSimulationContext(
                agentSet.get(0),
                agentSet,
                config,
                cache,
                mutableClockFromConfig(config),
                requestResponseController(config),
                emptyEnvironment(),
                new SplittableRandom()
        );
    }

    /**
     * An agent simulation context with a given current attribute set.
     */
    public static AgentSimulationContext agentSimulationContextWithAttributeSet(
            Config config,
            AttributeSet<?,?> attributeSet
    ) {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());

        AgentSimulationContext agentSimulationContext = new AgentSimulationContext(
                agentSet.get(0),
                agentSet,
                config,
                contextCache(),
                mutableClockFromConfig(config),
                requestResponseController(config),
                emptyEnvironment(),
                new SplittableRandom()
        );

        agentSimulationContext.setCurrentAttributeSet(attributeSet);

        return agentSimulationContext;
    }

    /**
     * An agent simulation context with a given current attribute.
     */
    public static AgentSimulationContext agentSimulationContextWithAttribute(
            Config config,
            Attribute<?> attribute
    ) {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());

        AgentSimulationContext agentSimulationContext = new AgentSimulationContext(
                agentSet.get(0),
                agentSet,
                config,
                contextCache(),
                mutableClockFromConfig(config),
                requestResponseController(config),
                emptyEnvironment(),
                new SplittableRandom()
        );

        agentSimulationContext.setCurrentAttribute(attribute);

        return agentSimulationContext;
    }

    /**
     * An agent simulation context with a given random generator.
     */
    public static AgentSimulationContext agentSimulationContextWithRandomGenerator(
            Config config,
            RandomGenerator randomGenerator
    ) {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());

        return new AgentSimulationContext(
                agentSet.get(0),
                agentSet,
                config,
                contextCache(),
                mutableClockFromConfig(config),
                requestResponseController(config),
                emptyEnvironment(),
                randomGenerator
        );
    }

    public static void openToCloningModule(String... packages) {
        Module self = TestFixtures.class.getModule();
        Module cloning = com.rits.cloning.Cloner.class.getModule();
        for (String pkg : packages)
            self.addOpens(pkg, cloning);
    }
}
