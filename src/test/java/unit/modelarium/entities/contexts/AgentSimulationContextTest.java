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
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AgentSimulationContextTest {
    @Test
    public void testGetThisEntity() {
        Agent agent = TestFixtures.emptyAgent("Alice");
        Agent otherAgent = TestFixtures.emptyAgent("Bob");
        AgentSet agentSet = TestFixtures.agentSet(agent, otherAgent);
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithAgent(agent, agentSet, config);

        assertSame(agent, context.getThisEntity());
    }

    @Test
    public void testGetThisAttributeSet() {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        AgentAttributeSet set = TestAttributes.singlePropertyAgentSet("owner", "food", "hunger");
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithAttributeSet(config, set);

        assertSame(set, context.getThisAttributeSet());
    }

    @Test
    public void testGetThisAttribute() {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Attribute<?> attribute = new TestAttributes.CounterProperty("a");
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithAttribute(config, attribute);

        assertSame(attribute, context.getThisAttribute());
    }

    @Test
    public void testGetEnvironmentWithUnsyncedThreads() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Config config = TestFixtures.unsyncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithEnvironment(config, environment);

        ImmutableEnvironment returnedImmutableEnvironment = context.getEnvironment();
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        Environment returnedEnvironment = (Environment) getMutableEntityMethod.invoke(returnedImmutableEnvironment);

        assertSame(environment, returnedEnvironment);
    }

    @Test
    public void testGetEnvironmentWithSyncedThreadsIsCached() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        ContextCache cache = TestFixtures.contextCache();
        Environment environment = TestFixtures.emptyEnvironment();
        cache.addEnvironment(environment);
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithCache(config, cache);

        ImmutableEnvironment returnedImmutableEnvironment = context.getEnvironment();
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        Environment returnedEnvironment = (Environment) getMutableEntityMethod.invoke(returnedImmutableEnvironment);

        assertSame(environment, returnedEnvironment);
    }

    @Test
    public void testGetEnvironmentWithSyncedThreadsIsNotCached() throws InterruptedException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();
        RequestResponseInterface mockRequestResponseInterface = mock(RequestResponseInterface.class);
        doReturn(environment).when(mockRequestResponseInterface).getEnvironmentFromCoordinator(any());
        AgentSimulationContext context = TestFixtures.emptyAgentSimulationContext(config);
        Field requestResponseInterfaceField = SimulationContext.class.getDeclaredField(
                "requestResponseInterface"
        );
        requestResponseInterfaceField.setAccessible(true);
        requestResponseInterfaceField.set(context, mockRequestResponseInterface);

        ImmutableEnvironment returnedImmutableEnvironment = context.getEnvironment();
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        Environment returnedEnvironment = (Environment) getMutableEntityMethod.invoke(returnedImmutableEnvironment);

        assertSame(environment, returnedEnvironment);
    }

    @Test
    public void testGetEnvironmentSimulationInterruptedException() {
        fail("Not yet implemented...");
    }

    @Test
    public void testGetEnvironmentEnvironmentNotFoundException() {
        fail("Not yet implemented...");
    }

    @Test
    public void testGetClock() {
        Config config  = TestFixtures.syncedConfig(2, 10, 1);
        MutableClock clock = TestFixtures.mutableClockFromConfig(config);
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithClock(config, clock);

        assertSame(clock, context.getClock());
    }

    @Test
    public void testDoesAgentExistInThisCoreTrue() {
        int populationSize = 20;

        Config config  = TestFixtures.syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize);
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithAgent(agentSet.get(0), agentSet, config);

        String agentName = agentSet.get(12).name();

        assertTrue(context.doesAgentExistInThisCore(agentName));
    }

    @Test
    public void testDoesAgentExistInThisCoreFalse() {
        int populationSize = 20;

        Config config  = TestFixtures.syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize);
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithAgent(agentSet.get(0), agentSet, config);

        String agentName = "James";

        assertFalse(context.doesAgentExistInThisCore(agentName));
    }

    @Test
    public void testGetRandom() {
        Config config  = TestFixtures.syncedConfig(2, 10, 1);
        SplittableRandom randomGenerator = new SplittableRandom();
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithRandomGenerator(config, randomGenerator);

        assertSame(randomGenerator, context.getRandom());
    }
}
