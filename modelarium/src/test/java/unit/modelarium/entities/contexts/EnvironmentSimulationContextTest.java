package unit.modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.immutable.ReadOnlyAgentSet;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.attributes.sets.mutable.AttributeBase;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.environments.Environment;
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
        Environment environment = emptyEnvironment();
        Config config = syncedConfig(2, 10, 1);
        EnvironmentSimulationContext context = simulationContextWithEnvironment(
                EnvironmentSimulationContext.class,
                config,
                environment
        );

        assertSame(environment, context.getThisEntity());
    }

    @Test
    public void testGetThisAttributeSet() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = syncedConfig(2, 10, 1);
        MutableEnvironmentAttributeSet set = singlePropertyEnvironmentSet("owner", "time", "ticks");
        EnvironmentSimulationContext context = simulationContextWithAttributeSet(
                EnvironmentSimulationContext.class,
                config,
                set
        );

        assertSame(set, context.getThisAttributeSet());
    }

    @Test
    public void testGetThisAttribute() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = syncedConfig(2, 10, 1);
        AttributeBase<?> attribute = new EnvironmentTickProperty("ticker");
        EnvironmentSimulationContext context = simulationContextWithAttribute(
                EnvironmentSimulationContext.class,
                config,
                attribute
        );

        assertSame(attribute, context.getThisAttribute());
    }

    @Test
    public void testGetEnvironment_UnsupportedOperationException() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = syncedConfig(2, 10, 1);
        EnvironmentSimulationContext context = emptySimulationContext(
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
        Config config = syncedConfig(2, 10, 1);
        MutableClock clock = mutableClockFromConfig(config);
        EnvironmentSimulationContext context = simulationContextWithClock(
                EnvironmentSimulationContext.class,
                config,
                clock
        );

        assertSame(clock, context.getClock());
    }

    @Test
    public void testDoesAgentExistInThisCoreTrue() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int populationSize = 20;

        Config config = syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = agentSetOfSize(populationSize);
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
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

        Config config = syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = agentSetOfSize(populationSize);
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                config,
                agentSet
        );

        String agentName = "James";

        assertFalse(context.doesAgentExistInThisCore(agentName));
    }

    @Test
    public void testGetRandom() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = syncedConfig(2, 10, 1);
        SplittableRandom randomGenerator = new SplittableRandom();
        EnvironmentSimulationContext context = simulationContextWithRandomGenerator(
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
        Config config = syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = agentSetOfSize(populationSize);
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                config,
                agentSet
        );
        String agentName = agentSet.get(agentIndex).name();

        Agent returnedAgent = getMutableFromImmutable(context.getAgent(agentName));

        assertSame(agentSet.get(agentIndex), returnedAgent);
    }

    @Test
    public void testGetAgent_IsCached() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = syncedConfig(20, 10, 1);
        ContextCache cache = contextCache();
        ReadOnlyAgent agent = new ReadOnlyAgent(emptyAgent("Carol"));
        cache.addAgent(agent);
        EnvironmentSimulationContext context = simulationContextWithCache(
                EnvironmentSimulationContext.class,
                config,
                cache
        );

        ReadOnlyAgent returnedAgent = context.getAgent(agent.name());

        assertSame(agent, returnedAgent);
    }

    @Test
    public void testGetAgent_ThreadsUnsynced_AgentNotFoundException() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = unsyncedConfig(20, 10, 1);
        Environment environment = emptyEnvironment();
        EnvironmentSimulationContext context = simulationContextWithEnvironment(
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
    public void testGetAgent_ThreadsSynced() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Config config = syncedConfig(20, 10, 1);
        ReadOnlyAgent agent = emptyAgent("Carol").getAsImmutable();

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodReturns(
                EnvironmentSimulationContext.class,
                agent,
                "getAgentFromCoordinator",
                new Class<?>[]{String.class, String.class},
                config,
                emptyEnvironment()
        );

        ReadOnlyAgent returnedAgent = context.getAgent(agent.name());

        assertSame(agent, returnedAgent);
    }

    @Test
    public void testGetAgent_ThreadsSynced_SimulationInterruptedException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = syncedConfig(2, 10, 1);
        Environment environment = emptyEnvironment();

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
    public void testGetAgent_ThreadsSynced_AgentNotFoundException_CoordinatorTimeoutException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = syncedConfig(2, 10, 1);
        Environment environment = emptyEnvironment();

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
    public void testGetAgent_ThreadsSynced_AgentNotFoundException_CoordinatorErrorException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = syncedConfig(2, 10, 1);
        Environment environment = emptyEnvironment();

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
    public void testGetFilteredAgents_IsCached() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        int populationSize = 7;
        Config config = syncedConfig(populationSize, 10, 1);
        ReadOnlyAgentSet agentSet = agentSetOfSize(populationSize).getAsImmutable();
        ContextCache cache = contextCache();
        Predicate<ReadOnlyAgent> filter = a -> true;
        cache.addFilteredAgents(filter, agentSet);
        EnvironmentSimulationContext context = simulationContextWithCache(
                EnvironmentSimulationContext.class,
                config,
                cache
        );

        ReadOnlyAgentSet filteredAgentSet = context.getFilteredAgents(filter);

        assertSame(agentSet, filteredAgentSet);
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        int populationSize = 7;
        Config config = syncedConfig(populationSize, 10, 1);
        ReadOnlyAgentSet agentSet = agentSetOfSize(populationSize).getAsImmutable();
        Predicate<ReadOnlyAgent> filter = a -> true;

        EnvironmentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodReturns(
                EnvironmentSimulationContext.class,
                agentSet,
                "getFilteredAgentsFromCoordinator",
                new Class<?>[]{String.class, Predicate.class},
                config,
                emptyEnvironment()
        );

        assertSame(agentSet, context.getFilteredAgents(filter));
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_SimulationInterruptedException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Config config = syncedConfig(2, 10, 1);
        Environment environment = emptyEnvironment();
        Predicate<ReadOnlyAgent> filter = a -> true;

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
        Config config = syncedConfig(2, 10, 1);
        Environment environment = emptyEnvironment();
        Predicate<ReadOnlyAgent> filter = a -> true;

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
        Config config = syncedConfig(2, 10, 1);
        Environment environment = emptyEnvironment();
        Predicate<ReadOnlyAgent> filter = a -> true;

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

    @Test
    public void testGetFilteredAgents_ThreadsUnsynced() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        int populationSize = 20;
        Config config = unsyncedConfig(populationSize, 10, 1);
        AgentSet agentSet = agentSetOfSize(populationSize);
        Predicate<ReadOnlyAgent> filter = a -> true;

        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                config,
                agentSet
        );

        assertSetsContainSameAgents(agentSet, context.getFilteredAgents(filter));
    }
}
