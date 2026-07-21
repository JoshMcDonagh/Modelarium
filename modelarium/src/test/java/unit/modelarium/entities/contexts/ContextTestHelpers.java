package unit.modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.Entity;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.*;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.EnvironmentGenerator;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;
import modelarium.entities.immutable.ImmutableEntity;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.mockito.stubbing.Stubber;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.SplittableRandom;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;

class ContextTestHelpers {
    private ContextTestHelpers() {}

    private static int agentCount = 0;

    static Agent emptyAgent(String name) {
        return new Agent(name, List.of());
    }

    static AgentSet agentSet(Agent... agents) {
        return new AgentSet(List.of(agents));
    }

    static AgentSet agentSetOfSize(int size) {
        AgentSet agentSet = new AgentSet();

        for (int i = 0; i < size; i++) {
            agentSet.add(emptyAgent(String.valueOf(agentCount)));
            agentCount++;
        }

        return agentSet;
    }

    static Environment emptyEnvironment() {
        return new Environment(List.of());
    }

    static ContextCache contextCache() {
        return new ContextCache();
    }

    static MutableClock mutableClockFromConfig(Config config) {
        return new MutableClock(config.tickCount());
    }

    static RequestResponseController requestResponseController(Config config) {
        return new RequestResponseController(config);
    }

    private static DefaultAgentGenerator agentGenerator() {
        return new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                return emptyAgent("agent_" + index++);
            }
        };
    }

    private static EnvironmentGenerator environmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config, RandomGenerator random) {
                return emptyEnvironment();
            }
        };
    }

    static Config syncedConfig(int populationSize, int tickCount, int threadCount) {
        return Config.builder()
                .populationSize(populationSize)
                .tickCount(tickCount)
                .threadCount(threadCount)
                .areThreadsSynced(true)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build();
    }

    static Config unsyncedConfig(int populationSize, int tickCount, int threadCount) {
        return Config.builder()
                .populationSize(populationSize)
                .tickCount(tickCount)
                .threadCount(threadCount)
                .areThreadsSynced(false)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build();
    }

    static class AgentCounterProperty extends AgentProperty<Double> {
        private double value = 0.0;

        AgentCounterProperty(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC, Double.class);
        }

        @Override
        protected void run(AgentContext context) {
            value += 1.0;
        }

        @Override
        protected void set(AgentContext context, Double value) {
            this.value = value;
        }

        @Override
        protected Double get(AgentContext context) {
            return value;
        }
    }

    static class EnvironmentTickProperty extends EnvironmentProperty<Integer> {
        private int tick = 0;

        EnvironmentTickProperty(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(EnvironmentContext context) {
            tick++;
        }

        @Override
        protected void set(EnvironmentContext context, Integer value) {
            this.tick = value;
        }

        @Override
        protected Integer get(EnvironmentContext context) {
            return tick;
        }
    }

    @SuppressWarnings("unchecked")
    static AgentAttributeSet singlePropertyAgentSet(String ownerName, String attributeSetName, String propertyName) {
        return new AgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(new AgentCounterProperty(propertyName))
        );
    }

    @SuppressWarnings("unchecked")
    static EnvironmentAttributeSet singlePropertyEnvironmentSet(String ownerName, String attributeSetName, String propertyName) {
        return new EnvironmentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(new EnvironmentTickProperty(propertyName))
        );
    }

    private static <C extends SimulationContext> C simulationContext(
            Class<C> contextClass,
            Config config,
            AgentSet agentSet,
            ContextCache contextCache,
            MutableClock clock,
            Environment thisEnvironment,
            Environment localEnvironment,
            RandomGenerator randomGenerator
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> entityClass;
        Entity<?,?,?,?> entity;

        if (contextClass.equals(AgentSimulationContext.class)) {
            entityClass = Agent.class;
            entity = agentSet.get(0);
        }
        else if (contextClass.equals(EnvironmentSimulationContext.class)) {
            entityClass = Environment.class;
            entity = thisEnvironment;
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
                contextCache,
                clock,
                requestResponseController(config),
                localEnvironment,
                randomGenerator
        );
    }

    static <C extends SimulationContext> C emptySimulationContext(
            Class<C> contextClass,
            Config config
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Environment environment = emptyEnvironment();
        return simulationContext(
                contextClass,
                config,
                agentSetOfSize(config.populationSize()),
                contextCache(),
                mutableClockFromConfig(config),
                environment,
                environment,
                new SplittableRandom()
        );
    }

    static <C extends SimulationContext> C simulationContextWithAgentSet(
            Class<C> contextClass,
            Config config,
            AgentSet agentSet
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return simulationContext(
                contextClass,
                config,
                agentSet,
                contextCache(),
                mutableClockFromConfig(config),
                emptyEnvironment(),
                emptyEnvironment(),
                new SplittableRandom()
        );
    }

    static <C extends SimulationContext> C simulationContextWithClock(
            Class<C> contextClass,
            Config config,
            MutableClock clock
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return simulationContext(
                contextClass,
                config,
                agentSetOfSize(config.populationSize()),
                contextCache(),
                clock,
                emptyEnvironment(),
                emptyEnvironment(),
                new SplittableRandom()
        );
    }

    static AgentSimulationContext agentSimulationContextWithAgent(
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

    static <C extends SimulationContext> C simulationContextWithEnvironment(
            Class<C> contextClass,
            Config config,
            Environment environment
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return simulationContext(
                contextClass,
                config,
                agentSetOfSize(config.populationSize()),
                contextCache(),
                mutableClockFromConfig(config),
                environment,
                environment,
                new SplittableRandom()
        );
    }

    static <C extends SimulationContext> C simulationContextWithCache(
            Class<C> contextClass,
            Config config,
            ContextCache cache
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return simulationContext(
                contextClass,
                config,
                agentSetOfSize(config.populationSize()),
                cache,
                mutableClockFromConfig(config),
                emptyEnvironment(),
                emptyEnvironment(),
                new SplittableRandom()
        );
    }

    static <C extends SimulationContext> C simulationContextWithAttributeSet(
            Class<C> contextClass,
            Config config,
            AttributeSet<?,?> attributeSet
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        C context = simulationContext(
                contextClass,
                config,
                agentSetOfSize(config.populationSize()),
                contextCache(),
                mutableClockFromConfig(config),
                emptyEnvironment(),
                emptyEnvironment(),
                new SplittableRandom()
        );

        context.setCurrentAttributeSet(attributeSet);

        return context;
    }

    static <C extends SimulationContext> C simulationContextWithAttribute(
            Class<C> contextClass,
            Config config,
            AttributeBase<?> attribute
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        C context = simulationContext(
                contextClass,
                config,
                agentSetOfSize(config.populationSize()),
                contextCache(),
                mutableClockFromConfig(config),
                emptyEnvironment(),
                emptyEnvironment(),
                new SplittableRandom()
        );

        context.setCurrentAttribute(attribute);

        return context;
    }

    static <C extends SimulationContext> C simulationContextWithRandomGenerator(
            Class<C> contextClass,
            Config config,
            RandomGenerator randomGenerator
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return simulationContext(
                contextClass,
                config,
                agentSetOfSize(config.populationSize()),
                contextCache(),
                mutableClockFromConfig(config),
                emptyEnvironment(),
                emptyEnvironment(),
                randomGenerator
        );
    }

    static Agent getMutableFromImmutable(ImmutableAgent immutableAgent) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        return (Agent) getMutableEntityMethod.invoke(immutableAgent);
    }

    static Environment getMutableFromImmutable(ImmutableEnvironment immutableEnvironment) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        return (Environment) getMutableEntityMethod.invoke(immutableEnvironment);
    }

    static AgentSet getMutableFromImmutable(ImmutableAgentSet immutableAgentSet) throws NoSuchFieldException, IllegalAccessException {
        Field agentSetField = ImmutableAgentSet.class.getDeclaredField("agentSet");
        agentSetField.setAccessible(true);
        return (AgentSet) agentSetField.get(immutableAgentSet);
    }

    static <E extends Throwable, C extends Throwable> void assertCorrectExceptionThrown(
            Class<E> expectedException,
            Executable executableToCheck,
            String expectedMessage,
            Class<C> causingException
    ) {
        E exception = assertThrows(expectedException, executableToCheck);
        assertEquals(expectedMessage, exception.getMessage());

        if (causingException != null)
            assertInstanceOf(causingException, exception.getCause());
    }

    static <E extends Throwable> void assertCorrectExceptionThrown(
            Class<E> expectedException,
            Executable executableToCheck,
            String expectedMessage
    ) {
        assertCorrectExceptionThrown(expectedException, executableToCheck, expectedMessage, null);
    }

    private static Object anyFor(Class<?> parameterType) {
        if (!parameterType.isPrimitive())
            return any();
        if (parameterType == int.class)
            return anyInt();
        if (parameterType == long.class)
            return anyLong();
        if (parameterType == double.class)
            return anyDouble();
        if (parameterType == float.class)
            return anyFloat();
        if (parameterType == boolean.class)
            return anyBoolean();
        if (parameterType == byte.class)
            return anyByte();
        if (parameterType == short.class)
            return anyShort();
        if (parameterType == char.class)
            return anyChar();
        throw new IllegalArgumentException("Unhandled primitive parameter type: " + parameterType);
    }

    private static <C extends SimulationContext, T, E extends Entity<?,?,?,?>> C generateContextWithMockRequestResponseInterface(
            Class<C> contextClass,
            T returned,
            String methodName,
            Class<?>[] methodParameterTypes,
            Config config,
            E thisEntity,
            Function<T, Stubber> doMethod
    ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        Method method = RequestResponseInterface.class.getDeclaredMethod(methodName, methodParameterTypes);
        RequestResponseInterface mockRequestResponseInterface = mock(RequestResponseInterface.class);
        RequestResponseInterface stubbing = doMethod.apply(returned).when(mockRequestResponseInterface);

        Object[] args = new Object[methodParameterTypes.length];
        for (int i = 0; i < args.length; i++)
            args[i] = anyFor(methodParameterTypes[i]);
        method.invoke(stubbing, args);

        C context;

        if (contextClass.equals(AgentSimulationContext.class)) {
            Agent agent = (Agent) thisEntity;
            AgentSet agentSet = agentSetOfSize(config.populationSize() - 1);
            agentSet.add(agent);
            context = (C) agentSimulationContextWithAgent(
                    config,
                    agent,
                    agentSet
            );
        }
        else if (contextClass.equals(EnvironmentSimulationContext.class)) {
            context = simulationContextWithEnvironment(
                    contextClass,
                    config,
                    (Environment) thisEntity
            );
        }
        else {
            throw new IllegalArgumentException("'" + contextClass.getName() + "' is not supported");
        }

        Field requestResponseInterfaceField = SimulationContext.class.getDeclaredField(
                "requestResponseInterface"
        );
        requestResponseInterfaceField.setAccessible(true);
        requestResponseInterfaceField.set(context, mockRequestResponseInterface);

        return context;
    }

    static <C extends SimulationContext, T, E extends Entity<?,?,?,?>> C generateContextWhereRequestResponseInterfaceMethodReturns(
            Class<C> contextClass,
            T returned,
            String methodName,
            Class<?>[] methodParameterTypes,
            Config config,
            E thisEntity
    ) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        return generateContextWithMockRequestResponseInterface(
                contextClass,
                returned,
                methodName,
                methodParameterTypes,
                config,
                thisEntity,
                Mockito::doReturn
        );
    }

    static <C extends SimulationContext, T extends Throwable, E extends Entity<?,?,?,?>> C generateContextWhereRequestResponseInterfaceMethodThrows(
            Class<C> contextClass,
            Class<T> exceptionClass,
            String methodName,
            Class<?>[] methodParameterTypes,
            Config config,
            E thisEntity
    ) throws IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        return generateContextWithMockRequestResponseInterface(
                contextClass,
                exceptionClass,
                methodName,
                methodParameterTypes,
                config,
                thisEntity,
                v -> Mockito.doThrow(mock((Class<Throwable>) v))
        );
    }

    static void assertSetsContainSameAgents(AgentSet expected, ImmutableAgentSet actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++)
            assertEquals(expected.get(i).name(), actual.get(i).name());
    }
}
