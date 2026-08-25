package integration.agentEnvironmentInteraction;

import modelarium.Config;
import modelarium.Model;
import modelarium.entities.generators.DefaultAgentGenerator;
import modelarium.entities.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.Environment;
import modelarium.entities.readonly.ReadOnlyEnvironment;
import modelarium.entities.generators.EnvironmentGenerator;
import modelarium.results.readonly.ReadOnlyResults;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test: agents observe environment state during a synced run.
 *
 * <p>The environment has a "temperature" property that increases each tick.
 * Each agent records the temperature it observed at its tick. In synced mode,
 * the environment is run by the coordinator after all workers finish their
 * tick, so agents should observe the environment state from the PREVIOUS
 * tick (the coordinator runs the environment between worker ticks).
 */
public class AgentEnvironmentInteractionIntegrationTest {

    // ---- Environment property: temperature increases each tick ----

    static class Temperature extends EnvironmentProperty<Double> {
        private double temp = 20.0;

        Temperature() {
            super("temperature", true, AttributeAccessLevel.PUBLIC, Double.class);
        }

        @Override
        protected void run(EnvironmentContext context) {
            temp += 1.0;
        }

        @Override
        protected void set(EnvironmentContext context, Double v) {
            temp = v;
        }

        @Override
        protected Double get(EnvironmentContext context) {
            return temp;
        }
    }

    // ---- Agent property: reads the environment temperature on each tick ----

    static class ObservedTemperature extends AgentProperty<Double> {
        private double observed = 0.0;

        ObservedTemperature() {
            super("observedTemp", true, AttributeAccessLevel.PUBLIC, Double.class);
        }

        @Override
        protected void run(AgentContext context) {
            ReadOnlyEnvironment env = context.getEnvironment();
            observed = (Double) env.getProperty("weather", "temperature").get();
        }

        @Override
        protected void set(AgentContext context, Double v) {
            observed = v;
        }

        @Override
        protected Double get(AgentContext context) {
            return observed;
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAgentsCanAccessEnvironment() {
        int population = 4;
        int ticks = 5;
        int threads = 2;

        DefaultAgentGenerator agentGen = new DefaultAgentGenerator() {
            private int idx = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                String name = "agent_" + idx++;
                ObservedTemperature obs = new ObservedTemperature();
                AgentAttributeSet set = new AgentAttributeSet("sensors",
                        (List<Attribute>) (List<?>) List.of(obs));
                return new Agent(name, List.of(set));
            }
        };

        EnvironmentGenerator envGen = new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config, RandomGenerator random) {
                Temperature temp = new Temperature();
                EnvironmentAttributeSet weatherSet = new EnvironmentAttributeSet("weather",
                        (List<Attribute>) (List<?>) List.of(temp));
                return new Environment(List.of(weatherSet));
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

        ReadOnlyResults results = model.getResults();

        // Each agent should have `ticks` observations
        List<Double> observed = results.agents().attributeLogs(
                "agent_0", "sensors", "observedTemp", Double.class);
        assertEquals(ticks, observed.size());

        // Agents run before the coordinator runs the environment for the tick,
        // so they must observe the environment state from the end of the previous tick.
        // On tick 0 that is the initial state (20.0).
        for (int i = 0; i < observed.size(); i++) {
            assertEquals(20.0 + i, observed.get(i), 1e-9,
                    "Agent should observe the previous-tick environment state.");
        }

        // Check environment temperature was logged
        List<Double> tempLog = results.environment().attributeLogs(
                "weather", "temperature", Double.class);

        assertEquals(ticks, tempLog.size(),
                "Environment temperature should have one value per tick.");

        // Temperature starts at 20, increments 1/tick: 21, 22, 23, ...
        for (int i = 0; i < tempLog.size(); i++) {
            assertEquals(21.0 + i, tempLog.get(i), 1e-9,
                    "Temperature at environment tick " + i + " should be " + (21.0 + i));
        }
    }
}
