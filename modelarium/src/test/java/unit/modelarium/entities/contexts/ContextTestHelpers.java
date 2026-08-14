package unit.modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.MutableEntity;
import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.*;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.AttributeBase;
import modelarium.entities.attributes.sets.mutable.MutableAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.entities.environments.generators.EnvironmentGenerator;
import modelarium.entities.agents.immutable.ImmutableAgent;
import modelarium.entities.agents.immutable.ImmutableAgentSet;
import modelarium.entities.ImmutableEntity;
import modelarium.entities.environments.ImmutableEnvironment;
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

    static MutableAgent emptyAgent(String name) {
        return new MutableAgent(name, List.of());
    }

    static MutableAgentSet agentSet(MutableAgent... agents) {
        return new MutableAgentSet(List.of(agents));
    }

    static MutableAgentSet agentSetOfSize(int size) {
        MutableAgentSet agentSet = new MutableAgentSet();

        for (int i = 0; i < size; i++) {
            agentSet.add(emptyAgent(String.valueOf(agentCount)));
            agentCount++;
        }

        return agentSet;
    }

    static MutableEnvironment emptyEnvironment() {
        return new MutableEnvironment(List.of());
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
            protected MutableAgent generateAgent(Config config, RandomGenerator random) {
                return emptyAgent("agent_" + index++);
            }
        };
    }

    private static EnvironmentGenerator environmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public MutableEnvironment generateEnvironment(Config config, RandomGenerator random) {
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
    static MutableAgentAttributeSet singlePropertyAgentSet(String ownerName, String attributeSetName, String propertyName) {
        return new MutableAgentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(new AgentCounterProperty(propertyName))
        );
    }

    @SuppressWarnings("unchecked")
    static MutableEnvironmentAttributeSet singlePropertyEnvironmentSet(String ownerName, String attributeSetName, String propertyName) {
        return new MutableEnvironmentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(new EnvironmentTickProperty(propertyName))
        );
    }

    private static <C extends SimulationContext> C simulationContext(
            Class<C> contextClass,
            Config config,
            MutableAgentSet agentSet,
            ContextCache contextCache,
            MutableClock clock,
            MutableEnvironment thisEnvironment,
            MutableEnvironment localEnvironment,
            RandomGenerator randomGenerator
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> entityClass;
        MutableEntity<?,?,?,?> entity;

        if (contextClass.equals(AgentSimulationContext.class)) {
            entityClass = MutableAgent.class;
            entity = agentSet.get(0);
        }
        else if (contextClass.equals(EnvironmentSimulationContext.class)) {
            entityClass = MutableEnvironment.class;
            entity = thisEnvironment;
        }
        else {
            throw new IllegalArgumentException("'" + contextClass.getName() + "' is not supported");
        }

        return contextClass.getConstructor(
                entityClass,
                MutableAgentSet.class,
                Config.class,
                ContextCache.class,
                MutableClock.class,
                RequestResponseController.class,
                MutableEnvironment.class,
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
        MutableEnvironment environment = emptyEnvironment();
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
            MutableAgentSet agentSet
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
            MutableAgent agent,
            MutableAgentSet agentSet
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
            MutableEnvironment environment
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
            MutableAttributeSet<?,?> attributeSet
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

    static MutableAgent getMutableFromImmutable(ImmutableAgent immutableAgent) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        return (MutableAgent) getMutableEntityMethod.invoke(immutableAgent);
    }

    static MutableEnvironment getMutableFromImmutable(ImmutableEnvironment immutableEnvironment) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        return (MutableEnvironment) getMutableEntityMethod.invoke(immutableEnvironment);
    }

    static MutableAgentSet getMutableFromImmutable(ImmutableAgentSet immutableAgentSet) throws NoSuchFieldException, IllegalAccessException {
        Field agentSetField = ImmutableAgentSet.class.getDeclaredField("agentSet");
        agentSetField.setAccessible(true);
        return (MutableAgentSet) agentSetField.get(immutableAgentSet);
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

    private static <C extends SimulationContext, T, E extends MutableEntity<?,?,?,?>> C generateContextWithMockRequestResponseInterface(
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
            MutableAgent agent = (MutableAgent) thisEntity;
            MutableAgentSet agentSet = agentSetOfSize(config.populationSize() - 1);
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
                    (MutableEnvironment) thisEntity
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

    static <C extends SimulationContext, T, E extends MutableEntity<?,?,?,?>> C generateContextWhereRequestResponseInterfaceMethodReturns(
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

    static <C extends SimulationContext, T extends Throwable, E extends MutableEntity<?,?,?,?>> C generateContextWhereRequestResponseInterfaceMethodThrows(
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

    static void assertSetsContainSameAgents(MutableAgentSet expected, ImmutableAgentSet actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++)
            assertEquals(expected.get(i).name(), actual.get(i).name());
    }
}
