package dev.modelarium.examples.epstein_axtell_sugarscape.entities.agents;

import dev.modelarium.examples.epstein_axtell_sugarscape.entities.agents.attributes.SugarscapeBooleanProperty;
import dev.modelarium.examples.epstein_axtell_sugarscape.entities.agents.attributes.SugarscapeIntegerProperty;
import dev.modelarium.examples.epstein_axtell_sugarscape.scheduler.SugarscapeRunSpec;
import modelarium.Config;
import modelarium.entities.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.generators.DefaultAgentGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/** Generates the heterogeneous agents used by the Chapter II Sugarscape experiments. */
public final class SugarscapeAgentGenerator extends DefaultAgentGenerator {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private final SugarscapeRunSpec spec;
    private final List<Integer> availableCells = new ArrayList<>(WIDTH * HEIGHT);
    private int generated = 0;

    public SugarscapeAgentGenerator(SugarscapeRunSpec spec) {
        this.spec = spec;
        if (spec.placementMode() == SugarscapeRunSpec.PlacementMode.LOWER_RIGHT_BLOCK) {
            // 20x20 compact population used for the collective-wave reconstruction.
            for (int y = 0; y < 20; y++)
                for (int x = 30; x < 50; x++)
                    availableCells.add(y * WIDTH + x);
        } else {
            for (int cell = 0; cell < WIDTH * HEIGHT; cell++)
                availableCells.add(cell);
        }
    }

    @Override
    protected Agent generateAgent(Config config, RandomGenerator random) {
        if (availableCells.isEmpty())
            throw new IllegalStateException("No unoccupied Sugarscape cell remains for initial placement");

        int cellIndex = availableCells.remove(random.nextInt(availableCells.size()));
        int x = cellIndex % WIDTH;
        int y = cellIndex / WIDTH;

        List<Attribute> state = List.of(
                new SugarscapeBooleanProperty("alive", true),
                new SugarscapeIntegerProperty("x", x),
                new SugarscapeIntegerProperty("y", y),
                new SugarscapeIntegerProperty("vision", randomInclusive(random, spec.visionMinimum(), spec.visionMaximum())),
                new SugarscapeIntegerProperty("metabolism", randomInclusive(random, spec.metabolismMinimum(), spec.metabolismMaximum())),
                new SugarscapeIntegerProperty("wealth", randomInclusive(random, spec.initialWealthMinimum(), spec.initialWealthMaximum())),
                new SugarscapeIntegerProperty("age", 0),
                new SugarscapeIntegerProperty("max_age", randomInclusive(random, spec.maximumAgeMinimum(), spec.maximumAgeMaximum()))
        );

        Agent agent = new Agent(
                "agent_" + generated,
                List.of(new AgentAttributeSet("state", state))
        );
        generated++;
        return agent;
    }

    private static int randomInclusive(RandomGenerator random, int minimum, int maximum) {
        if (minimum == Integer.MAX_VALUE && maximum == Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        if (minimum == maximum)
            return minimum;
        return random.nextInt(minimum, maximum + 1);
    }
}
