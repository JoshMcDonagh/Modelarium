package integration.syncedModel;

import com.rits.cloning.Cloner;
import modelarium.Config;
import modelarium.Model;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.*;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.EnvironmentGenerator;
import modelarium.entities.environments.FunctionalEnvironmentGenerator;
import modelarium.results.immutable.ImmutableResults;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: runs a complete model with synchronised threads and a
 * coordinator. Verifies that the tick loop executes, properties are logged,
 * and results are retrievable at the end.
 *
 * <p>Model: each agent has a "hunger" property that increments by 0.1 per tick,
 * saturating at 1.0. The environment has a tick counter.
 */
public class SyncedModelIntegrationTest {

    @BeforeAll
    static void openForCloning() {
        SyncedModelIntegrationTest.class.getModule().addOpens(
                "integration.syncedModel",
                Cloner.class.getModule()
        );
    }

    // ---- Attribute implementations local to this test ----

    static class Hunger extends AgentProperty<Double> {
        private double value = 0.2;

        Hunger() {
            super("hunger", true, AttributeAccessLevel.PUBLIC, Double.class);
        }

        @Override
        protected void run(AgentContext context) {
            value = Math.min(1.0, value + 0.1);
        }

        @Override
        protected void set(AgentContext context, Double v) {
            value = v;
        }

        @Override
        protected Double get(AgentContext context) {
            return value;
        }
    }

    static class EnvTick extends EnvironmentProperty<Integer> {
        private int tick = 0;

        EnvTick() {
            super("envTick", true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(EnvironmentContext context) {
            tick++;
        }

        @Override
        protected void set(EnvironmentContext context, Integer v) {
            tick = v;
        }

        @Override
        protected Integer get(EnvironmentContext context) {
            return tick;
        }
    }

    // ---- Test ----

    @Test
    @SuppressWarnings("unchecked")
    public void testSyncedModelRunsAndProducesResults() {
        int population = 6;
        int ticks = 10;
        int threads = 2;

        DefaultAgentGenerator agentGen = new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                String name = "agent_" + index++;
                Hunger hunger = new Hunger();
                AgentAttributeSet foodSet = new AgentAttributeSet("food",
                        (List<Attribute>) (List<?>) List.of(hunger));
                return new Agent(name, List.of(foodSet));
            }
        };

        EnvironmentGenerator envGen = new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config, RandomGenerator random) {
                EnvTick envTick = new EnvTick();
                EnvironmentAttributeSet timingSet = new EnvironmentAttributeSet("timing",
                        (List<Attribute>) (List<?>) List.of(envTick));
                return new Environment(List.of(timingSet));
            }
        };

        Config config = Config.builder()
                .populationSize(population)
                .tickCount(ticks)
                .threadCount(threads)
                .areThreadsSynced(true)
                .agentGenerator(agentGen)
                .environmentGenerator(envGen)
                .scheduler(new InOrderScheduler())
                .build();

        Model model = new Model(config);
        model.run();

        ImmutableResults results = model.getResults();
        assertNotNull(results, "Results should not be null after a run.");

        // Check agent results: each agent should have `ticks` logged hunger values
        List<Double> hungerLog = results.agents().attributeLogs(
                "agent_0", "food", "hunger", Double.class);

        assertEquals(ticks, hungerLog.size(),
                "Each agent should have exactly one hunger value logged per tick.");

        // Hunger should increase over time (starts at 0.2, increments 0.1/tick)
        assertTrue(hungerLog.get(0) > 0.0, "Initial hunger after first tick should be positive.");
        assertTrue(hungerLog.get(hungerLog.size() - 1) >= hungerLog.get(0),
                "Hunger should generally increase or saturate.");

        // Check environment results
        List<Integer> envTickLog = results.environment().attributeLogs(
                "timing", "envTick", Integer.class);

        assertEquals(ticks, envTickLog.size(),
                "Environment should have one tick value logged per tick.");

        // Environment tick counter should count up: 1, 2, 3, ...
        for (int i = 0; i < envTickLog.size(); i++) {
            assertEquals(i + 1, envTickLog.get(i),
                    "Environment tick counter should increment each tick.");
        }
    }

    @Test
    public void testResultsNotAccessibleBeforeRun() {
        Config config = Config.builder()
                .populationSize(1)
                .tickCount(1)
                .threadCount(1)
                .agentGenerator(new DefaultAgentGenerator() {
                    @Override
                    protected Agent generateAgent(Config c, RandomGenerator random) {
                        return new Agent("a", List.of());
                    }
                })
                .environmentGenerator(new FunctionalEnvironmentGenerator((c, random) -> new Environment(List.of())))
                .build();

        Model model = new Model(config);
        assertThrows(IllegalStateException.class, model::getResults,
                "Accessing results before run() should throw.");
    }
}
