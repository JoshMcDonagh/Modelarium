package unit.modelarium.entities.contexts;

import helpers.TestAttributes;
import helpers.TestFixtures;
import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;
import modelarium.entities.immutable.ImmutableEntity;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.exceptions.AgentNotFoundException;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;
import modelarium.exceptions.SimulationInterruptedException;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.SplittableRandom;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EnvironmentSimulationContextTest {
    @Test
    public void testGetThisEntity() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Environment environment = TestFixtures.emptyEnvironment();
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithEnvironment(
                EnvironmentSimulationContext.class,
                config,
                environment
        );

        assertSame(environment, context.getThisEntity());
    }

    @Test
    public void testGetThisAttributeSet() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        EnvironmentAttributeSet set = TestAttributes.singlePropertyEnvironmentSet("owner", "time", "ticks");
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithAttributeSet(
                EnvironmentSimulationContext.class,
                config,
                set
        );

        assertSame(set, context.getThisAttributeSet());
    }

    @Test
    public void testGetThisAttribute() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Attribute<?> attribute = new TestAttributes.EnvironmentTickProperty("ticker");
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithAttribute(
                EnvironmentSimulationContext.class,
                config,
                attribute
        );

        assertSame(attribute, context.getThisAttribute());
    }

    @Test
    public void testGetEnvironment() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        EnvironmentSimulationContext context = TestFixtures.emptySimulationContext(
                EnvironmentSimulationContext.class,
                config
        );

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                context::getEnvironment
        );

        assertEquals(
                "Context requester is already an Environment - use 'getThisEntity()' instead",
                exception.getMessage()
        );
    }

    @Test
    public void testGetClock() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        MutableClock clock = TestFixtures.mutableClockFromConfig(config);
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithClock(
                EnvironmentSimulationContext.class,
                config,
                clock
        );

        assertSame(clock, context.getClock());
    }

    @Test
    public void testDoesAgentExistInThisCoreTrue() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int populationSize = 20;

        Config config = TestFixtures.syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize);
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                config,
                agentSet
        );

        String agentName = agentSet.get(12).name();

        assertTrue(context.doesAgentExistInThisCore(agentName));
    }

    @Test
    public void testDoesAgentExistInThisCoreFalse() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int populationSize = 20;

        Config config = TestFixtures.syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize);
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                config,
                agentSet
        );

        String agentName = "James";

        assertFalse(context.doesAgentExistInThisCore(agentName));
    }

    @Test
    public void testGetRandom() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config  = TestFixtures.syncedConfig(2, 10, 1);
        SplittableRandom randomGenerator = new SplittableRandom();
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithRandomGenerator(
                EnvironmentSimulationContext.class,
                config,
                randomGenerator
        );

        assertSame(randomGenerator, context.getRandom());
    }

    @Test
    public void testGetAgent_ExistsInThisCore() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int populationSize = 20;
        int agentIndex = 8;
        Config config = TestFixtures.syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize);
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                config,
                agentSet
        );
        String agentName = agentSet.get(agentIndex).name();

        ImmutableAgent returnedImmutableAgent = context.getAgent(agentName);
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        Agent returnedAgent = (Agent) getMutableEntityMethod.invoke(returnedImmutableAgent);

        assertSame(agentSet.get(agentIndex), returnedAgent);
    }

    @Test
    public void testGetAgent_IsCached() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(20, 10, 1);
        ContextCache cache = TestFixtures.contextCache();
        Agent agent = TestFixtures.emptyAgent("Carol");
        cache.addAgent(agent);
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithCache(
                EnvironmentSimulationContext.class,
                config,
                cache
        );

        ImmutableAgent returnedImmutableAgent = context.getAgent(agent.name());
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        Agent returnedAgent = (Agent) getMutableEntityMethod.invoke(returnedImmutableAgent);

        assertSame(agent, returnedAgent);
    }

    @Test
    public void testGetAgent_ThreadsNotSynced_AgentNotFound() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.unsyncedConfig(20, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithEnvironment(
                EnvironmentSimulationContext.class,
                config,
                environment
        );

        String agentName = "John";

        AgentNotFoundException exception = assertThrows(
                AgentNotFoundException.class,
                () -> context.getAgent(agentName)
        );

        assertEquals(
                "Agent '" + agentName + "' requested by '" + environment.name() + "' not found in this thread (threads are not synced)",
                exception.getMessage()
        );
    }

    @Test
    public void testGetAgent_ThreadsSynced() throws InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Config config = TestFixtures.syncedConfig(20, 10, 1);
        Agent agent = TestFixtures.emptyAgent("Carol");
        RequestResponseInterface mockRequestResponseInterface = mock(RequestResponseInterface.class);
        doReturn(agent).when(mockRequestResponseInterface).getAgentFromCoordinator(any(), any());
        EnvironmentSimulationContext context = TestFixtures.emptySimulationContext(
                EnvironmentSimulationContext.class,
                config
        );
        Field requestResponseInterfaceField = SimulationContext.class.getDeclaredField(
                "requestResponseInterface"
        );
        requestResponseInterfaceField.setAccessible(true);
        requestResponseInterfaceField.set(context, mockRequestResponseInterface);

        ImmutableAgent returnedImmutableAgent = context.getAgent(agent.name());
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        Agent returnedAgent = (Agent) getMutableEntityMethod.invoke(returnedImmutableAgent);

        assertSame(agent, returnedAgent);
    }

    private Object anyFor(Class<?> parameterType) {
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

    private <E extends Throwable> EnvironmentSimulationContext generateContextWhereRequestResponseInterfaceMethodThrows(
            Class<E> exceptionClass,
            String methodName,
            Class<?>[] methodParameterTypes,
            Config config,
            Environment environment
    ) throws IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Method method = RequestResponseInterface.class.getDeclaredMethod(
                methodName,
                methodParameterTypes
        );
        RequestResponseInterface mockRequestResponseInterface = mock(RequestResponseInterface.class);
        RequestResponseInterface stubbing = doThrow(mock(exceptionClass)).when(mockRequestResponseInterface);

        Object[] args = new Object[methodParameterTypes.length];
        for (int i = 0; i < args.length; i++)
            args[i] = anyFor(methodParameterTypes[i]);
        method.invoke(stubbing, args);

        EnvironmentSimulationContext context = TestFixtures.simulationContextWithEnvironment(
                EnvironmentSimulationContext.class,
                config,
                environment
        );

        Field requestResponseInterfaceField = SimulationContext.class.getDeclaredField(
                "requestResponseInterface"
        );
        requestResponseInterfaceField.setAccessible(true);
        requestResponseInterfaceField.set(context, mockRequestResponseInterface);

        return context;
    }

    @Test
    public void testGetAgent_ThreadsSynced_SimulationInterruptedException() throws NoSuchFieldException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                InterruptedException.class,
                "getAgentFromCoordinator",
                new Class<?>[]{String.class, String.class},
                config,
                environment
        );

        SimulationInterruptedException exception = assertThrows(
                SimulationInterruptedException.class,
                () -> context.getAgent(requestedAgentName)
        );

        assertEquals(
                "Interrupted while fetching agent '" + requestedAgentName + "'",
                exception.getMessage()
        );

        assertInstanceOf(InterruptedException.class, exception.getCause());
    }

    @Test
    public void testGetAgent_ThreadsSynced_AgentNotFoundException_CoordinatorTimeoutException() throws NoSuchFieldException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                CoordinatorTimeoutException.class,
                "getAgentFromCoordinator",
                new Class<?>[]{String.class, String.class},
                config,
                environment
        );

        AgentNotFoundException exception = assertThrows(
                AgentNotFoundException.class,
                () -> context.getAgent(requestedAgentName)
        );

        assertEquals(
                "Agent '" + requestedAgentName + "' requested by '" + environment.name() + "' not found",
                exception.getMessage()
        );

        assertInstanceOf(CoordinatorTimeoutException.class, exception.getCause());
    }

    @Test
    public void testGetAgent_ThreadsSynced_AgentNotFoundException_CoordinatorErrorException() throws NoSuchFieldException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                CoordinatorErrorException.class,
                "getAgentFromCoordinator",
                new Class<?>[]{String.class, String.class},
                config,
                environment
        );

        AgentNotFoundException exception = assertThrows(
                AgentNotFoundException.class,
                () -> context.getAgent(requestedAgentName)
        );

        assertEquals(
                "Agent '" + requestedAgentName + "' requested by '" + environment.name() + "' not found",
                exception.getMessage()
        );

        assertInstanceOf(CoordinatorErrorException.class, exception.getCause());
    }

    private AgentSet getMutableAgentSetFromImmutable(ImmutableAgentSet immutableAgentSet) throws NoSuchFieldException, IllegalAccessException {
        Field agentSetField = ImmutableAgentSet.class.getDeclaredField("agentSet");
        agentSetField.setAccessible(true);
        return (AgentSet) agentSetField.get(immutableAgentSet);
    }

    @Test
    public void testGetFilteredAgents_isCached() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        int populationSize = 7;
        Config config = TestFixtures.syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize);
        ContextCache cache = TestFixtures.contextCache();
        Predicate<Agent> filter = a -> true;
        cache.addFilteredAgents(filter, agentSet);
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithCache(
                EnvironmentSimulationContext.class,
                config,
                cache
        );

        ImmutableAgentSet filteredAgentSet = context.getFilteredAgents(filter);

        assertSame(agentSet, getMutableAgentSetFromImmutable(filteredAgentSet));
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced() throws InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        int populationSize = 7;
        Config config = TestFixtures.syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize);
        RequestResponseInterface mockRequestResponseInterface = mock(RequestResponseInterface.class);
        doReturn(agentSet).when(mockRequestResponseInterface).getFilteredAgentsFromCoordinator(any(), any());
        Predicate<Agent> filter = a -> true;
        EnvironmentSimulationContext context = TestFixtures.emptySimulationContext(
                EnvironmentSimulationContext.class,
                config
        );
        Field requestResponseInterfaceField = SimulationContext.class.getDeclaredField(
                "requestResponseInterface"
        );
        requestResponseInterfaceField.setAccessible(true);
        requestResponseInterfaceField.set(context, mockRequestResponseInterface);

        ImmutableAgentSet filteredAgentSet = context.getFilteredAgents(filter);

        assertSame(agentSet, getMutableAgentSetFromImmutable(filteredAgentSet));
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_SimulationInterruptedException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();
        Predicate<Agent> filter = a -> true;

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                InterruptedException.class,
                "getFilteredAgentsFromCoordinator",
                new Class<?>[]{String.class, Predicate.class},
                config,
                environment
        );

        SimulationInterruptedException exception = assertThrows(
                SimulationInterruptedException.class,
                () -> context.getFilteredAgents(filter)
        );

        assertEquals(
                "Interrupted while retrieving filtered agents requested by " + "'" + environment.name() + "'",
                exception.getMessage()
        );

        assertInstanceOf(InterruptedException.class, exception.getCause());
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_AgentNotFoundException_CoordinatorTimeoutException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();
        Predicate<Agent> filter = a -> true;

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                CoordinatorTimeoutException.class,
                "getFilteredAgentsFromCoordinator",
                new Class<?>[]{String.class, Predicate.class},
                config,
                environment
        );

        AgentNotFoundException exception = assertThrows(
                AgentNotFoundException.class,
                () -> context.getFilteredAgents(filter)
        );

        assertEquals(
                "Failed to retrieve filtered agents requested by '" + environment.name() + "' from the coordinator",
                exception.getMessage()
        );

        assertInstanceOf(CoordinatorTimeoutException.class, exception.getCause());
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_AgentNotFoundException_CoordinatorErrorException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();
        Predicate<Agent> filter = a -> true;

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                CoordinatorErrorException.class,
                "getFilteredAgentsFromCoordinator",
                new Class<?>[]{String.class, Predicate.class},
                config,
                environment
        );

        AgentNotFoundException exception = assertThrows(
                AgentNotFoundException.class,
                () -> context.getFilteredAgents(filter)
        );

        assertEquals(
                "Failed to retrieve filtered agents requested by '" + environment.name() + "' from the coordinator",
                exception.getMessage()
        );

        assertInstanceOf(CoordinatorErrorException.class, exception.getCause());
    }
}
