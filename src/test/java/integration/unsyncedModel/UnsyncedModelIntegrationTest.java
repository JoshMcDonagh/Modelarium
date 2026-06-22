package integration.unsyncedModel;

import modelarium.Config;
import modelarium.Model;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.FunctionalEnvironmentGenerator;
import modelarium.results.immutable.ImmutableResults;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.List;

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
            protected Agent generateAgent(Config config) {
                String name = "agent_" + idx++;
                StepCounter counter = new StepCounter();
                AgentAttributeSet set = new AgentAttributeSet(name, "movement",
                        (List<Attribute<AgentSimulationContext>>) (List<?>) List.of(counter));
                return new Agent(name, List.of(set));
            }
        };

        Config config = Config.builder()
                .populationSize(population)
                .tickCount(ticks)
                .threadCount(threads)
                .areThreadsSynced(false)
                .agentGenerator(agentGen)
                .environmentGenerator(new FunctionalEnvironmentGenerator(c -> new Environment("env", List.of())))
                .scheduler(new InOrderScheduler())
                .build();

        Model model = new Model(config);
        model.run();

        ImmutableResults results = model.getResults();
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
            protected Agent generateAgent(Config config) {
                String name = "a_" + idx++;
                StepCounter c = new StepCounter();
                AgentAttributeSet set = new AgentAttributeSet(name, "s",
                        (List<Attribute<AgentSimulationContext>>) (List<?>) List.of(c));
                return new Agent(name, List.of(set));
            }
        };

        Config config = Config.builder()
                .populationSize(2)
                .tickCount(3)
                .threadCount(1)
                .areThreadsSynced(false)
                .agentGenerator(agentGen)
                .environmentGenerator(new FunctionalEnvironmentGenerator(c -> new Environment("env", List.of())))
                .build();

        Model model = new Model(config);
        assertDoesNotThrow(model::run, "Single-thread unsynced should complete without errors.");

        ImmutableResults results = model.getResults();
        List<Integer> log = results.agents().attributeLogs("a_0", "s", "steps", Integer.class);
        assertEquals(3, log.size());
    }
}
