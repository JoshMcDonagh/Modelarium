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
    public void testGetFilteredAgents_IsCached() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        int populationSize = 7;
        Config config = syncedConfig(populationSize, 10, 1);
        ReadOnlyAgentSet agentSet = agentSetOfSize(populationSize).getAsImmutable();
        ContextCache cache = contextCache();
        Predicate<ReadOnlyAgent> filter = a -> true;
        cache.addFilteredAgents(filter, agentSet);
        AgentSimulationContext context = simulationContextWithCache(
                AgentSimulationContext.class,
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
        Agent thisAgent = emptyAgent("Greg");
        AgentSet agentSet = agentSetOfSize(populationSize - 1);
        agentSet.add(thisAgent);
        Predicate<ReadOnlyAgent> filter = a -> true;

        AgentSimulationContext context = generateContextWhereRequestResponseInterfaceMethodReturns(
                AgentSimulationContext.class,
                agentSet.getAsImmutable(),
                "getFilteredAgentsFromCoordinator",
                new Class<?>[]{String.class, Predicate.class},
                config,
                thisAgent
        );

        assertSame(agentSet.getAsImmutable(), context.getFilteredAgents(filter));
    }

    @Test
    public void testGetFilteredAgents_ThreadsSynced_SimulationInterruptedException() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Config config = syncedConfig(2, 10, 1);
        Agent thisAgent = emptyAgent("Greg");
        Predicate<ReadOnlyAgent> filter = a -> true;

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
        Config config = syncedConfig(2, 10, 1);
        Agent thisAgent = emptyAgent("Greg");
        Predicate<ReadOnlyAgent> filter = a -> true;

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
        Config config = syncedConfig(2, 10, 1);
        Agent thisAgent = emptyAgent("Greg");
        Predicate<ReadOnlyAgent> filter = a -> true;

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
    public void testGetFilteredAgents_ThreadsUnsynced() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        int populationSize = 20;
        Config config = unsyncedConfig(populationSize, 10, 1);
        AgentSet agentSet = agentSetOfSize(populationSize);
        Predicate<ReadOnlyAgent> filter = a -> true;

        AgentSimulationContext context = simulationContextWithAgentSet(
                AgentSimulationContext.class,
                config,
                agentSet
        );

        assertSetsContainSameAgents(agentSet, context.getFilteredAgents(filter));
    }


    @Test
    public void testAddAgent() throws ReflectiveOperationException {
        Config config = unsyncedConfig(1, 1, 1);
        AgentSet agentSet = agentSet(emptyAgent("agent_0"));

        AgentSimulationContext context = simulationContextWithAgentSet(
                AgentSimulationContext.class,
                config,
                agentSet
        );
        Agent newAgent = emptyAgent("agent_1");

        context.addAgent(newAgent);

        assertTrue(context.doesAgentExistInThisCore("agent_1"));
        assertSame(newAgent, getMutableFromImmutable(context.getAgent("agent_1")));
    }

    @Test
    public void testAddAgents_WithList() throws ReflectiveOperationException {
        Config config = unsyncedConfig(1, 1, 1);
        AgentSet agentSet = agentSet(emptyAgent("agent_0"));

        AgentSimulationContext context = simulationContextWithAgentSet(
                AgentSimulationContext.class,
                config,
                agentSet
        );

        context.addAgents(List.of(emptyAgent("agent_1"), emptyAgent("agent_2")));

        assertTrue(context.doesAgentExistInThisCore("agent_1"));
        assertTrue(context.doesAgentExistInThisCore("agent_2"));
    }

    @Test
    public void testAddAgents_WithAgentSet() throws ReflectiveOperationException {
        Config config = unsyncedConfig(1, 1, 1);
        AgentSet agentSet = agentSet(emptyAgent("agent_0"));

        AgentSimulationContext context = simulationContextWithAgentSet(
                AgentSimulationContext.class,
                config,
                agentSet
        );

        context.addAgents(agentSet(emptyAgent("agent_1"), emptyAgent("agent_2")));

        assertTrue(context.doesAgentExistInThisCore("agent_1"));
        assertTrue(context.doesAgentExistInThisCore("agent_2"));
    }

    @Test
    public void testAddAgentDeepCopy() throws ReflectiveOperationException {
        Config config = unsyncedConfig(1, 1, 1);
        AgentSet agentSet = agentSet(emptyAgent("agent_0"));

        AgentSimulationContext context = simulationContextWithAgentSet(
                AgentSimulationContext.class,
                config,
                agentSet
        );

        Agent newAgent = new Agent("agent_1", List.of(singlePropertyAgentSet("agent_1", "food", "hunger")));
        context.addAgentDeepCopy(newAgent);

        assertTrue(context.doesAgentExistInThisCore("agent_1"));
        Agent stored = getMutableFromImmutable(context.getAgent("agent_1"));
        assertNotSame(newAgent, stored);
        assertEquals("agent_1", stored.name());
    }

    @Test
    public void testAddAgentsDeepCopy_WithList() throws ReflectiveOperationException {
        AgentSimulationContext context = simulationContextWithAgentSet(
                AgentSimulationContext.class, unsyncedConfig(1, 1, 1), agentSet(emptyAgent("agent_0")));
        Agent newAgent = emptyAgent("agent_1");

        context.addAgentsDeepCopy(List.of(newAgent));

        assertTrue(context.doesAgentExistInThisCore("agent_1"));
        assertNotSame(newAgent, getMutableFromImmutable(context.getAgent("agent_1")));
    }

    @Test
    public void testAddAgentsDeepCopy_WithAgentSet() throws ReflectiveOperationException {
        AgentSimulationContext context = simulationContextWithAgentSet(
                AgentSimulationContext.class, unsyncedConfig(1, 1, 1), agentSet(emptyAgent("agent_0")));
        Agent newAgent = emptyAgent("agent_1");

        context.addAgentsDeepCopy(agentSet(newAgent));

        assertTrue(context.doesAgentExistInThisCore("agent_1"));
        assertNotSame(newAgent, getMutableFromImmutable(context.getAgent("agent_1")));
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
    public void testKillAgent_ThreadsUnsynced_KillsLocalAgent() {
        Agent self = emptyAgent("self");
        Agent target = emptyAgent("target");
        AgentSet localAgents = agentSet(self, target);
        Config config = config(false, 2);
        AgentSimulationContext context = context(config, self, localAgents, new RequestResponseController(config));

        context.killAgent("target");

        assertTrue(target.isDead());
    }

    @Test
    public void testKillAgent_ReadOnlyAgent_UsesWrappedAgentsName() {
        Agent self = emptyAgent("self");
        Agent target = emptyAgent("target");
        AgentSet localAgents = agentSet(self, target);
        Config config = config(false, 2);
        AgentSimulationContext context = context(config, self, localAgents, new RequestResponseController(config));

        context.killAgent(target.getAsImmutable());

        assertTrue(target.isDead());
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
    public void testKillAgents_ThreadsUnsynced_KillsAllNamedAgents() {
        Agent self = emptyAgent("self");
        Agent first = emptyAgent("first");
        Agent second = emptyAgent("second");
        AgentSet localAgents = agentSet(self, first, second);
        Config config = config(false, 3);
        AgentSimulationContext context = context(config, self, localAgents, new RequestResponseController(config));

        context.killAgents(List.of("first", "second"));

        assertTrue(first.isDead());
        assertTrue(second.isDead());
        assertFalse(self.isDead());
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
    public void testKillAgents_ReadOnlyAgentSet_KillsAllAgentsInSet() {
        Agent self = emptyAgent("self");
        Agent first = emptyAgent("first");
        Agent second = emptyAgent("second");
        AgentSet localAgents = agentSet(self, first, second);
        ReadOnlyAgentSet targets = new AgentSet(List.of(first, second)).getAsImmutable();
        Config config = config(false, 3);
        AgentSimulationContext context = context(config, self, localAgents, new RequestResponseController(config));

        context.killAgents(targets);

        assertTrue(first.isDead());
        assertTrue(second.isDead());
    }

    @Test
    public void testKillAgent_ThreadsSynced_QueuesCoordinatorRequest() throws InterruptedException {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 2);
        RequestResponseController controller = new RequestResponseController(config);
        AgentSimulationContext context = context(config, self, localAgents, controller);

        context.killAgent("remote");

        Request request = controller.getRequestQueue().take();
        assertEquals("self", request.getRequester());
        assertEquals(RequestType.KILL_AGENT, request.getRequestType());
        assertEquals("remote", request.getPayload());
    }

    @Test
    public void testKillAgents_ThreadsSynced_QueuesCoordinatorRequest() throws InterruptedException {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 3);
        RequestResponseController controller = new RequestResponseController(config);
        AgentSimulationContext context = context(config, self, localAgents, controller);
        List<String> names = List.of("remote_0", "remote_1");

        context.killAgents(names);

        Request request = controller.getRequestQueue().take();
        assertEquals(RequestType.KILL_AGENTS, request.getRequestType());
        assertSame(names, request.getPayload());
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

    private static RequestResponseController failingController(
            Config config,
            Throwable killOneFailure,
            Throwable killManyFailure
    ) {
        return new RequestResponseController(config) {
            @Override
            public RequestResponseInterface getInterface(String name) {
                return new RequestResponseInterface(name, config, this) {
                    @Override
                    public void killCoordinatorAgent(String agentName) throws InterruptedException {
                        throwFailure(killOneFailure);
                    }

                    @Override
                    public void killCoordinatorAgents(List<String> agentNames) throws InterruptedException {
                        throwFailure(killManyFailure);
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
        RequestResponseController controller = failingController(config, new InterruptedException("interrupted"), null);
        AgentSimulationContext context = context(config, self, localAgents, controller);

        try {
            SimulationInterruptedException exception = assertThrows(
                    SimulationInterruptedException.class,
                    () -> context.killAgent("remote")
            );

            assertInstanceOf(InterruptedException.class, exception.getCause());
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    public void testKillAgent_ThreadsSynced_CoordinatorTimeout_WrappedAsAgentNotFoundException() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 2);
        CoordinatorTimeoutException cause = new CoordinatorTimeoutException("timeout");
        RequestResponseController controller = failingController(config, cause, null);
        AgentSimulationContext context = context(config, self, localAgents, controller);

        AgentNotFoundException exception = assertThrows(
                AgentNotFoundException.class,
                () -> context.killAgent("remote")
        );

        assertSame(cause, exception.getCause());
    }

    @Test
    public void testKillAgent_ThreadsSynced_CoordinatorError_WrappedAsAgentNotFoundException() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 2);
        CoordinatorErrorException cause = new CoordinatorErrorException("error", new IllegalStateException());
        RequestResponseController controller = failingController(config, cause, null);
        AgentSimulationContext context = context(config, self, localAgents, controller);

        AgentNotFoundException exception = assertThrows(
                AgentNotFoundException.class,
                () -> context.killAgent("remote")
        );

        assertSame(cause, exception.getCause());
    }

    @Test
    public void testKillAgents_ThreadsSynced_Interrupted_ThrowsSimulationInterruptedExceptionAndRestoresInterrupt() {
        Agent self = emptyAgent("self");
        AgentSet localAgents = agentSet(self);
        Config config = config(true, 2);
        RequestResponseController controller = failingController(config, null, new InterruptedException("interrupted"));
        AgentSimulationContext context = context(config, self, localAgents, controller);

        try {
            SimulationInterruptedException exception = assertThrows(
                    SimulationInterruptedException.class,
                    () -> context.killAgents(List.of("remote"))
            );

            assertInstanceOf(InterruptedException.class, exception.getCause());
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
        CoordinatorErrorException cause = new CoordinatorErrorException("error", new IllegalStateException());
        RequestResponseController controller = failingController(config, null, cause);
        AgentSimulationContext context = context(config, self, localAgents, controller);

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
