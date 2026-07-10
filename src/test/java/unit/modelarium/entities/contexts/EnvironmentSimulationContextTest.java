package unit.modelarium.entities.contexts;

import helpers.TestAttributes;
import helpers.TestFixtures;
import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableAgentSet;
import modelarium.exceptions.AgentNotFoundException;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;
import modelarium.exceptions.SimulationInterruptedException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.SplittableRandom;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.contexts.ContextTestHelpers.*;

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

        assertCorrectExceptionThrown(
                UnsupportedOperationException.class,
                context::getEnvironment,
                "Context requester is already an Environment - use 'getThisEntity()' instead"
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

        Agent returnedAgent = getMutableAgentFromImmutable(context.getAgent(agentName));

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

        Agent returnedAgent = getMutableAgentFromImmutable(context.getAgent(agent.name()));

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

        assertCorrectExceptionThrown(
                AgentNotFoundException.class,
                () -> context.getAgent(agentName),
                "Agent '" + agentName + "' requested by '" + environment.name() + "' not found in this thread (threads are not synced)"
        );
    }

    @Test
    public void testGetAgent_ThreadsSynced() throws InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Config config = TestFixtures.syncedConfig(20, 10, 1);
        Agent agent = TestFixtures.emptyAgent("Carol");

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodReturns(
                EnvironmentSimulationContext.class,
                agent,
                "getAgentFromCoordinator",
                new Class[]{String.class, String.class},
                config,
                TestFixtures.emptyEnvironment()
        );

        Agent returnedAgent = getMutableAgentFromImmutable(context.getAgent(agent.name()));

        assertSame(agent, returnedAgent);
    }

    @Test
    public void testGetAgent_ThreadsSynced_SimulationInterruptedException() throws NoSuchFieldException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                EnvironmentSimulationContext.class,
                InterruptedException.class,
                "getAgentFromCoordinator",
                new Class<?>[]{String.class, String.class},
                config,
                environment
        );

        assertCorrectExceptionThrown(
                SimulationInterruptedException.class,
                () -> context.getAgent(requestedAgentName),
                "Interrupted while fetching agent '" + requestedAgentName + "'",
                InterruptedException.class
        );
    }

    @Test
    public void testGetAgent_ThreadsSynced_AgentNotFoundException_CoordinatorTimeoutException() throws NoSuchFieldException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                EnvironmentSimulationContext.class,
                CoordinatorTimeoutException.class,
                "getAgentFromCoordinator",
                new Class<?>[]{String.class, String.class},
                config,
                environment
        );

        assertCorrectExceptionThrown(
                AgentNotFoundException.class,
                () -> context.getAgent(requestedAgentName),
                "Agent '" + requestedAgentName + "' requested by '" + environment.name() + "' not found",
                CoordinatorTimeoutException.class
        );
    }

    @Test
    public void testGetAgent_ThreadsSynced_AgentNotFoundException_CoordinatorErrorException() throws NoSuchFieldException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                EnvironmentSimulationContext.class,
                CoordinatorErrorException.class,
                "getAgentFromCoordinator",
                new Class<?>[]{String.class, String.class},
                config,
                environment
        );

        assertCorrectExceptionThrown(
                AgentNotFoundException.class,
                () -> context.getAgent(requestedAgentName),
                "Agent '" + requestedAgentName + "' requested by '" + environment.name() + "' not found",
                CoordinatorErrorException.class
        );
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
        Predicate<Agent> filter = a -> true;

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodReturns(
                EnvironmentSimulationContext.class,
                agentSet,
                "getFilteredAgentsFromCoordinator",
                new Class[]{String.class, Predicate.class},
                config,
                TestFixtures.emptyEnvironment()
        );

        assertSame(agentSet, getMutableAgentSetFromImmutable(context.getFilteredAgents(filter)));
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_SimulationInterruptedException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();
        Predicate<Agent> filter = a -> true;

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                EnvironmentSimulationContext.class,
                InterruptedException.class,
                "getFilteredAgentsFromCoordinator",
                new Class<?>[]{String.class, Predicate.class},
                config,
                environment
        );

        assertCorrectExceptionThrown(
                SimulationInterruptedException.class,
                () -> context.getFilteredAgents(filter),
                "Interrupted while retrieving filtered agents requested by " + "'" + environment.name() + "'",
                InterruptedException.class
        );
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_AgentNotFoundException_CoordinatorTimeoutException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();
        Predicate<Agent> filter = a -> true;

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                EnvironmentSimulationContext.class,
                CoordinatorTimeoutException.class,
                "getFilteredAgentsFromCoordinator",
                new Class<?>[]{String.class, Predicate.class},
                config,
                environment
        );

        assertCorrectExceptionThrown(
                AgentNotFoundException.class,
                () -> context.getFilteredAgents(filter),
                "Failed to retrieve filtered agents requested by '" + environment.name() + "' from the coordinator",
                CoordinatorTimeoutException.class
        );
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_AgentNotFoundException_CoordinatorErrorException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Environment environment = TestFixtures.emptyEnvironment();
        Predicate<Agent> filter = a -> true;

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                EnvironmentSimulationContext.class,
                CoordinatorErrorException.class,
                "getFilteredAgentsFromCoordinator",
                new Class<?>[]{String.class, Predicate.class},
                config,
                environment
        );

        assertCorrectExceptionThrown(
                AgentNotFoundException.class,
                () -> context.getFilteredAgents(filter),
                "Failed to retrieve filtered agents requested by '" + environment.name() + "' from the coordinator",
                CoordinatorErrorException.class
        );
    }
}
