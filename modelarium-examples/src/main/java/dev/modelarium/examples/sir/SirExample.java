package dev.modelarium.examples.sir;

import modelarium.Config;
import modelarium.Model;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.generators.FunctionalDefaultAgentGenerator;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.functional.FunctionalAgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.functional.FunctionalAgentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.FunctionalEnvironmentGenerator;
import modelarium.results.immutable.ImmutableResults;
import modelarium.scheduler.RandomOrderScheduler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Example of a single-core SIR (susceptible, infected, recovered) contagion model.
 *
 * <p>Each person owns a logged {@code state} property along with two events: an infection event, whose trigger
 * probability scales with the fraction of the population currently infected (sampled through the context's filtered
 * agent access), and a recovery event with a fixed per-tick probability. The example demonstrates event triggers,
 * reading and writing a property from event logic, and querying other agents through the context.
 *
 * <p>Agents are updated sequentially within a tick (a deliberate simplicity), so an agent's trigger sees the states
 * of earlier-updated agents from the current tick and later-updated agents from the previous tick.
 */
public final class SirExample {

    /** The name of the attribute set each person owns */
    public static final String ATTRIBUTE_SET_NAME = "epidemic";

    /** The name of the logged state property, holding "S", "I" or "R" */
    public static final String STATE_PROPERTY_NAME = "state";

    /** The per-contact transmission scaling used by the infection event */
    private static final double TRANSMISSION_RATE = 0.6;

    /** The per-tick probability that an infected person recovers */
    private static final double RECOVERY_RATE = 0.1;

    private SirExample() {}

    /**
     * Builds and runs the SIR model.
     *
     * @param populationSize the number of people the model will contain
     * @param initialInfected the number of people who start in the infected state
     * @param tickCount the number of ticks the model will perform
     * @param seed the seed for the model's random generator, making the run reproducible
     * @return the results of the completed run
     */
    public static ImmutableResults run(int populationSize, int initialInfected, int tickCount, long seed) {
        if (initialInfected < 0 || initialInfected > populationSize)
            throw new IllegalArgumentException("initialInfected must be between 0 and populationSize");

        AtomicInteger nextPersonIndex = new AtomicInteger(0);

        Config config = Config.builder()
                .populationSize(populationSize)
                .tickCount(tickCount)
                .threadCount(1)
                .areThreadsSynced(false)
                .agentGenerator(new FunctionalDefaultAgentGenerator(cfg -> {
                    int index = nextPersonIndex.getAndIncrement();
                    return makePerson("person_" + index, index < initialInfected ? "I" : "S");
                }))
                .environmentGenerator(new FunctionalEnvironmentGenerator(cfg ->
                        new Environment("environment", List.of())))
                .scheduler(new RandomOrderScheduler())
                .seed(seed)
                .build();

        Model model = new Model(config);
        model.run();
        return model.getResults();
    }

    /**
     * Creates a single person agent with a state property and infection/recovery events.
     *
     * @param name the person's unique name
     * @param initialState the state the person starts in ("S" or "I")
     * @return a new person {@link Agent} instance
     */
    private static Agent makePerson(String name, String initialState) {
        // The state property is listed first so it is initialised before the events read it on the first tick.
        FunctionalAgentProperty<String> state = new FunctionalAgentProperty<>(
                STATE_PROPERTY_NAME,
                true,
                AttributeAccessLevel.PUBLIC,
                String.class,
                (context, value) -> value,
                (context, currentValue, newValue) -> newValue,
                (context, value) -> value == null ? initialState : value
        );

        FunctionalAgentEvent infection = new FunctionalAgentEvent(
                "infection",
                true,
                AttributeAccessLevel.PUBLIC,
                context -> stateProperty(context).set("I"),
                context -> {
                    if (!"S".equals(stateProperty(context).get()))
                        return false;

                    double infectedFraction = fractionInfected(context);
                    return context.getRandom().nextDouble() < TRANSMISSION_RATE * infectedFraction;
                }
        );

        FunctionalAgentEvent recovery = new FunctionalAgentEvent(
                "recovery",
                true,
                AttributeAccessLevel.PUBLIC,
                context -> stateProperty(context).set("R"),
                context -> "I".equals(stateProperty(context).get())
                        && context.getRandom().nextDouble() < RECOVERY_RATE
        );

        return new Agent(name, List.of(
                new AgentAttributeSet(name, ATTRIBUTE_SET_NAME, List.of(state, infection, recovery))
        ));
    }

    /**
     * Retrieves the owning agent's state property from its attribute set.
     *
     * @param context the context of the attribute currently being run
     * @return the agent's state {@link AgentProperty} instance
     */
    @SuppressWarnings("unchecked")
    private static AgentProperty<String> stateProperty(AgentContext context) {
        return (AgentProperty<String>) context.getThisAttributeSet().getProperty(STATE_PROPERTY_NAME);
    }

    /**
     * Returns the fraction of the population currently in the infected state.
     *
     * @param context the context of the attribute currently being run
     * @return the infected fraction, between 0.0 and 1.0
     */
    private static double fractionInfected(AgentContext context) {
        int total = context.getFilteredAgents(agent -> true).size();
        if (total == 0)
            return 0.0;

        int infected = context.getFilteredAgents(agent ->
                "I".equals(agent.getProperty(ATTRIBUTE_SET_NAME, STATE_PROPERTY_NAME).get())).size();

        return (double) infected / total;
    }

    /**
     * Runs the example with demonstration parameters and prints the final state counts.
     *
     * @param args unused command line arguments
     */
    public static void main(String[] args) {
        int populationSize = 100;
        int initialInfected = 3;
        int tickCount = 100;

        ImmutableResults results = run(populationSize, initialInfected, tickCount, 42L);

        int susceptible = 0, infected = 0, recovered = 0;
        for (int i = 0; i < populationSize; i++) {
            List<String> states = results.agents().attributeLogs(
                    "person_" + i, ATTRIBUTE_SET_NAME, STATE_PROPERTY_NAME, String.class);
            switch (states.get(states.size() - 1)) {
                case "S" -> susceptible++;
                case "I" -> infected++;
                case "R" -> recovered++;
            }
        }

        System.out.printf("Ran an SIR model of %d people for %d ticks.%n", populationSize, tickCount);
        System.out.printf("Final counts - susceptible: %d, infected: %d, recovered: %d%n",
                susceptible, infected, recovered);
    }
}
