package unit.modelarium.entities.contexts;

import com.rits.cloning.Cloner;
import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.immutable.ReadOnlyAgentSet;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.attributes.sets.mutable.AttributeBase;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.ReadOnlyEnvironment;
import modelarium.entities.environments.generators.EnvironmentGenerator;
import modelarium.exceptions.*;
import modelarium.multithreading.requestresponse.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.SplittableRandom;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static unit.modelarium.entities.contexts.ContextTestHelpers.*;

public class AgentSimulationContextTest {
    @BeforeAll
    static void openForCloning() {
        AgentSimulationContextTest.class.getModule().addOpens(
                "unit.modelarium.entities.contexts",
                Cloner.class.getModule()
        );
    }

    @Test
    public void testGetThisEntity() {
        Agent agent = emptyAgent("Alice");
        Agent otherAgent = emptyAgent("Bob");
        AgentSet agentSet = agentSet(agent, otherAgent);
        Config config = syncedConfig(2, 10, 1);
        AgentSimulationContext context = agentSimulationContextWithAgent(
                config,
                agent,
                agentSet
        );

        assertSame(agent, context.getThisEntity());
    }

    @Test
    public void testGetThisAttributeSet() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = syncedConfig(2, 10, 1);
        MutableAgentAttributeSet set = singlePropertyAgentSet("owner", "food", "hunger");
        AgentSimulationContext context = simulationContextWithAttributeSet(
                AgentSimulationContext.class,
                config,
                set
        );

        assertSame(set, context.getThisAttributeSet());
    }

    @Test
    public void testGetThisAttribute() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = syncedConfig(2, 10, 1);
        AttributeBase<?> attribute = new AgentCounterProperty("a");
        AgentSimulationContext context = simulationContextWithAttribute(
                AgentSimulationContext.class,
                config,
                attribute
        );

        assertSame(attribute, context.getThisAttribute());
    }

    @Test
    public void testGetEnvironment_ThreadsUnsynced() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Config config = unsyncedConfig(2, 10, 1);
        Environment environment = emptyEnvironment();
        AgentSimulationContext context = simulationContextWithEnvironment(
                AgentSimulationContext.class,
                config,
                environment
        );

        Environment returnedEnvironment = getMutableFromImmutable(context.getEnvironment());

        assertSame(environment, returnedEnvironment);
    }

    @Test
    public void testGetEnvironment_ThreadsSynced_IsCached() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Config config = syncedConfig(2, 10, 1);
        ContextCache cache = contextCache();
        ReadOnlyEnvironment environment = emptyEnvironment().getAsImmutable();
        cache.addEnvironment(environment);
        AgentSimulationContext context = simulationContextWithCache(
                AgentSimulationContext.class,
                config,
                cache
        );

        ReadOnlyEnvironment returnedEnvironment = context.getEnvironment();

        assertSame(environment, returnedEnvironment);
    }

    @Test
    public void testGetEnvironment_ThreadsSynced_IsNotCached() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Config config = syncedConfig(2, 10, 1);
        Agent agent = emptyAgent("James");
        ReadOnlyEnvironment environment = emptyEnvironment().getAsImmutable();

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodReturns(
                AgentSimulationContext.class,
                environment,
                "getEnvironmentFromCoordinator",
                new Class<?>[]{String.class},
                config,
                agent
        );

        ReadOnlyEnvironment returnedEnvironment = context.getEnvironment();

        assertSame(environment, returnedEnvironment);
    }

    @Test
    public void testGetEnvironment_ThreadsSynced_SimulationInterruptedException() throws NoSuchFieldException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        String thisAgentName = "Alice";
        Config config = syncedConfig(2, 10, 1);
        Agent agent = emptyAgent(thisAgentName);

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                InterruptedException.class,
                "getEnvironmentFromCoordinator",
                new Class<?>[]{String.class},
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
    public void testGetEnvironment_ThreadsSynced_EnvironmentNotFoundException_CoordinatorTimeoutException() throws NoSuchFieldException, InterruptedException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String thisAgentName = "Alice";
        Config config = syncedConfig(2, 10, 1);
        Agent agent = emptyAgent(thisAgentName);

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                CoordinatorTimeoutException.class,
                "getEnvironmentFromCoordinator",
                new Class<?>[]{String.class},
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
    public void testGetEnvironment_ThreadsSynced_EnvironmentNotFoundException_CoordinatorErrorException() throws NoSuchFieldException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String thisAgentName = "Alice";
        Config config = syncedConfig(2, 10, 1);
        Agent agent = emptyAgent(thisAgentName);

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                CoordinatorErrorException.class,
                "getEnvironmentFromCoordinator",
                new Class<?>[]{String.class},
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
        Config config = syncedConfig(2, 10, 1);
        MutableClock clock = mutableClockFromConfig(config);
        AgentSimulationContext context = simulationContextWithClock(
                AgentSimulationContext.class,
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
        AgentSimulationContext context = simulationContextWithAgentSet(
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

        Config config = syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = agentSetOfSize(populationSize);
        AgentSimulationContext context = simulationContextWithAgentSet(
                AgentSimulationContext.class,
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
        AgentSimulationContext context = simulationContextWithRandomGenerator(
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
        Config config = syncedConfig(populationSize, 10, 1);
        AgentSet agentSet = agentSetOfSize(populationSize);
        AgentSimulationContext context = simulationContextWithAgentSet(
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
        Config config = syncedConfig(20, 10, 1);
        ContextCache cache = contextCache();
        ReadOnlyAgent agent = emptyAgent("Carol").getAsImmutable();
        cache.addAgent(agent);
        AgentSimulationContext context = simulationContextWithCache(
                AgentSimulationContext.class,
                config,
                cache
        );

        ReadOnlyAgent returnedAgent = context.getAgent(agent.name());

        assertSame(agent, returnedAgent);
    }

    @Test
    public void testGetAgent_ThreadsUnsynced_AgentNotFoundException() {
        int populationSize = 20;
        Config config = unsyncedConfig(populationSize, 10, 1);
        Agent thisAgent = emptyAgent("Greg");
        AgentSet agentSet = agentSetOfSize(populationSize - 1);
        agentSet.add(thisAgent);
        AgentSimulationContext context = agentSimulationContextWithAgent(
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
        Config config = syncedConfig(20, 10, 1);
        ReadOnlyAgent requestedAgent = emptyAgent("Carol").getAsImmutable();
        Agent thisAgent = emptyAgent("Greg");

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodReturns(
                AgentSimulationContext.class,
                requestedAgent,
                "getAgentFromCoordinator",
                new Class<?>[]{String.class, String.class},
                config,
                thisAgent
        );

        ReadOnlyAgent returnedAgent = context.getAgent(requestedAgent.name());

        assertSame(requestedAgent, returnedAgent);
    }

    @Test
    public void testGetAgent_ThreadsSynced_SimulationInterruptedException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String requestedAgentName = "John";
        Config config = syncedConfig(2, 10, 1);
        Agent thisAgent = emptyAgent("Greg");

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
        Config config = syncedConfig(2, 10, 1);
        Agent thisAgent = emptyAgent("Greg");

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
        Config config = syncedConfig(2, 10, 1);
        Agent thisAgent = emptyAgent("Greg");

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
    public void testGetFilteredAgents_Default_UsesLivingOnlyFilteredCache() throws Exception {
        int populationSize = 7;
        Config config = syncedConfig(populationSize, 10, 1);
        ReadOnlyAgentSet cachedResult = agentSetOfSize(populationSize).getAsImmutable();
        ContextCache cache = contextCache();
        Predicate<ReadOnlyAgent> filter = agent -> true;
        cache.addLivingOnlyFilteredAgents(filter, cachedResult);
        AgentSimulationContext context = simulationContextWithCache(
                AgentSimulationContext.class,
                config,
                cache
        );

        ReadOnlyAgentSet filteredAgentSet = context.getFilteredAgents(filter);

        assertSame(cachedResult, filteredAgentSet);
    }

    @Test
    public void testGetFilteredAgents_IncludeDeadAgents_UsesAllAgentsFilteredCache() throws Exception {
        int populationSize = 7;
        Config config = syncedConfig(populationSize, 10, 1);
        ReadOnlyAgentSet cachedResult = agentSetOfSize(populationSize).getAsImmutable();
        ContextCache cache = contextCache();
        Predicate<ReadOnlyAgent> filter = agent -> true;
        cache.addFilteredAgents(filter, cachedResult);
        AgentSimulationContext context = simulationContextWithCache(
                AgentSimulationContext.class,
                config,
                cache
        );

        ReadOnlyAgentSet filteredAgentSet = context.getFilteredAgents(filter, true);

        assertSame(cachedResult, filteredAgentSet);
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_RequestsGlobalSetAndFiltersLocally() throws Exception {
        Config config = syncedConfig(3, 10, 1);
        Agent keep = emptyAgent("keep");
        Agent drop = emptyAgent("drop");
        Agent deadKeep = emptyAgent("keep_dead");
        deadKeep.kill();
        ReadOnlyAgentSet globalAgentSet = new AgentSet(List.of(keep, drop, deadKeep)).getAsImmutable();
        ContextCache cache = contextCache();
        AgentSimulationContext context = simulationContextWithCache(AgentSimulationContext.class, config, cache);
        RequestResponseInterface requestResponseInterface = mock(RequestResponseInterface.class);
        when(requestResponseInterface.getGlobalAgentSetFromCoordinator(anyString())).thenReturn(globalAgentSet);
        setRequestResponseInterface(context, requestResponseInterface);
        Predicate<ReadOnlyAgent> filter = agent -> agent.name().startsWith("keep");

        ReadOnlyAgentSet result = context.getFilteredAgents(filter);

        assertEquals(1, result.size());
        assertEquals("keep", result.get(0).name());
        assertSame(globalAgentSet, cache.getGlobalAgentSet());
        assertTrue(cache.doesLivingOnlyAgentFilterExist(filter));
        assertFalse(cache.doesAgentFilterExist(filter));
        verify(requestResponseInterface, times(1)).getGlobalAgentSetFromCoordinator(anyString());
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_IncludeDeadAgentsTrue_IncludesMatchingDeadAgents() throws Exception {
        Config config = syncedConfig(2, 10, 1);
        Agent alive = emptyAgent("match_alive");
        Agent dead = emptyAgent("match_dead");
        dead.kill();
        ReadOnlyAgentSet globalAgentSet = new AgentSet(List.of(alive, dead)).getAsImmutable();
        ContextCache cache = contextCache();
        AgentSimulationContext context = simulationContextWithCache(AgentSimulationContext.class, config, cache);
        RequestResponseInterface requestResponseInterface = mock(RequestResponseInterface.class);
        when(requestResponseInterface.getGlobalAgentSetFromCoordinator(anyString())).thenReturn(globalAgentSet);
        setRequestResponseInterface(context, requestResponseInterface);
        Predicate<ReadOnlyAgent> filter = agent -> agent.name().startsWith("match");

        ReadOnlyAgentSet result = context.getFilteredAgents(filter, true);

        assertEquals(2, result.size());
        assertFalse(result.get("match_alive").isDead());
        assertTrue(result.get("match_dead").isDead());
        assertTrue(cache.doesAgentFilterExist(filter));
        assertFalse(cache.doesLivingOnlyAgentFilterExist(filter));
        verify(requestResponseInterface, times(1)).getGlobalAgentSetFromCoordinator(anyString());
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_ReusesGlobalAgentSetForDifferentFilters() throws Exception {
        Config config = syncedConfig(3, 10, 1);
        Agent first = emptyAgent("first");
        Agent second = emptyAgent("second");
        Agent dead = emptyAgent("dead");
        dead.kill();
        ReadOnlyAgentSet globalAgentSet = new AgentSet(List.of(first, second, dead)).getAsImmutable();
        ContextCache cache = contextCache();
        AgentSimulationContext context = simulationContextWithCache(AgentSimulationContext.class, config, cache);
        RequestResponseInterface requestResponseInterface = mock(RequestResponseInterface.class);
        when(requestResponseInterface.getGlobalAgentSetFromCoordinator(anyString())).thenReturn(globalAgentSet);
        setRequestResponseInterface(context, requestResponseInterface);

        ReadOnlyAgentSet firstResult = context.getFilteredAgents(agent -> agent.name().equals("first"), true);
        ReadOnlyAgentSet secondResult = context.getFilteredAgents(agent -> agent.name().equals("second"));

        assertEquals(1, firstResult.size());
        assertEquals("first", firstResult.get(0).name());
        assertEquals(1, secondResult.size());
        assertEquals("second", secondResult.get(0).name());
        assertTrue(cache.doesGlobalAgentSetExist());
        verify(requestResponseInterface, times(1)).getGlobalAgentSetFromCoordinator(anyString());
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_SamePredicateCachesDeadAndLivingResultsSeparately() throws Exception {
        Config config = syncedConfig(2, 10, 1);
        Agent alive = emptyAgent("alive");
        Agent dead = emptyAgent("dead");
        dead.kill();
        ReadOnlyAgentSet globalAgentSet = new AgentSet(List.of(alive, dead)).getAsImmutable();
        ContextCache cache = contextCache();
        AgentSimulationContext context = simulationContextWithCache(AgentSimulationContext.class, config, cache);
        RequestResponseInterface requestResponseInterface = mock(RequestResponseInterface.class);
        when(requestResponseInterface.getGlobalAgentSetFromCoordinator(anyString())).thenReturn(globalAgentSet);
        setRequestResponseInterface(context, requestResponseInterface);
        Predicate<ReadOnlyAgent> filter = agent -> true;

        ReadOnlyAgentSet livingOnly = context.getFilteredAgents(filter);
        ReadOnlyAgentSet includingDead = context.getFilteredAgents(filter, true);

        assertEquals(1, livingOnly.size());
        assertEquals(2, includingDead.size());
        assertSame(livingOnly, cache.getLivingOnlyFilteredAgents(filter));
        assertSame(includingDead, cache.getFilteredAgents(filter));
        verify(requestResponseInterface, times(1)).getGlobalAgentSetFromCoordinator(anyString());
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_SimulationInterruptedException() throws Exception {
        Config config = syncedConfig(2, 10, 1);
        Agent thisAgent = emptyAgent("Greg");
        Predicate<ReadOnlyAgent> filter = agent -> true;

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                InterruptedException.class,
                "getGlobalAgentSetFromCoordinator",
                new Class<?>[]{String.class},
                config,
                thisAgent
        );

        assertCorrectExceptionThrown(
                SimulationInterruptedException.class,
                () -> context.getFilteredAgents(filter),
                "Interrupted while retrieving filtered agents requested by '" + thisAgent.name() + "'",
                InterruptedException.class
        );
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_AgentNotFoundException_CoordinatorTimeoutException() throws Exception {
        Config config = syncedConfig(2, 10, 1);
        Agent thisAgent = emptyAgent("Greg");
        Predicate<ReadOnlyAgent> filter = agent -> true;

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                CoordinatorTimeoutException.class,
                "getGlobalAgentSetFromCoordinator",
                new Class<?>[]{String.class},
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
    public void testGetFilteredAgents_ThreadsSynced_AgentNotFoundException_CoordinatorErrorException() throws Exception {
        Config config = syncedConfig(2, 10, 1);
        Agent thisAgent = emptyAgent("Greg");
        Predicate<ReadOnlyAgent> filter = agent -> true;

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodThrows(
                AgentSimulationContext.class,
                CoordinatorErrorException.class,
                "getGlobalAgentSetFromCoordinator",
                new Class<?>[]{String.class},
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
    public void testGetFilteredAgents_ThreadsUnsynced_DefaultExcludesDeadAgents() throws Exception {
        Config config = unsyncedConfig(2, 10, 1);
        Agent alive = emptyAgent("alive");
        Agent dead = emptyAgent("dead");
        dead.kill();
        AgentSet localAgentSet = new AgentSet(List.of(alive, dead));
        AgentSimulationContext context = simulationContextWithAgentSet(
                AgentSimulationContext.class,
                config,
                localAgentSet
        );

        ReadOnlyAgentSet result = context.getFilteredAgents(agent -> true);

        assertEquals(1, result.size());
        assertEquals("alive", result.get(0).name());
    }

    @Test
    public void testGetFilteredAgents_ThreadsUnsynced_IncludeDeadAgentsTrueIncludesDeadAgents() throws Exception {
        Config config = unsyncedConfig(2, 10, 1);
        Agent alive = emptyAgent("alive");
        Agent dead = emptyAgent("dead");
        dead.kill();
        AgentSet localAgentSet = new AgentSet(List.of(alive, dead));
        AgentSimulationContext context = simulationContextWithAgentSet(
                AgentSimulationContext.class,
                config,
                localAgentSet
        );

        ReadOnlyAgentSet result = context.getFilteredAgents(agent -> true, true);

        assertEquals(2, result.size());
        assertFalse(result.get("alive").isDead());
        assertTrue(result.get("dead").isDead());
    }


    @Test
    public void testAddAgent() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(false, 1);

        AgentSimulationContext context =
                context(
                        config,
                        self,
                        localAgents,
                        new RequestResponseController(config)
                );

        Agent newAgent = emptyAgent("new");

        context.addAgent(newAgent);

        // It must not be visible during the current tick.
        assertFalse(
                context.doesAgentExistInThisCore("new")
        );

        // It should instead be queued for WorkerThread.
        assertTrue(
                context.getAddedAgents()
                        .doesAgentExist("new")
        );

        assertSame(
                newAgent,
                context.getAddedAgents().get("new")
        );
    }

    @Test
    public void testAddAgents_WithList() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(false, 1);

        AgentSimulationContext context =
                context(
                        config,
                        self,
                        localAgents,
                        new RequestResponseController(config)
                );

        Agent agent1 = emptyAgent("agent_1");
        Agent agent2 = emptyAgent("agent_2");

        context.addAgents(
                List.of(agent1, agent2)
        );

        // Additions are not visible until the tick boundary.
        assertFalse(
                context.doesAgentExistInThisCore("agent_1")
        );
        assertFalse(
                context.doesAgentExistInThisCore("agent_2")
        );

        AgentSet addedAgents =
                context.getAddedAgents();

        assertTrue(
                addedAgents.doesAgentExist("agent_1")
        );
        assertTrue(
                addedAgents.doesAgentExist("agent_2")
        );

        assertSame(
                agent1,
                addedAgents.get("agent_1")
        );
        assertSame(
                agent2,
                addedAgents.get("agent_2")
        );
    }

    @Test
    public void testAddAgents_WithAgentSet() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(false, 1);

        AgentSimulationContext context =
                context(
                        config,
                        self,
                        localAgents,
                        new RequestResponseController(config)
                );

        Agent agent1 = emptyAgent("agent_1");
        Agent agent2 = emptyAgent("agent_2");

        AgentSet agentsToAdd =
                agentSet(agent1, agent2);

        context.addAgents(agentsToAdd);

        // Still invisible during this tick.
        assertFalse(
                context.doesAgentExistInThisCore("agent_1")
        );
        assertFalse(
                context.doesAgentExistInThisCore("agent_2")
        );

        AgentSet queuedAgents =
                context.getAddedAgents();

        assertTrue(
                queuedAgents.doesAgentExist("agent_1")
        );
        assertTrue(
                queuedAgents.doesAgentExist("agent_2")
        );
    }

    private static Config config(boolean synced, int populationSize) {
        return Config.builder()
                .populationSize(populationSize)
                .tickCount(5)
                .threadCount(synced ? 2 : 1)
                .areThreadsSynced(synced)
                .agentGenerator(new DefaultAgentGenerator() {
                    @Override
                    protected Agent generateAgent(Config config, RandomGenerator random) {
                        return emptyAgent("generated");
                    }
                })
                .environmentGenerator(new EnvironmentGenerator() {
                    @Override
                    public Environment generateEnvironment(Config config, RandomGenerator random) {
                        return new Environment("environment", List.of());
                    }
                })
                .build();
    }

    private static AgentSimulationContext context(
            Config config,
            Agent self,
            AgentSet localAgents,
            RequestResponseController controller
    ) {
        return new AgentSimulationContext(
                self,
                localAgents,
                config,
                new ContextCache(),
                new MutableClock(config.tickCount()),
                controller,
                new Environment("environment", List.of()),
                new SplittableRandom()
        );
    }

    @Test
    public void testKillAgent_ThreadsUnsynced_QueuesLocalKill() {
        Agent self = emptyAgent("self");
        Agent target = emptyAgent("target");

        AgentSet localAgents =
                agentSet(self, target);

        Config config = config(false, 2);

        AgentSimulationContext context =
                context(
                        config,
                        self,
                        localAgents,
                        new RequestResponseController(config)
                );

        context.killAgent("target");

        // The agent remains alive for the current tick.
        assertFalse(target.isDead());

        // The worker will apply this at the tick boundary.
        assertEquals(
                List.of("target"),
                context.getKilledAgentNames()
        );
    }

    @Test
    public void testKillAgent_ReadOnlyAgent_QueuesWrappedAgentsName() {
        Agent self = emptyAgent("self");
        Agent target = emptyAgent("target");
        AgentSet localAgents = agentSet(self, target);
        Config config = config(false, 2);

        AgentSimulationContext context =
                context(
                        config,
                        self,
                        localAgents,
                        new RequestResponseController(config)
                );

        context.killAgent(target.getAsImmutable());

        // The agent should still appear alive during this tick.
        assertFalse(target.isDead());

        // But its name should have been queued for killing at the tick boundary.
        assertEquals(
                List.of("target"),
                context.getKilledAgentNames()
        );
    }

    @Test
    public void testKillAgent_ThreadsUnsynced_MissingAgent_AgentNotFoundException() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(false, 1);
        AgentSimulationContext context = context(config, self, localAgents, new RequestResponseController(config));

        assertThrows(AgentNotFoundException.class, () -> context.killAgent("missing"));
        assertFalse(self.isDead());
    }

    @Test
    public void testKillAgents_ThreadsUnsynced_QueuesAllNamedAgents() {
        Agent self = emptyAgent("self");
        Agent target1 = emptyAgent("target_1");
        Agent target2 = emptyAgent("target_2");

        AgentSet localAgents =
                agentSet(self, target1, target2);

        Config config = config(false, 3);

        AgentSimulationContext context =
                context(
                        config,
                        self,
                        localAgents,
                        new RequestResponseController(config)
                );

        context.killAgents(
                List.of("target_1", "target_2")
        );

        // Neither should change during this tick.
        assertFalse(target1.isDead());
        assertFalse(target2.isDead());

        assertEquals(
                List.of("target_1", "target_2"),
                context.getKilledAgentNames()
        );
    }

    @Test
    public void testKillAgents_ThreadsUnsynced_ValidatesWholeListBeforeKillingAnything() {
        Agent self = emptyAgent("self");
        Agent first = emptyAgent("first");
        AgentSet localAgents = agentSet(self, first);
        Config config = config(false, 2);
        AgentSimulationContext context = context(config, self, localAgents, new RequestResponseController(config));

        assertThrows(
                AgentNotFoundException.class,
                () -> context.killAgents(List.of("first", "missing"))
        );

        assertFalse(first.isDead());
    }

    @Test
    public void testKillAgents_ReadOnlyAgentSet_QueuesAllAgentsInSet() {
        Agent self = emptyAgent("self");
        Agent first = emptyAgent("first");
        Agent second = emptyAgent("second");

        AgentSet localAgents =
                agentSet(self, first, second);

        ReadOnlyAgentSet targets =
                new AgentSet(List.of(first, second))
                        .getAsImmutable();

        Config config = config(false, 3);

        AgentSimulationContext context =
                context(
                        config,
                        self,
                        localAgents,
                        new RequestResponseController(config)
                );

        context.killAgents(targets);

        // Deaths are deferred until the tick boundary.
        assertFalse(first.isDead());
        assertFalse(second.isDead());

        // The ReadOnlyAgentSet overload should have extracted
        // and queued both names.
        assertEquals(
                List.of("first", "second"),
                context.getKilledAgentNames()
        );
    }

    @Test
    public void testKillAgents_ThreadsSynced_QueuesKillsAfterRemoteAgentValidation() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);

        Config config = config(true, 3);

        RequestResponseController controller =
                successfulAgentAccessController(config);

        AgentSimulationContext context =
                context(
                        config,
                        self,
                        localAgents,
                        controller
                );

        List<String> names =
                List.of("remote_0", "remote_1");

        context.killAgents(names);

        assertEquals(
                names,
                context.getKilledAgentNames()
        );
    }

    private static RequestResponseController successfulAgentAccessController(
            Config config
    ) {
        return new RequestResponseController(config) {
            @Override
            public RequestResponseInterface getInterface(String name) {
                return new RequestResponseInterface(
                        name,
                        config,
                        this
                ) {
                    @Override
                    public ReadOnlyAgent getAgentFromCoordinator(
                            String requesterEntityName,
                            String targetAgentName
                    ) {
                        return emptyAgent(
                                targetAgentName
                        ).getAsImmutable();
                    }
                };
            }
        };
    }

    @Test
    public void testKillAgent_ThreadsSynced_QueuesKillAfterRemoteAgentValidation() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 2);

        RequestResponseController controller =
                successfulAgentAccessController(config);

        AgentSimulationContext context =
                context(config, self, localAgents, controller);

        context.killAgent("remote");

        assertEquals(
                List.of("remote"),
                context.getKilledAgentNames()
        );

        // killAgent() itself must not send a KILL_AGENT request.
        assertTrue(controller.getRequestQueue().isEmpty());
    }

    private static void throwFailure(Throwable failure) throws InterruptedException {
        if (failure == null)
            return;
        if (failure instanceof InterruptedException interruptedException)
            throw interruptedException;
        if (failure instanceof RuntimeException runtimeException)
            throw runtimeException;
        throw new RuntimeException(failure);
    }

    private static RequestResponseController failingAgentAccessController(
            Config config,
            Throwable failure
    ) {
        return new RequestResponseController(config) {
            @Override
            public RequestResponseInterface getInterface(String name) {
                return new RequestResponseInterface(
                        name,
                        config,
                        this
                ) {
                    @Override
                    public ReadOnlyAgent getAgentFromCoordinator(
                            String requesterEntityName,
                            String targetAgentName
                    ) throws InterruptedException {
                        throwFailure(failure);
                        return null;
                    }
                };
            }
        };
    }

    @Test
    public void testKillAgent_ThreadsSynced_Interrupted_ThrowsSimulationInterruptedExceptionAndRestoresInterrupt() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 2);

        RequestResponseController controller =
                failingAgentAccessController(
                        config,
                        new InterruptedException("interrupted")
                );

        AgentSimulationContext context =
                context(config, self, localAgents, controller);

        try {
            SimulationInterruptedException exception = assertThrows(
                    SimulationInterruptedException.class,
                    () -> context.killAgent("remote")
            );

            assertInstanceOf(
                    InterruptedException.class,
                    exception.getCause()
            );

            assertTrue(Thread.currentThread().isInterrupted());

            // Validation failed, so nothing should have been queued.
            assertFalse(
                    context.getKilledAgentNames().contains("remote")
            );
        } finally {
            // Don't leave the JUnit thread interrupted.
            Thread.interrupted();
        }
    }

    @Test
    public void testKillAgent_ThreadsSynced_CoordinatorTimeout_WrappedAsAgentNotFoundException() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 2);

        CoordinatorTimeoutException cause =
                new CoordinatorTimeoutException("timeout");

        RequestResponseController controller =
                failingAgentAccessController(config, cause);

        AgentSimulationContext context =
                context(config, self, localAgents, controller);

        AgentNotFoundException exception = assertThrows(
                AgentNotFoundException.class,
                () -> context.killAgent("remote")
        );

        assertSame(cause, exception.getCause());

        // The failed validation must not queue a kill.
        assertFalse(
                context.getKilledAgentNames().contains("remote")
        );
    }

    @Test
    public void testKillAgent_ThreadsSynced_CoordinatorError_WrappedAsAgentNotFoundException() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 2);

        CoordinatorErrorException cause =
                new CoordinatorErrorException(
                        "error",
                        new IllegalStateException()
                );

        RequestResponseController controller =
                failingAgentAccessController(config, cause);

        AgentSimulationContext context =
                context(config, self, localAgents, controller);

        AgentNotFoundException exception = assertThrows(
                AgentNotFoundException.class,
                () -> context.killAgent("remote")
        );

        assertSame(cause, exception.getCause());

        // Again, validation failed before the kill was queued.
        assertFalse(
                context.getKilledAgentNames().contains("remote")
        );
    }

    @Test
    public void testKillAgents_ThreadsSynced_Interrupted_ThrowsSimulationInterruptedExceptionAndRestoresInterrupt() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 2);

        RequestResponseController controller =
                failingAgentAccessController(
                        config,
                        new InterruptedException("interrupted")
                );

        AgentSimulationContext context =
                context(config, self, localAgents, controller);

        try {
            SimulationInterruptedException exception = assertThrows(
                    SimulationInterruptedException.class,
                    () -> context.killAgents(List.of("remote"))
            );

            assertInstanceOf(
                    InterruptedException.class,
                    exception.getCause()
            );

            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    public void testKillAgents_ThreadsSynced_CoordinatorError_WrappedAsAgentNotFoundException() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 2);

        CoordinatorErrorException cause =
                new CoordinatorErrorException(
                        "error",
                        new IllegalStateException()
                );

        RequestResponseController controller =
                failingAgentAccessController(config, cause);

        AgentSimulationContext context =
                context(config, self, localAgents, controller);

        AgentNotFoundException exception = assertThrows(
                AgentNotFoundException.class,
                () -> context.killAgents(List.of("remote"))
        );

        assertSame(cause, exception.getCause());
    }

    @Test
    public void testGetAgent_ThreadsSynced_RemoteDeadAgent_AgentIsDeadException() throws InterruptedException {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 2);
        RequestResponseController controller = new RequestResponseController(config);
        AgentSimulationContext context = context(config, self, localAgents, controller);
        Agent remote = emptyAgent("remote");
        remote.kill();

        controller.getResponseQueue("self").put(new Response(
                "coordinator",
                "self",
                ResponseType.AGENT_ACCESS,
                remote.getAsImmutable()
        ));

        AgentIsDeadException exception = assertThrows(
                AgentIsDeadException.class,
                () -> context.getAgent("remote")
        );

        assertTrue(exception.getMessage().contains("Agent 'remote'"));
        assertEquals(RequestType.AGENT_ACCESS, controller.getRequestQueue().take().getRequestType());
    }
}
