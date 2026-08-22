package dev.modelarium.examples.multicore;

import modelarium.Config;
import modelarium.Model;
import modelarium.entities.agents.generators.FunctionalDefaultAgentGenerator;
import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.functional.FunctionalAgentProperty;
import modelarium.entities.attributes.properties.functional.FunctionalEnvironmentProperty;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.generators.FunctionalEnvironmentGenerator;
import modelarium.results.immutable.ReadOnlyResults;
import modelarium.scheduler.InOrderScheduler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Example of a synchronised two-core model in which agents read each other's state across cores.
 *
 * <p>Each agent owns a {@code local_value} property that increments every tick, and a {@code partner_sum} property
 * that accumulates its partner's {@code local_value} each tick via the context's agent access. The default agent
 * generator distributes agents across cores round-robin, so with an even population every agent's partner (the next
 * agent by index) lives on the other core and each read travels through the co-ordinator.
 *
 * <p>Two synchronisation details are worth noting. First, a read of an agent on another core observes that agent's
 * state as of the end of the previous tick, since worker cores push their updates to the co-ordinator at each tick
 * boundary. Second, partner reads are skipped on tick 0, before the first synchronisation point. Together these
 * make the run fully deterministic: after the run, each agent's {@code partner_sum} equals the sum of its partner's
 * values from ticks 0 to {@code tickCount - 2}. The environment also owns a logged {@code ticks_completed} property
 * to show environment attributes running at the co-ordinator once per tick.
 */
public final class CrossCoreInteractionExample {

    /** The name of the attribute set each agent owns */
    public static final String ATTRIBUTE_SET_NAME = "interaction";

    /** The name of the logged property each agent increments every tick */
    public static final String LOCAL_VALUE_PROPERTY_NAME = "local_value";

    /** The name of the logged property accumulating the partner's value */
    public static final String PARTNER_SUM_PROPERTY_NAME = "partner_sum";

    /** The name of the environment's attribute set */
    public static final String ENVIRONMENT_ATTRIBUTE_SET_NAME = "environment_state";

    /** The name of the environment's logged tick counter property */
    public static final String TICKS_COMPLETED_PROPERTY_NAME = "ticks_completed";

    private CrossCoreInteractionExample() {}

    /**
     * Builds and runs the cross-core interaction model on two synchronised cores.
     *
     * @param populationSize the number of agents the model will contain (must be even so every partner is remote)
     * @param tickCount the number of ticks the model will perform
     * @param seed the seed for the model's random generator
     * @return the results of the completed run
     */
    public static ReadOnlyResults run(int populationSize, int tickCount, long seed) {
        if (populationSize % 2 != 0)
            throw new IllegalArgumentException("populationSize must be even so every partner is on the other core");

        AtomicInteger nextAgentIndex = new AtomicInteger(0);

        Config config = Config.builder()
                .populationSize(populationSize)
                .tickCount(tickCount)
                .threadCount(2)
                .areThreadsSynced(true)
                .agentGenerator(new FunctionalDefaultAgentGenerator((cfg, random) -> {
                    int index = nextAgentIndex.getAndIncrement();
                    String partnerName = "agent_" + ((index + 1) % cfg.populationSize());
                    return makeAgent("agent_" + index, index, partnerName);
                }))
                .environmentGenerator(new FunctionalEnvironmentGenerator((cfg, random) -> makeEnvironment()))
                .scheduler(new InOrderScheduler())
                .seed(seed)
                .build();

        Model model = new Model(config);
        model.run();
        return model.getResults();
    }

    /**
     * Creates a single agent with an incrementing local value and a partner-reading accumulator.
     *
     * @param name the agent's unique name
     * @param startingValue the value the agent's local value starts at (its index)
     * @param partnerName the name of the agent whose local value is accumulated each tick
     * @return a new {@link Agent} instance
     */
    private static Agent makeAgent(String name, int startingValue, String partnerName) {
        FunctionalAgentProperty<Double> localValue = new FunctionalAgentProperty<>(
                LOCAL_VALUE_PROPERTY_NAME,
                true,
                AttributeAccessLevel.PUBLIC,
                Double.class,
                (context, value) -> value,
                (context, currentValue, newValue) -> newValue,
                (context, value) -> value == null ? (double) startingValue : value + 1.0
        );

        FunctionalAgentProperty<Double> partnerSum = new FunctionalAgentProperty<>(
                PARTNER_SUM_PROPERTY_NAME,
                true,
                AttributeAccessLevel.PUBLIC,
                Double.class,
                (context, value) -> value,
                (context, currentValue, newValue) -> newValue,
                (context, value) -> {
                    double accumulated = value == null ? 0.0 : value;

                    // Other cores' state only becomes visible after the first synchronisation point.
                    if (context.getClock().currentTick() == 0)
                        return accumulated;

                    ReadOnlyAgent partner = context.getAgent(partnerName);
                    Double partnerValue = (Double) partner
                            .getAttributeSet(ATTRIBUTE_SET_NAME)
                            .getProperty(LOCAL_VALUE_PROPERTY_NAME)
                            .get();

                    return accumulated + (partnerValue == null ? 0.0 : partnerValue);
                }
        );

        return new Agent(name, List.of(
                new MutableAgentAttributeSet(ATTRIBUTE_SET_NAME, List.<Attribute>of(localValue, partnerSum))
        ));
    }

    /**
     * Creates the model's environment with a logged tick counter property.
     *
     * @return a new {@link Environment} instance
     */
    private static Environment makeEnvironment() {
        FunctionalEnvironmentProperty<Integer> ticksCompleted = new FunctionalEnvironmentProperty<>(
                TICKS_COMPLETED_PROPERTY_NAME,
                true,
                AttributeAccessLevel.PUBLIC,
                Integer.class,
                (context, value) -> value,
                (context, currentValue, newValue) -> newValue,
                (context, value) -> value == null ? 1 : value + 1
        );

        return new Environment("environment", List.of(
                new MutableEnvironmentAttributeSet(ENVIRONMENT_ATTRIBUTE_SET_NAME,
                        List.<Attribute>of(ticksCompleted))
        ));
    }

    /**
     * Runs the example with demonstration parameters and prints one agent's accumulated partner sum.
     *
     * @param args unused command line arguments
     */
    public static void main(String[] args) {
        int populationSize = 10;
        int tickCount = 50;

        ReadOnlyResults results = run(populationSize, tickCount, 42L);

        List<Double> partnerSums = results.agents().attributeLogs(
                "agent_0", ATTRIBUTE_SET_NAME, PARTNER_SUM_PROPERTY_NAME, Double.class);
        List<Integer> ticksCompleted = results.environment().attributeLogs(
                ENVIRONMENT_ATTRIBUTE_SET_NAME, TICKS_COMPLETED_PROPERTY_NAME, Integer.class);

        System.out.printf("Ran %d agents on two synchronised cores for %d ticks.%n", populationSize, tickCount);
        System.out.printf("agent_0 accumulated partner sum: %.1f%n", partnerSums.get(partnerSums.size() - 1));
        System.out.printf("Environment recorded %d completed ticks.%n", ticksCompleted.get(ticksCompleted.size() - 1));
    }
}
