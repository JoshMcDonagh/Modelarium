package integration.eventsAndRoutines;

import com.rits.cloning.Cloner;
import modelarium.Config;
import modelarium.Model;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.*;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.EnvironmentGenerator;
import modelarium.results.immutable.ImmutableResults;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: runs a complete synchronised model whose agents and
 * environment each carry a property, an event and a routine. It verifies that,
 * end-to-end across a real multithreaded run:
 *
 * <ul>
 *     <li>properties run and log a value every tick;</li>
 *     <li>events evaluate their trigger condition every tick, log the resulting
 *         boolean, and only run when triggered;</li>
 *     <li>routines run every tick (their effect is visible through a sibling
 *         property) but are never themselves logged.</li>
 * </ul>
 */
public class EventsAndRoutinesIntegrationTest {

    @BeforeAll
    static void openForCloning() {
        EventsAndRoutinesIntegrationTest.class.getModule().addOpens(
                "integration.eventsAndRoutines",
                Cloner.class.getModule()
        );
    }

    // ---- Agent attributes ----

    static class StepCount extends AgentProperty<Integer> {
        private int value = 0;

        StepCount() {
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

    /** Fires on every tick where the sibling step counter is even. */
    static class PulseEvent extends AgentEvent {
        private final StepCount stepCount;

        PulseEvent(StepCount stepCount) {
            super("pulse", true, AttributeAccessLevel.PUBLIC);
            this.stepCount = stepCount;
        }

        @Override
        protected boolean isTriggered(AgentContext context) {
            return stepCount.get() % 2 == 0;
        }

        @Override
        protected void run(AgentContext context) {}
    }

    static class Energy extends AgentProperty<Integer> {
        private int value = 0;

        Energy() {
            super("energy", true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(AgentContext context) {
            // No-op: the sibling routine mutates this property.
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

    /** Not logged: increments the sibling energy property every tick. */
    static class RechargeRoutine extends AgentRoutine {
        private final Energy energy;

        RechargeRoutine(Energy energy) {
            super("recharge", AttributeAccessLevel.PUBLIC);
            this.energy = energy;
        }

        @Override
        protected void run(AgentContext context) {
            energy.set(energy.get() + 1);
        }
    }

    // ---- Environment attributes ----

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

    /** Fires once the sibling temperature reaches or exceeds 23. */
    static class HeatAlarm extends EnvironmentEvent {
        private final Temperature temperature;

        HeatAlarm(Temperature temperature) {
            super("heatAlarm", true, AttributeAccessLevel.PUBLIC);
            this.temperature = temperature;
        }

        @Override
        protected boolean isTriggered(EnvironmentContext context) {
            return temperature.get() >= 23.0;
        }

        @Override
        protected void run(EnvironmentContext context) {}
    }

    static class Airflow extends EnvironmentProperty<Integer> {
        private int value = 0;

        Airflow() {
            super("airflow", true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(EnvironmentContext context) {
            // No-op: the sibling routine mutates this property.
        }

        @Override
        protected void set(EnvironmentContext context, Integer v) {
            value = v;
        }

        @Override
        protected Integer get(EnvironmentContext context) {
            return value;
        }
    }

    /** Not logged: increments the sibling airflow property every tick. */
    static class VentilateRoutine extends EnvironmentRoutine {
        private final Airflow airflow;

        VentilateRoutine(Airflow airflow) {
            super("ventilate", AttributeAccessLevel.PUBLIC);
            this.airflow = airflow;
        }

        @Override
        protected void run(EnvironmentContext context) {
            airflow.set(airflow.get() + 1);
        }
    }

    // ---- Test ----

    @Test
    @SuppressWarnings("unchecked")
    public void testEventsAndRoutinesRunAndLogCorrectly() {
        int population = 4;
        int ticks = 10;
        int threads = 2;

        DefaultAgentGenerator agentGenerator = new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                String name = "agent_" + index++;
                StepCount stepCount = new StepCount();
                Energy energy = new Energy();
                AgentAttributeSet activity = new AgentAttributeSet("activity",
                        (List<Attribute>) (List<?>) List.of(
                                stepCount,
                                new PulseEvent(stepCount),
                                new RechargeRoutine(energy),
                                energy));
                return new Agent(name, List.of(activity));
            }
        };

        EnvironmentGenerator environmentGenerator = new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config, RandomGenerator random) {
                Temperature temperature = new Temperature();
                Airflow airflow = new Airflow();
                EnvironmentAttributeSet climate = new EnvironmentAttributeSet("climate",
                        (List<Attribute>) (List<?>) List.of(
                                temperature,
                                new HeatAlarm(temperature),
                                new VentilateRoutine(airflow),
                                airflow));
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
                .build();

        Model model = new Model(config);
        model.run();

        ImmutableResults results = model.getResults();

        // Every agent's step counter should log 1..ticks.
        List<Integer> expectedSteps = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        for (int i = 0; i < population; i++) {
            String name = "agent_" + i;
            assertEquals(expectedSteps,
                    results.agents().attributeLogs(name, "activity", "steps", Integer.class),
                    name + " step counter should log 1..ticks.");
        }

        // The event logs its trigger boolean each tick (even steps -> true).
        assertEquals(
                List.of(false, true, false, true, false, true, false, true, false, true),
                results.agents().attributeLogs("agent_0", "activity", "pulse", Boolean.class),
                "Pulse event should fire on even steps.");

        // The routine's effect is visible through the sibling energy property.
        assertEquals(expectedSteps,
                results.agents().attributeLogs("agent_0", "activity", "energy", Integer.class),
                "Recharge routine should raise energy by one each tick.");

        // The routine itself must never be logged.
        assertFalse(
                results.agents().attributeSetLogs("agent_0", "activity").containsKey("recharge"),
                "Routines should not be logged.");

        // Environment temperature should log 21..30.
        assertEquals(
                List.of(21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0),
                results.environment().attributeLogs("climate", "temperature", Double.class),
                "Environment temperature should rise by one each tick.");

        // Environment event: false until temperature reaches 23, then true.
        assertEquals(
                List.of(false, false, true, true, true, true, true, true, true, true),
                results.environment().attributeLogs("climate", "heatAlarm", Boolean.class),
                "Heat alarm should trigger once temperature reaches 23.");

        // Environment routine effect visible through airflow; routine not logged.
        assertEquals(
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                results.environment().attributeLogs("climate", "airflow", Integer.class),
                "Ventilate routine should raise airflow by one each tick.");
        assertFalse(
                results.environment().attributeSetLogs("climate").containsKey("ventilate"),
                "Environment routines should not be logged.");
    }
}
