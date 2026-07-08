package helpers;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.Entity;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.EnvironmentGenerator;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.lang.reflect.InvocationTargetException;
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
     * An empty simulation context.
     */
    public static <C extends SimulationContext> C emptySimulationContext(
            Class<C> contextClass,
            Config config
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());
        Environment environment = emptyEnvironment();

        Class<?> entityClass;
        Entity<?,?,?,?> entity;

        if (contextClass.equals(AgentSimulationContext.class)) {
            entityClass = Agent.class;
            entity = agentSet.get(0);
        }
        else if (contextClass.equals(EnvironmentSimulationContext.class)) {
            entityClass = Environment.class;
            entity = environment;
        }
        else {
            throw new IllegalArgumentException("'" + contextClass.getName() + "' is not supported");
        }

        return contextClass.getConstructor(
                entityClass,
                AgentSet.class,
                Config.class,
                ContextCache.class,
                MutableClock.class,
                RequestResponseController.class,
                Environment.class,
                RandomGenerator.class
        ).newInstance(
                entity,
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
     * A simulation context with a given clock.
     */
    public static <C extends SimulationContext> C simulationContextWithClock(
            Class<C> contextClass,
            Config config,
            MutableClock clock
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());
        Environment environment = emptyEnvironment();

        Class<?> entityClass;
        Entity<?,?,?,?> entity;

        if (contextClass.equals(AgentSimulationContext.class)) {
            entityClass = Agent.class;
            entity = agentSet.get(0);
        }
        else if (contextClass.equals(EnvironmentSimulationContext.class)) {
            entityClass = Environment.class;
            entity = environment;
        }
        else {
            throw new IllegalArgumentException("'" + contextClass.getName() + "' is not supported");
        }

        return contextClass.getConstructor(
                entityClass,
                AgentSet.class,
                Config.class,
                ContextCache.class,
                MutableClock.class,
                RequestResponseController.class,
                Environment.class,
                RandomGenerator.class
        ).newInstance(
                entity,
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
            Config config,
            Agent agent,
            AgentSet agentSet
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
     * A simulation context with a given environment.
     */
    public static <C extends SimulationContext> C simulationContextWithEnvironment(
            Class<C> contextClass,
            Config config,
            Environment environment
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());

        Class<?> entityClass;
        Entity<?,?,?,?> entity;

        if (contextClass.equals(AgentSimulationContext.class)) {
            entityClass = Agent.class;
            entity = agentSet.get(0);
        }
        else if (contextClass.equals(EnvironmentSimulationContext.class)) {
            entityClass = Environment.class;
            entity = environment;
        }
        else {
            throw new IllegalArgumentException("'" + contextClass.getName() + "' is not supported");
        }

        return contextClass.getConstructor(
                entityClass,
                AgentSet.class,
                Config.class,
                ContextCache.class,
                MutableClock.class,
                RequestResponseController.class,
                Environment.class,
                RandomGenerator.class
        ).newInstance(
                entity,
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
     * A simulation context with a given context cache,
     */
    public static <C extends SimulationContext> C simulationContextWithCache(
            Class<C> contextClass,
            Config config,
            ContextCache cache
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());
        Environment environment = emptyEnvironment();

        Class<?> entityClass;
        Entity<?,?,?,?> entity;

        if (contextClass.equals(AgentSimulationContext.class)) {
            entityClass = Agent.class;
            entity = agentSet.get(0);
        }
        else if (contextClass.equals(EnvironmentSimulationContext.class)) {
            entityClass = Environment.class;
            entity = environment;
        }
        else {
            throw new IllegalArgumentException("'" + contextClass.getName() + "' is not supported");
        }

        return contextClass.getConstructor(
                entityClass,
                AgentSet.class,
                Config.class,
                ContextCache.class,
                MutableClock.class,
                RequestResponseController.class,
                Environment.class,
                RandomGenerator.class
        ).newInstance(
                entity,
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
     * A simulation context with a given current attribute set.
     */
    public static <C extends SimulationContext> C simulationContextWithAttributeSet(
            Class<C> contextClass,
            Config config,
            AttributeSet<?,?> attributeSet
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());
        Environment environment = emptyEnvironment();

        Class<?> entityClass;
        Entity<?,?,?,?> entity;

        if (contextClass.equals(AgentSimulationContext.class)) {
            entityClass = Agent.class;
            entity = agentSet.get(0);
        }
        else if (contextClass.equals(EnvironmentSimulationContext.class)) {
            entityClass = Environment.class;
            entity = environment;
        }
        else {
            throw new IllegalArgumentException("'" + contextClass.getName() + "' is not supported");
        }

        C context = contextClass.getConstructor(
                entityClass,
                AgentSet.class,
                Config.class,
                ContextCache.class,
                MutableClock.class,
                RequestResponseController.class,
                Environment.class,
                RandomGenerator.class
        ).newInstance(
                entity,
                agentSet,
                config,
                contextCache(),
                mutableClockFromConfig(config),
                requestResponseController(config),
                emptyEnvironment(),
                new SplittableRandom()
        );

        context.setCurrentAttributeSet(attributeSet);

        return context;
    }

    /**
     * A simulation context with a given current attribute.
     */
    public static <C extends SimulationContext> C simulationContextWithAttribute(
            Class<C> contextClass,
            Config config,
            Attribute<?> attribute
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());
        Environment environment = emptyEnvironment();

        Class<?> entityClass;
        Entity<?,?,?,?> entity;

        if (contextClass.equals(AgentSimulationContext.class)) {
            entityClass = Agent.class;
            entity = agentSet.get(0);
        }
        else if (contextClass.equals(EnvironmentSimulationContext.class)) {
            entityClass = Environment.class;
            entity = environment;
        }
        else {
            throw new IllegalArgumentException("'" + contextClass.getName() + "' is not supported");
        }

        C context = contextClass.getConstructor(
                entityClass,
                AgentSet.class,
                Config.class,
                ContextCache.class,
                MutableClock.class,
                RequestResponseController.class,
                Environment.class,
                RandomGenerator.class
        ).newInstance(
                entity,
                agentSet,
                config,
                contextCache(),
                mutableClockFromConfig(config),
                requestResponseController(config),
                emptyEnvironment(),
                new SplittableRandom()
        );

        context.setCurrentAttribute(attribute);

        return context;
    }

    /**
     * A simulation context with a given random generator.
     */
    public static <C extends SimulationContext> C simulationContextWithRandomGenerator(
            Class<C> contextClass,
            Config config,
            RandomGenerator randomGenerator
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize());
        Environment environment = emptyEnvironment();

        Class<?> entityClass;
        Entity<?,?,?,?> entity;

        if (contextClass.equals(AgentSimulationContext.class)) {
            entityClass = Agent.class;
            entity = agentSet.get(0);
        }
        else if (contextClass.equals(EnvironmentSimulationContext.class)) {
            entityClass = Environment.class;
            entity = environment;
        }
        else {
            throw new IllegalArgumentException("'" + contextClass.getName() + "' is not supported");
        }

        return contextClass.getConstructor(
                entityClass,
                AgentSet.class,
                Config.class,
                ContextCache.class,
                MutableClock.class,
                RequestResponseController.class,
                Environment.class,
                RandomGenerator.class
        ).newInstance(
                entity,
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
