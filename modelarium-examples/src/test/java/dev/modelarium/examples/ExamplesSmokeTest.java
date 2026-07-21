package dev.modelarium.examples;

import dev.modelarium.examples.multicore.CrossCoreInteractionExample;
import dev.modelarium.examples.randomwalk.RandomWalkExample;
import modelarium.results.immutable.ImmutableResults;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Smoke tests running each example end to end with tiny parameters.
 *
 * <p>These tests exist to keep the examples honest: they exercise the full Modelarium runtime (including the
 * synchronised multi-core path) on every build, so any change to the library's behaviour or public API that breaks
 * the examples fails the build immediately.
 */
class ExamplesSmokeTest {

    @Test
    void randomWalkProducesFullTrajectories() {
        int populationSize = 4;
        int tickCount = 5;

        ImmutableResults results = RandomWalkExample.run(populationSize, tickCount, 7L);

        for (int i = 0; i < populationSize; i++) {
            List<Double> positions = results.agents().attributeLogs(
                    "walker_" + i,
                    RandomWalkExample.ATTRIBUTE_SET_NAME,
                    RandomWalkExample.POSITION_PROPERTY_NAME,
                    Double.class);
            assertEquals(tickCount, positions.size());
        }
    }

    @Test
    void crossCoreReadsAccumulateDeterministically() {
        int populationSize = 4;
        int tickCount = 6;

        ImmutableResults results = CrossCoreInteractionExample.run(populationSize, tickCount, 7L);

        for (int i = 0; i < populationSize; i++) {
            String agentName = "agent_" + i;
            int partnerIndex = (i + 1) % populationSize;

            List<Double> localValues = results.agents().attributeLogs(
                    agentName,
                    CrossCoreInteractionExample.ATTRIBUTE_SET_NAME,
                    CrossCoreInteractionExample.LOCAL_VALUE_PROPERTY_NAME,
                    Double.class);
            List<Double> partnerSums = results.agents().attributeLogs(
                    agentName,
                    CrossCoreInteractionExample.ATTRIBUTE_SET_NAME,
                    CrossCoreInteractionExample.PARTNER_SUM_PROPERTY_NAME,
                    Double.class);

            assertEquals(tickCount, localValues.size());
            assertEquals(tickCount, partnerSums.size());

            // The local value starts at the agent's index and increments once per tick.
            assertEquals(i + tickCount - 1.0, localValues.get(tickCount - 1).doubleValue());

            // At tick t (t >= 1) an agent sees its partner's value from the end of tick t - 1, which is
            // partnerIndex + (t - 1). The accumulated sum over ticks 1 to tickCount - 1 is therefore:
            double expectedSum = 0.0;
            for (int t = 1; t < tickCount; t++)
                expectedSum += partnerIndex + (t - 1);
            assertEquals(expectedSum, partnerSums.get(tickCount - 1).doubleValue());
        }

        // The environment's tick counter runs once per tick at the co-ordinator.
        List<Integer> ticksCompleted = results.environment().attributeLogs(
                CrossCoreInteractionExample.ENVIRONMENT_ATTRIBUTE_SET_NAME,
                CrossCoreInteractionExample.TICKS_COMPLETED_PROPERTY_NAME,
                Integer.class);
        assertEquals(tickCount, ticksCompleted.get(ticksCompleted.size() - 1).intValue());
    }
}
