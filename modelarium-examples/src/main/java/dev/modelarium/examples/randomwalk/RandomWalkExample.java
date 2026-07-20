package dev.modelarium.examples.randomwalk;

import modelarium.Config;
import modelarium.Model;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.generators.FunctionalDefaultAgentGenerator;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.functional.FunctionalAgentProperty;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.FunctionalEnvironmentGenerator;
import modelarium.results.immutable.ImmutableResults;
import modelarium.scheduler.InOrderScheduler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Example of a minimal single-core Modelarium model: a population of one-dimensional random walkers.
 *
 * <p>Each walker owns a single logged {@code position} property whose run function adds a Gaussian step to the
 * stored value every tick, drawn from the seeded random generator the context provides. The example demonstrates
 * the smallest useful model: a functional agent generator, an inert environment, the in-order scheduler, and
 * reading a logged series back out of the results.
 */
public final class RandomWalkExample {

    /** The name of the attribute set each walker owns */
    public static final String ATTRIBUTE_SET_NAME = "movement";

    /** The name of the logged position property */
    public static final String POSITION_PROPERTY_NAME = "position";

    private RandomWalkExample() {}

    /**
     * Builds and runs the random walk model.
     *
     * @param populationSize the number of walkers the model will contain
     * @param tickCount the number of ticks the model will perform
     * @param seed the seed for the model's random generator, making the run reproducible
     * @return the results of the completed run
     */
    public static ImmutableResults run(int populationSize, int tickCount, long seed) {
        AtomicInteger nextWalkerIndex = new AtomicInteger(0);

        Config config = Config.builder()
                .populationSize(populationSize)
                .tickCount(tickCount)
                .threadCount(1)
                .areThreadsSynced(false)
                .agentGenerator(new FunctionalDefaultAgentGenerator(cfg ->
                        makeWalker("walker_" + nextWalkerIndex.getAndIncrement())))
                .environmentGenerator(new FunctionalEnvironmentGenerator(cfg ->
                        new Environment("environment", List.of())))
                .scheduler(new InOrderScheduler())
                .seed(seed)
                .build();

        Model model = new Model(config);
        model.run();
        return model.getResults();
    }

    /**
     * Creates a single walker agent with a logged position property.
     *
     * @param name the walker's unique name
     * @return a new walker {@link Agent} instance
     */
    private static Agent makeWalker(String name) {
        FunctionalAgentProperty<Double> position = new FunctionalAgentProperty<>(
                POSITION_PROPERTY_NAME,
                true,
                AttributeAccessLevel.PUBLIC,
                Double.class,
                (context, value) -> value,
                (context, currentValue, newValue) -> newValue,
                (context, value) -> (value == null ? 0.0 : value) + context.getRandom().nextGaussian()
        );

        return new Agent(name, List.of(
                new AgentAttributeSet(name, ATTRIBUTE_SET_NAME, List.<Attribute>of(position))
        ));
    }

    /**
     * Runs the example with demonstration parameters and prints a summary of the results.
     *
     * @param args unused command line arguments
     */
    public static void main(String[] args) {
        int populationSize = 50;
        int tickCount = 200;

        ImmutableResults results = run(populationSize, tickCount, 42L);

        double meanFinalPosition = 0.0;
        for (int i = 0; i < populationSize; i++) {
            List<Double> positions = results.agents().attributeLogs(
                    "walker_" + i, ATTRIBUTE_SET_NAME, POSITION_PROPERTY_NAME, Double.class);
            meanFinalPosition += positions.get(positions.size() - 1);
        }
        meanFinalPosition /= populationSize;

        System.out.printf("Ran %d walkers for %d ticks.%n", populationSize, tickCount);
        System.out.printf("Mean final position: %.3f%n", meanFinalPosition);
    }
}
