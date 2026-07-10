package unit.modelarium.entities.contexts;

import helpers.TestAttributes;
import helpers.TestFixtures;
import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableEntity;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;
import modelarium.exceptions.EnvironmentNotFoundException;
import modelarium.exceptions.SimulationInterruptedException;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static unit.modelarium.entities.contexts.ContextTestHelpers.generateContextWhereRequestResponseInterfaceMethodReturns;
import static unit.modelarium.entities.contexts.ContextTestHelpers.getMutableFromImmutable;

public class AgentSimulationContextTest {
    @Test
    public void testGetThisEntity() {
        Agent agent = TestFixtures.emptyAgent("Alice");
        Agent otherAgent = TestFixtures.emptyAgent("Bob");
        AgentSet agentSet = TestFixtures.agentSet(agent, otherAgent);
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithAgent(
                config,
                agent,
                agentSet
        );

        assertSame(agent, context.getThisEntity());
    }

    @Test
    public void testGetThisAttributeSet() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        AgentAttributeSet set = TestAttributes.singlePropertyAgentSet("owner", "food", "hunger");
        AgentSimulationContext context = TestFixtures.simulationContextWithAttributeSet(
                AgentSimulationContext.class,
                config,
                set
        );

        assertSame(set, context.getThisAttributeSet());
    }

    @Test
    public void testGetThisAttribute() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Attribute<?> attribute = new TestAttributes.AgentCounterProperty("a");
        AgentSimulationContext context = TestFixtures.simulationContextWithAttribute(
                AgentSimulationContext.class,
                config,
                attribute
        );

        assertSame(attribute, context.getThisAttribute());
    }

    @Test
    public void testGetEnvironmentWithUnsyncedThreads() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Config config = TestFixtures.unsyncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();
        AgentSimulationContext context = TestFixtures.simulationContextWithEnvironment(
                AgentSimulationContext.class,
                config,
                environment
        );

        Environment returnedEnvironment = getMutableFromImmutable(context.getEnvironment());

        assertSame(environment, returnedEnvironment);
    }

    @Test
    public void testGetEnvironmentWithSyncedThreads_IsCached() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        ContextCache cache = TestFixtures.contextCache();
        Environment environment = TestFixtures.emptyEnvironment();
        cache.addEnvironment(environment);
        AgentSimulationContext context = TestFixtures.simulationContextWithCache(
                AgentSimulationContext.class,
                config,
                cache
        );

        Environment returnedEnvironment = getMutableFromImmutable(context.getEnvironment());

        assertSame(environment, returnedEnvironment);
    }

    @Test
    public void testGetEnvironmentWithSyncedThreads_IsNotCached() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodReturns(
                AgentSimulationContext.class,
                environment,
                "getEnvironmentFromCoordinator",
                new Class[]{String.class},
                config,
                environment
        );

        Environment returnedEnvironment = getMutableFromImmutable(context.getEnvironment());

        assertSame(environment, returnedEnvironment);
    }

    private <E extends Throwable> AgentSimulationContext generateContextWhereGetEnvironmentFromCoordinatorThrows(
            Class<E> exceptionClass,
            String thisAgentName,
            Config config
    ) throws IllegalAccessException, InterruptedException, NoSuchFieldException {
        Agent thisAgent = TestFixtures.emptyAgent(thisAgentName);
        Agent otherAgent = TestFixtures.emptyAgent("Not_" + thisAgentName);
        AgentSet agentSet = TestFixtures.agentSet(thisAgent, otherAgent);



        RequestResponseInterface mockRequestResponseInterface = mock(RequestResponseInterface.class);
        doThrow(mock(exceptionClass)).when(mockRequestResponseInterface).getEnvironmentFromCoordinator(any());

        AgentSimulationContext context = TestFixtures.agentSimulationContextWithAgent(
                config,
                thisAgent,
                agentSet
        );

        Field requestResponseInterfaceField = SimulationContext.class.getDeclaredField(
                "requestResponseInterface"
        );
        requestResponseInterfaceField.setAccessible(true);
        requestResponseInterfaceField.set(context, mockRequestResponseInterface);

        return context;
    }

    @Test
    public void testGetEnvironmentWithSyncedThreads_SimulationInterruptedException() throws InterruptedException, NoSuchFieldException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        String thisAgentName = "Alice";
        Config config = TestFixtures.syncedConfig(2, 10, 1);

        AgentSimulationContext context = generateContextWhereGetEnvironmentFromCoordinatorThrows(
                InterruptedException.class,
                thisAgentName,
                config
        );

        SimulationInterruptedException exception = assertThrows(
                SimulationInterruptedException.class,
                context::getEnvironment
        );

        assertEquals(
                "Interrupted while fetching environment requested by '" + thisAgentName + "'",
                exception.getMessage()
        );

        assertInstanceOf(InterruptedException.class, exception.getCause());
    }

    @Test
    public void testGetEnvironmentWithSyncedThreads_EnvironmentNotFoundException_CoordinatorTimeoutException() throws NoSuchFieldException, InterruptedException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String thisAgentName = "Alice";
        Config config = TestFixtures.syncedConfig(2, 10, 1);

        AgentSimulationContext context = generateContextWhereGetEnvironmentFromCoordinatorThrows(
                CoordinatorTimeoutException.class,
                thisAgentName,
                config
        );

        EnvironmentNotFoundException exception = assertThrows(
                EnvironmentNotFoundException.class,
                context::getEnvironment
        );

        assertEquals(
                "Environment requested by '" + thisAgentName + "' could not be found",
                exception.getMessage()
        );

        assertInstanceOf(CoordinatorTimeoutException.class, exception.getCause());
    }

    @Test
    public void testGetEnvironmentWithSyncedThreads_EnvironmentNotFoundException_CoordinatorErrorException() throws NoSuchFieldException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String thisAgentName = "Alice";
        Config config = TestFixtures.syncedConfig(2, 10, 1);

        AgentSimulationContext context = generateContextWhereGetEnvironmentFromCoordinatorThrows(
                CoordinatorErrorException.class,
                thisAgentName,
                config
        );

        EnvironmentNotFoundException exception = assertThrows(
                EnvironmentNotFoundException.class,
                context::getEnvironment
        );

        assertEquals(
                "Environment requested by '" + thisAgentName + "' could not be found",
                exception.getMessage()
        );

        assertInstanceOf(CoordinatorErrorException.class, exception.getCause());
    }

    @Test
    public void testGetClock() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config  = TestFixtures.syncedConfig(2, 10, 1);
        MutableClock clock = TestFixtures.mutableClockFromConfig(config);
        AgentSimulationContext context = TestFixtures.simulationContextWithClock(
                AgentSimulationContext.class,
                config,
                clock
        );

        assertSame(clock, context.getClock());
    }

    @Test
    public void testDoesAgentExistInThisCoreTrue() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int populationSize = 20;

        Config config  = TestFixtures.syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize);
        AgentSimulationContext context = TestFixtures.simulationContextWithAgentSet(
                AgentSimulationContext.class,
                config,
                agentSet
        );

        String agentName = agentSet.get(12).name();

        assertTrue(context.doesAgentExistInThisCore(agentName));
    }

    @Test
    public void testDoesAgentExistInThisCoreFalse() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int populationSize = 20;

        Config config  = TestFixtures.syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize);
        AgentSimulationContext context = TestFixtures.simulationContextWithAgentSet(
                AgentSimulationContext.class,
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
        AgentSimulationContext context = TestFixtures.simulationContextWithRandomGenerator(
                AgentSimulationContext.class,
                config,
                randomGenerator
        );

        assertSame(randomGenerator, context.getRandom());
    }
}
