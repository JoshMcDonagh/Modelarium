package unit.modelarium.entities.contexts;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.entities.readonly.ReadOnlyAgent;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.attributes.sets.AttributeBase;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.Environment;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
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
}
