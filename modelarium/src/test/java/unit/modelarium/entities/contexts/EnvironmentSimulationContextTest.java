package unit.modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.entities.Agent;
import modelarium.entities.Environment;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.attributes.AttributeBase;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.readonly.ReadOnlyAgent;
import modelarium.exceptions.AgentNotFoundException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.SplittableRandom;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.contexts.ContextTestHelpers.*;

public class EnvironmentSimulationContextTest {
    @Test
    public void testGetLocalAgentSet_ReturnsLocalAgentSet() throws Exception {
        AgentSet agentSet = agentSetOfSize(3);
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                syncedConfig(3, 10, 1),
                agentSet
        );

        assertSame(agentSet, context.getLocalAgentSet());
    }

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
        EnvironmentAttributeSet set = singlePropertyEnvironmentSet("owner", "time", "ticks");
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
        Clock clock = mutableClockFromConfig(config);
        EnvironmentSimulationContext context = simulationContextWithClock(
                EnvironmentSimulationContext.class,
                config,
                clock
        );

        assertEquals(clock.currentTick(), context.getClock().currentTick());
        assertEquals(clock.totalTickCount(), context.getClock().totalTickCount());
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
    public void testGetAgent() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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
    public void testGetCurrentPopulationSize_ReturnsLocalAgentSetSize() throws Exception {
        AgentSet agentSet = agentSetOfSize(4);
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                syncedConfig(4, 10, 1),
                agentSet
        );

        assertEquals(4, context.getCurrentPopulationSize());
    }

    @Test
    public void testGetAgent_MissingAgent_ThrowsAgentNotFoundException() throws Exception {
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                syncedConfig(1, 10, 1),
                agentSet(emptyAgent("present"))
        );

        AgentNotFoundException exception = assertThrows(
                AgentNotFoundException.class,
                () -> context.getAgent("missing")
        );

        assertTrue(exception.getMessage().contains("missing"));
    }

    @Test
    public void testGetFilteredAgents() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
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

    @Test
    public void testGetFilteredAgents_IncludeDeadAgentsTrue_IncludesDeadAgentsAndRemainsLocalWhenSynced() throws Exception {
        Config config = syncedConfig(2, 10, 1);
        Agent alive = emptyAgent("alive");
        Agent dead = emptyAgent("dead");
        dead.kill();
        AgentSet agentSet = new AgentSet(List.of(alive, dead));
        EnvironmentContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                config,
                agentSet
        );

        ReadOnlyAgentSet result = context.getFilteredAgents(agent -> true, true);

        assertEquals(2, result.size());
        assertFalse(result.get("alive").isDead());
        assertTrue(result.get("dead").isDead());
    }

    @Test
    public void testKillAgent_ByName_KillsLocalAgent() throws Exception {
        Agent target = emptyAgent("target");
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                syncedConfig(1, 10, 1),
                agentSet(target)
        );

        context.killAgent("target");

        assertTrue(target.isDead());
    }

    @Test
    public void testKillAgent_ByReadOnlyAgent_KillsLocalAgent() throws Exception {
        Agent target = emptyAgent("target");
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                syncedConfig(1, 10, 1),
                agentSet(target)
        );

        context.killAgent(target.getAsImmutable());

        assertTrue(target.isDead());
    }

    @Test
    public void testKillAgent_MissingAgent_ThrowsWithoutKillingExistingAgent() throws Exception {
        Agent existing = emptyAgent("existing");
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                syncedConfig(1, 10, 1),
                agentSet(existing)
        );

        assertThrows(AgentNotFoundException.class, () -> context.killAgent("missing"));

        assertFalse(existing.isDead());
    }

    @Test
    public void testKillAgents_ByNames_KillsEveryNamedAgent() throws Exception {
        Agent first = emptyAgent("first");
        Agent second = emptyAgent("second");
        Agent untouched = emptyAgent("untouched");
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                syncedConfig(3, 10, 1),
                agentSet(first, second, untouched)
        );

        context.killAgents(List.of("first", "second"));

        assertTrue(first.isDead());
        assertTrue(second.isDead());
        assertFalse(untouched.isDead());
    }

    @Test
    public void testKillAgents_MissingAgent_IsAtomic() throws Exception {
        Agent first = emptyAgent("first");
        Agent second = emptyAgent("second");
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                syncedConfig(2, 10, 1),
                agentSet(first, second)
        );

        assertThrows(
                AgentNotFoundException.class,
                () -> context.killAgents(List.of("first", "missing", "second"))
        );

        assertFalse(first.isDead(), "No agent should be killed when validation of the complete request fails.");
        assertFalse(second.isDead(), "No agent should be killed when validation of the complete request fails.");
    }

    @Test
    public void testKillAgents_ByReadOnlySet_KillsEveryAgentInSet() throws Exception {
        Agent first = emptyAgent("first");
        Agent second = emptyAgent("second");
        Agent untouched = emptyAgent("untouched");
        AgentSet localAgents = agentSet(first, second, untouched);
        EnvironmentSimulationContext context = simulationContextWithAgentSet(
                EnvironmentSimulationContext.class,
                syncedConfig(3, 10, 1),
                localAgents
        );
        ReadOnlyAgentSet agentsToKill = agentSet(first, second).getAsImmutable();

        context.killAgents(agentsToKill);

        assertTrue(first.isDead());
        assertTrue(second.isDead());
        assertFalse(untouched.isDead());
    }
}
