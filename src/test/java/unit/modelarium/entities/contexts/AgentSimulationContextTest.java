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
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableAgentSet;
import modelarium.exceptions.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.SplittableRandom;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.contexts.ContextTestHelpers.*;

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
        Agent agent = TestFixtures.emptyAgent("James");
        Environment environment = TestFixtures.emptyEnvironment();

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodReturns(
                AgentSimulationContext.class,
                environment,
                "getEnvironmentFromCoordinator",
                new Class[]{String.class},
                config,
                agent
        );

        Environment returnedEnvironment = getMutableFromImmutable(context.getEnvironment());

        assertSame(environment, returnedEnvironment);
    }

    @Test
    public void testGetEnvironmentWithSyncedThreads_SimulationInterruptedException() throws NoSuchFieldException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        String thisAgentName = "Alice";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Agent agent = TestFixtures.emptyAgent(thisAgentName);

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                InterruptedException.class,
                "getEnvironmentFromCoordinator",
                new Class[]{String.class},
                config,
                agent
        );

        assertCorrectExceptionThrown(
                SimulationInterruptedException.class,
                context::getEnvironment,
                "Interrupted while fetching environment requested by '" + thisAgentName + "'",
                InterruptedException.class
        );
    }

    @Test
    public void testGetEnvironmentWithSyncedThreads_EnvironmentNotFoundException_CoordinatorTimeoutException() throws NoSuchFieldException, InterruptedException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String thisAgentName = "Alice";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Agent agent = TestFixtures.emptyAgent(thisAgentName);

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                CoordinatorTimeoutException.class,
                "getEnvironmentFromCoordinator",
                new Class[]{String.class},
                config,
                agent
        );

        assertCorrectExceptionThrown(
                EnvironmentNotFoundException.class,
                context::getEnvironment,
                "Environment requested by '" + thisAgentName + "' could not be found",
                CoordinatorTimeoutException.class
        );
    }

    @Test
    public void testGetEnvironmentWithSyncedThreads_EnvironmentNotFoundException_CoordinatorErrorException() throws NoSuchFieldException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String thisAgentName = "Alice";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Agent agent = TestFixtures.emptyAgent(thisAgentName);

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                CoordinatorErrorException.class,
                "getEnvironmentFromCoordinator",
                new Class[]{String.class},
                config,
                agent
        );

        assertCorrectExceptionThrown(
                EnvironmentNotFoundException.class,
                context::getEnvironment,
                "Environment requested by '" + thisAgentName + "' could not be found",
                CoordinatorErrorException.class
        );
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

    @Test
    public void testGetAgent_ExistsInThisCore() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int populationSize = 20;
        int agentIndex = 8;
        Config config = TestFixtures.syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize);
        AgentSimulationContext context = TestFixtures.simulationContextWithAgentSet(
                AgentSimulationContext.class,
                config,
                agentSet
        );
        String agentName = agentSet.get(agentIndex).name();

        Agent returnedAgent = getMutableFromImmutable(context.getAgent(agentName));

        assertSame(agentSet.get(agentIndex), returnedAgent);
    }

    @Test
    public void testGetAgent_IsCached() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(20, 10, 1);
        ContextCache cache = TestFixtures.contextCache();
        Agent agent = TestFixtures.emptyAgent("Carol");
        cache.addAgent(agent);
        AgentSimulationContext context = TestFixtures.simulationContextWithCache(
                AgentSimulationContext.class,
                config,
                cache
        );

        Agent returnedAgent = getMutableFromImmutable(context.getAgent(agent.name()));

        assertSame(agent, returnedAgent);
    }

    @Test
    public void testGetAgent_ThreadsNotSynced_AgentNotFound() {
        int populationSize = 20;
        Config config = TestFixtures.unsyncedConfig(populationSize, 10, 1);
        Agent thisAgent = TestFixtures.emptyAgent("Greg");
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize - 1);
        agentSet.add(thisAgent);
        AgentSimulationContext context = TestFixtures.agentSimulationContextWithAgent(
                config,
                thisAgent,
                agentSet
        );

        String agentName = "John";

        assertCorrectExceptionThrown(
                AgentNotFoundException.class,
                () -> context.getAgent(agentName),
                "Agent '" + agentName + "' requested by '" + thisAgent.name() + "' not found in this thread (threads are not synced)"
        );
    }

    @Test
    public void testGetAgent_ThreadsSynced() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Config config = TestFixtures.syncedConfig(20, 10, 1);
        Agent requestedAgent = TestFixtures.emptyAgent("Carol");
        Agent thisAgent = TestFixtures.emptyAgent("Greg");

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodReturns(
                AgentSimulationContext.class,
                requestedAgent,
                "getAgentFromCoordinator",
                new Class[]{String.class, String.class},
                config,
                thisAgent
        );

        Agent returnedAgent = getMutableFromImmutable(context.getAgent(requestedAgent.name()));

        assertSame(requestedAgent, returnedAgent);
    }

    @Test
    public void testGetAgent_ThreadsSynced_SimulationInterruptedException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Agent thisAgent = TestFixtures.emptyAgent("Greg");

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                InterruptedException.class,
                "getAgentFromCoordinator",
                new Class<?>[]{String.class, String.class},
                config,
                thisAgent
        );

        assertCorrectExceptionThrown(
                SimulationInterruptedException.class,
                () -> context.getAgent(requestedAgentName),
                "Interrupted while fetching agent '" + requestedAgentName + "'",
                InterruptedException.class
        );
    }

    @Test
    public void testGetAgent_ThreadsSynced_AgentNotFoundException_CoordinatorTimeoutException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Agent thisAgent = TestFixtures.emptyAgent("Greg");

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                CoordinatorTimeoutException.class,
                "getAgentFromCoordinator",
                new Class<?>[]{String.class, String.class},
                config,
                thisAgent
        );

        assertCorrectExceptionThrown(
                AgentNotFoundException.class,
                () -> context.getAgent(requestedAgentName),
                "Agent '" + requestedAgentName + "' requested by '" + thisAgent.name() + "' not found",
                CoordinatorTimeoutException.class
        );
    }

    @Test
    public void testGetAgent_ThreadsSynced_AgentNotFoundException_CoordinatorErrorException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Agent thisAgent = TestFixtures.emptyAgent("Greg");

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                CoordinatorErrorException.class,
                "getAgentFromCoordinator",
                new Class<?>[]{String.class, String.class},
                config,
                thisAgent
        );

        assertCorrectExceptionThrown(
                AgentNotFoundException.class,
                () -> context.getAgent(requestedAgentName),
                "Agent '" + requestedAgentName + "' requested by '" + thisAgent.name() + "' not found",
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
        AgentSimulationContext context = TestFixtures.simulationContextWithCache(
                AgentSimulationContext.class,
                config,
                cache
        );

        ImmutableAgentSet filteredAgentSet = context.getFilteredAgents(filter);

        assertSame(agentSet, getMutableFromImmutable(filteredAgentSet));
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        int populationSize = 7;
        Config config = TestFixtures.syncedConfig(populationSize, 10, 1);
        Agent thisAgent = TestFixtures.emptyAgent("Greg");
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize - 1);
        agentSet.add(thisAgent);
        Predicate<Agent> filter = a -> true;

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodReturns(
                AgentSimulationContext.class,
                agentSet,
                "getFilteredAgentsFromCoordinator",
                new Class[]{String.class, Predicate.class},
                config,
                thisAgent
        );

        assertSame(agentSet, getMutableFromImmutable(context.getFilteredAgents(filter)));
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_SimulationInterruptedException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Agent thisAgent = TestFixtures.emptyAgent("Greg");
        Predicate<Agent> filter = a -> true;

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                InterruptedException.class,
                "getFilteredAgentsFromCoordinator",
                new Class<?>[]{String.class, Predicate.class},
                config,
                thisAgent
        );

        assertCorrectExceptionThrown(
                SimulationInterruptedException.class,
                () -> context.getFilteredAgents(filter),
                "Interrupted while retrieving filtered agents requested by " + "'" + thisAgent.name() + "'",
                InterruptedException.class
        );
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_AgentNotFoundException_CoordinatorTimeoutException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Agent thisAgent = TestFixtures.emptyAgent("Greg");
        Predicate<Agent> filter = a -> true;

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                CoordinatorTimeoutException.class,
                "getFilteredAgentsFromCoordinator",
                new Class<?>[]{String.class, Predicate.class},
                config,
                thisAgent
        );

        assertCorrectExceptionThrown(
                AgentNotFoundException.class,
                () -> context.getFilteredAgents(filter),
                "Failed to retrieve filtered agents requested by '" + thisAgent.name() + "' from the coordinator",
                CoordinatorTimeoutException.class
        );
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_AgentNotFoundException_CoordinatorErrorException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Agent thisAgent = TestFixtures.emptyAgent("Greg");
        Predicate<Agent> filter = a -> true;

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                CoordinatorErrorException.class,
                "getFilteredAgentsFromCoordinator",
                new Class<?>[]{String.class, Predicate.class},
                config,
                thisAgent
        );

        assertCorrectExceptionThrown(
                AgentNotFoundException.class,
                () -> context.getFilteredAgents(filter),
                "Failed to retrieve filtered agents requested by '" + thisAgent.name() + "' from the coordinator",
                CoordinatorErrorException.class
        );
    }

    @Test
    public void testGetFilteredAgents_Threads_Unsynced() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        int populationSize = 20;
        Config config = TestFixtures.unsyncedConfig(populationSize, 10, 1);
        AgentSet agentSet = TestFixtures.agentSetOfSize(populationSize);
        Predicate<Agent> filter = a -> true;

        AgentSimulationContext context = TestFixtures.simulationContextWithAgentSet(
                AgentSimulationContext.class,
                config,
                agentSet
        );

        assertSetsContainSameAgents(agentSet, context.getFilteredAgents(filter));
    }
}
