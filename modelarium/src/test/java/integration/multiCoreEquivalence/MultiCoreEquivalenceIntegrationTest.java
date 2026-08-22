package integration.multiCoreEquivalence;

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
import modelarium.results.immutable.ImmutableResults;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test: verifies that a deterministic model produces equivalent
 * per-agent results regardless of how many threads it's split across.
 *
 * <p>Uses an unsynced model with a deterministic counter property so that
 * agent execution order doesn't affect final values. The key invariant is
 * that each agent's final counter value should be equal to the tick count,
 * no matter how agents are distributed across threads.
 */
public class MultiCoreEquivalenceIntegrationTest {

    static class DeterministicCounter extends AgentProperty<Integer> {
        private int value = 0;

        DeterministicCounter() {
            super("counter", true, AttributeAccessLevel.PUBLIC, Integer.class);
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

    @SuppressWarnings("unchecked")
    private Map<String, List<Integer>> runWithThreads(int threads) {
        int population = 6;
        int ticks = 10;

        DefaultAgentGenerator agentGen = new DefaultAgentGenerator() {
            private int idx = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                String name = "agent_" + idx++;
                DeterministicCounter counter = new DeterministicCounter();
                MutableAgentAttributeSet set = new MutableAgentAttributeSet("stats",
                        (List<Attribute>) (List<?>) List.of(counter));
                return new Agent(name, List.of(set));
            }
        };

        Config config = Config.builder()
                .populationSize(population)
                .tickCount(ticks)
                .threadCount(threads)
                .areThreadsSynced(false)
                .agentGenerator(agentGen)
                .environmentGenerator(new FunctionalEnvironmentGenerator((c, random) -> new Environment(List.of())))
                .scheduler(new InOrderScheduler())
                .build();

        Model model = new Model(config);
        model.run();

        ImmutableResults results = model.getResults();
        Map<String, List<Integer>> logs = new TreeMap<>();

        for (int i = 0; i < population; i++) {
            String name = "agent_" + i;
            logs.put(name, results.agents().attributeLogs(name, "stats", "counter", Integer.class));
        }

        return logs;
    }

    @Test
    public void testFinalValuesAreEquivalentAcrossThreadCounts() {
        // Gather final-tick values for 1, 2, and 3 threads
        Map<String, List<Integer>> results1 = runWithThreads(1);
        Map<String, List<Integer>> results2 = runWithThreads(2);
        Map<String, List<Integer>> results3 = runWithThreads(3);

        for (String agentName : results1.keySet()) {
            List<Integer> log1 = results1.get(agentName);
            List<Integer> log2 = results2.get(agentName);
            List<Integer> log3 = results3.get(agentName);

            // Same number of logged values
            assertEquals(log1.size(), log2.size(),
                    agentName + ": log size should match between 1 and 2 threads.");
            assertEquals(log1.size(), log3.size(),
                    agentName + ": log size should match between 1 and 3 threads.");

            // Same final value (should be == tick count)
            int final1 = log1.get(log1.size() - 1);
            int final2 = log2.get(log2.size() - 1);
            int final3 = log3.get(log3.size() - 1);

            assertEquals(final1, final2,
                    agentName + ": final counter should match between 1 and 2 threads.");
            assertEquals(final1, final3,
                    agentName + ": final counter should match between 1 and 3 threads.");

            assertEquals(10, final1,
                    agentName + ": final counter should equal the tick count.");
        }
    }
}
