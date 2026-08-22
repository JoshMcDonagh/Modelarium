package integration.determinism;

import com.rits.cloning.Cloner;
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
import modelarium.scheduler.RandomOrderScheduler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DeterminismTest {
    private static final int POPULATION = 24;
    private static final int TICKS = 15;
    private static final int THREADS = 3;

    @BeforeAll
    static void openForCloning() {
        DeterminismTest.class.getModule().addOpens(
                "integration.determinism",
                Cloner.class.getModule()
        );
    }

    // ---- Agent property: a seeded random walk ----

    static class RandomWalk extends AgentProperty<Long> {
        private long value = 0;

        RandomWalk() {
            super("walk", true, AttributeAccessLevel.PUBLIC, Long.class);
        }

        @Override
        protected void run(AgentContext context) {
            value += context.getRandom().nextLong(1_000_000);
        }

        @Override
        protected void set(AgentContext context, Long v) {
            value = v;
        }

        @Override
        protected Long get(AgentContext context) {
            return value;
        }
    }

    private Map<String, List<Long>> runModel(long seed) {
        return runModel(seed, false);
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Long>> runModel(long seed, boolean areThreadsSynced) {
        DefaultAgentGenerator agentGen = new DefaultAgentGenerator() {
            private int idx = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                String name = String.format("agent_%02d", idx++);
                MutableAgentAttributeSet set = new MutableAgentAttributeSet("state",
                        (List<Attribute>) (List<?>) List.of(new RandomWalk()));
                return new Agent(name, List.of(set));
            }
        };

        Config config = Config.builder()
                .populationSize(POPULATION)
                .tickCount(TICKS)
                .threadCount(THREADS)
                .areThreadsSynced(areThreadsSynced)
                .agentGenerator(agentGen)
                .environmentGenerator(new FunctionalEnvironmentGenerator((c, random) -> new Environment(List.of())))
                .scheduler(new RandomOrderScheduler())
                .seed(seed)
                .build();

        Model model = new Model(config);
        model.run();

        ReadOnlyResults results = model.getResults();
        Map<String, List<Long>> logs = new TreeMap<>();

        for (int i = 0; i < POPULATION; i++) {
            String name = String.format("agent_%02d", i);
            logs.put(name, results.agents().attributeLogs(name, "state", "walk", Long.class));
        }

        return logs;
    }

    @Test
    public void sameSeedAndThreadCountProducesIdenticalLogs() {
        Map<String, List<Long>> first = runModel(42L);
        Map<String, List<Long>> second = runModel(42L);

        for (Map.Entry<String, List<Long>> entry : first.entrySet())
            assertEquals(TICKS, entry.getValue().size(),
                    "Agent " + entry.getKey() + " should log one value per tick.");

        assertEquals(first, second,
                "Two runs with the same seed and thread count must produce "
                        + "identical full attribute logs, not just final values.");
    }

    @Test
    public void differentSeedsProduceDifferentLogs() {
        Map<String, List<Long>> first = runModel(42L);
        Map<String, List<Long>> third = runModel(43L);

        // 24 agents x 15 ticks of independent bounded draws colliding across
        // two seeds is astronomically unlikely; if this fails, the seed is
        // not reaching agent behaviour at all.
        assertNotEquals(first, third,
                "Runs with different seeds should diverge; if they match, "
                        + "Config.seed() is not being consumed.");
    }

    @Test
    public void sameSeedSyncedProducesIdenticalLogs() {
        Map<String, List<Long>> first = runModel(42L, true);
        Map<String, List<Long>> second = runModel(42L, true);

        assertEquals(first, second,
                "Two synchronised runs with the same seed and thread count must "
                        + "produce identical full attribute logs.");
    }
}
