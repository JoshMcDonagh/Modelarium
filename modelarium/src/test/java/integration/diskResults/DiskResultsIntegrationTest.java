package integration.diskResults;

import com.rits.cloning.Cloner;
import modelarium.Config;
import modelarium.Model;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.sets.mutable.AgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.EnvironmentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.generators.EnvironmentGenerator;
import modelarium.entities.logging.databases.factories.DiskBasedAttributeSetLogDatabaseFactory;
import modelarium.results.immutable.ReadOnlyResults;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test: runs a complete synchronised model whose results are written
 * to the disk-backed (SQLite) log database rather than the in-memory default.
 * It confirms that values of several types (integers, booleans, doubles) survive
 * the JSON serialisation and SQLite round-trip, and that an uneven population
 * split across workers still yields one complete log per agent.
 *
 * <p>Seven agents across two workers split 4/3 round-robin, so this also checks
 * that a remainder in the population distribution is handled cleanly.
 */
public class DiskResultsIntegrationTest {

    @BeforeAll
    static void openForCloning() {
        DiskResultsIntegrationTest.class.getModule().addOpens(
                "integration.diskResults",
                Cloner.class.getModule()
        );
    }

    static class StepCounter extends AgentProperty<Integer> {
        private int value = 0;

        StepCounter() {
            super("steps", true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(AgentContext context) {
            value++;
        }

        @Override
        protected void set(AgentContext context, Integer v) {
            value = v;
        }

        @Override
        protected Integer get(AgentContext context) {
            return value;
        }
    }

    static class Pulse extends AgentEvent {
        private final StepCounter stepCounter;

        Pulse(StepCounter stepCounter) {
            super("pulse", true, AttributeAccessLevel.PUBLIC);
            this.stepCounter = stepCounter;
        }

        @Override
        protected boolean isTriggered(AgentContext context) {
            return stepCounter.get() % 2 == 0;
        }

        @Override
        protected void run(AgentContext context) {}
    }

    static class Temperature extends EnvironmentProperty<Double> {
        private double value = 20.0;

        Temperature() {
            super("temperature", true, AttributeAccessLevel.PUBLIC, Double.class);
        }

        @Override
        protected void run(EnvironmentContext context) {
            value += 1.0;
        }

        @Override
        protected void set(EnvironmentContext context, Double v) {
            value = v;
        }

        @Override
        protected Double get(EnvironmentContext context) {
            return value;
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResultsPersistThroughDiskDatabase() {
        int population = 7;
        int ticks = 6;
        int threads = 2;

        DefaultAgentGenerator agentGenerator = new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                String name = "agent_" + index++;
                StepCounter stepCounter = new StepCounter();
                AgentAttributeSet movement = new AgentAttributeSet("movement",
                        (List<Attribute>) (List<?>) List.of(stepCounter, new Pulse(stepCounter)));
                return new Agent(name, List.of(movement));
            }
        };

        EnvironmentGenerator environmentGenerator = new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config, RandomGenerator random) {
                EnvironmentAttributeSet climate = new EnvironmentAttributeSet("climate",
                        (List<Attribute>) (List<?>) List.of(new Temperature()));
                return new Environment(List.of(climate));
            }
        };

        Config config = Config.builder()
                .populationSize(population)
                .tickCount(ticks)
                .threadCount(threads)
                .areThreadsSynced(true)
                .agentGenerator(agentGenerator)
                .environmentGenerator(environmentGenerator)
                .scheduler(new InOrderScheduler())
                .runLogDatabaseFactory(new DiskBasedAttributeSetLogDatabaseFactory())
                .build();

        Model model = new Model(config);
        model.run();

        ReadOnlyResults results = model.getResults();

        assertEquals(population, results.agents().agentLogCount(),
                "Every agent should have a complete log, even with an uneven split.");

        List<Integer> expectedSteps = List.of(1, 2, 3, 4, 5, 6);
        List<Boolean> expectedPulse = List.of(false, true, false, true, false, true);

        for (int i = 0; i < population; i++) {
            String name = "agent_" + i;
            assertEquals(expectedSteps,
                    results.agents().attributeLogs(name, "movement", "steps", Integer.class),
                    name + " integer log should survive the SQLite round-trip.");
            assertEquals(expectedPulse,
                    results.agents().attributeLogs(name, "movement", "pulse", Boolean.class),
                    name + " boolean log should survive the SQLite round-trip.");
        }

        assertEquals(
                List.of(21.0, 22.0, 23.0, 24.0, 25.0, 26.0),
                results.environment().attributeLogs("climate", "temperature", Double.class),
                "Environment double log should survive the SQLite round-trip.");
    }
}
