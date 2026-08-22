package integration.unsyncedModel;

import modelarium.Config;
import modelarium.Model;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.generators.FunctionalEnvironmentGenerator;
import modelarium.results.immutable.ReadOnlyResults;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: runs a model in unsynced mode.
 *
 * <p>Workers use independent clocks and don't coordinate via the
 * request-response system. Agent results should still be collected
 * and merged correctly.
 */
public class UnsyncedModelIntegrationTest {

    // Increments by 1 each tick — simple deterministic property
    static class StepCounter extends AgentProperty<Integer> {
        private int count = 0;

        StepCounter() {
            super("steps", true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(AgentContext context) {
            count++;
        }

        @Override
        protected void set(AgentContext context, Integer v) {
            count = v;
        }

        @Override
        protected Integer get(AgentContext context) {
            return count;
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnsyncedModelProducesResults() {
        int population = 4;
        int ticks = 8;
        int threads = 2;

        DefaultAgentGenerator agentGen = new DefaultAgentGenerator() {
            private int idx = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                String name = "agent_" + idx++;
                StepCounter counter = new StepCounter();
                MutableAgentAttributeSet set = new MutableAgentAttributeSet("movement",
                        (List<Attribute>) (List<?>) List.of(counter));
                return new Agent(name, List.of(set));
            }
        };

        Config config = Config.builder()
                .populationSize(population)
                .tickCount(ticks)
                .threadCount(threads)
                .areThreadsSynced(false)
                .agentGenerator(agentGen)
                .environmentGenerator(new FunctionalEnvironmentGenerator((c, random) -> new Environment(List.of())))
                .scheduler(new InOrderScheduler())
                .build();

        Model model = new Model(config);
        model.run();

        ReadOnlyResults results = model.getResults();
        assertNotNull(results);

        // Each agent should have `ticks` logged values
        for (int i = 0; i < population; i++) {
            String agentName = "agent_" + i;
            List<Integer> steps = results.agents().attributeLogs(
                    agentName, "movement", "steps", Integer.class);

            assertEquals(ticks, steps.size(),
                    agentName + " should have exactly " + ticks + " step values.");

            // StepCounter increments: should be 1, 2, 3, ..., ticks
            for (int t = 0; t < ticks; t++) {
                assertEquals(t + 1, steps.get(t),
                        agentName + " step counter at tick " + t + " should be " + (t + 1));
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleThreadUnsynced() {
        // Edge case: 1 thread, unsynced
        DefaultAgentGenerator agentGen = new DefaultAgentGenerator() {
            private int idx = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                String name = "a_" + idx++;
                StepCounter c = new StepCounter();
                MutableAgentAttributeSet set = new MutableAgentAttributeSet("s",
                        (List<Attribute>) (List<?>) List.of(c));
                return new Agent(name, List.of(set));
            }
        };

        Config config = Config.builder()
                .populationSize(2)
                .tickCount(3)
                .threadCount(1)
                .areThreadsSynced(false)
                .agentGenerator(agentGen)
                .environmentGenerator(new FunctionalEnvironmentGenerator((c, random) -> new Environment(List.of())))
                .build();

        Model model = new Model(config);
        assertDoesNotThrow(model::run, "Single-thread unsynced should complete without errors.");

        ReadOnlyResults results = model.getResults();
        List<Integer> log = results.agents().attributeLogs("a_0", "s", "steps", Integer.class);
        assertEquals(3, log.size());
    }
}
