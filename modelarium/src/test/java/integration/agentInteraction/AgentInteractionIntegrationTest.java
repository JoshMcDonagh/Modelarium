package integration.agentInteraction;

import modelarium.Config;
import modelarium.Model;
import modelarium.entities.generators.DefaultAgentGenerator;
import modelarium.entities.readonly.ReadOnlyAgent;
import modelarium.entities.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.Environment;
import modelarium.entities.generators.FunctionalEnvironmentGenerator;
import modelarium.exceptions.AgentNotFoundException;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.ModelRunException;
import modelarium.results.readonly.ReadOnlyResults;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: exercises inter-agent access across cores. In a synchronised
 * model an agent can read another agent (even one owned by a different worker)
 * and can query the whole population through the coordinator; in an unsynchronised
 * model each worker sees only its own local agents.
 *
 * <p>{@link DefaultAgentGenerator} distributes agents round-robin, so with two
 * workers {@code agent_0} and {@code agent_1} are guaranteed to live on different
 * cores, making the reads genuinely cross-core.
 */
public class AgentInteractionIntegrationTest {

    /** The value every agent exposes, and which remote readers should observe. */
    private static final double REMOTE_VALUE = 42.0;

    /** Always reports the same value, regardless of tick. */
    static class ConstantValue extends AgentProperty<Double> {
        ConstantValue() {
            super("val", true, AttributeAccessLevel.PUBLIC, Double.class);
        }

        @Override
        protected void run(AgentContext context) {}

        @Override
        protected void set(AgentContext context, Double v) {}

        @Override
        protected Double get(AgentContext context) {
            return REMOTE_VALUE;
        }
    }

    /** Reads {@code agent_0}'s constant value each tick. */
    static class Observer extends AgentProperty<Double> {
        private double observed = -1.0;

        Observer() {
            super("observed", true, AttributeAccessLevel.PUBLIC, Double.class);
        }

        @Override
        protected void run(AgentContext context) {
            ReadOnlyAgent target = context.getAgent("agent_0");
            observed = (Double) target.getAttributeSet("value").getProperty("val").get();
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

    /** Logs how many agents the current core can see through the filter each tick. */
    static class PopulationCounter extends AgentProperty<Integer> {
        private int count = 0;

        PopulationCounter() {
            super("count", true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(AgentContext context) {
            count = context.getFilteredAgents(agent -> agent.name().startsWith("agent_")).size();
        }

        @Override
        protected void set(AgentContext context, Integer v) {
            count = v;
        }

        @Override
        protected Integer get(AgentContext context) {
            return count;
        }
    }

    /** Attempts to read a named agent each tick; throws if it cannot be found. */
    static class Reader extends AgentProperty<Double> {
        private final String targetName;

        Reader(String targetName) {
            super("reader", true, AttributeAccessLevel.PUBLIC, Double.class);
            this.targetName = targetName;
        }

        @Override
        protected void run(AgentContext context) {
            context.getAgent(targetName);
        }

        @Override
        protected void set(AgentContext context, Double v) {}

        @Override
        protected Double get(AgentContext context) {
            return 0.0;
        }
    }

    @SuppressWarnings("unchecked")
    private Config config(int population, int threads, boolean synced, AgentAttributeSetFactory factory) {
        DefaultAgentGenerator agentGenerator = new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                String name = "agent_" + index++;
                return new Agent(name, factory.attributeSetsFor(name));
            }
        };

        return Config.builder()
                .populationSize(population)
                .tickCount(10)
                .threadCount(threads)
                .areThreadsSynced(synced)
                .agentGenerator(agentGenerator)
                .environmentGenerator(new FunctionalEnvironmentGenerator((c, random) -> new Environment(List.of())))
                .scheduler(new InOrderScheduler())
                .build();
    }

    private interface AgentAttributeSetFactory {
        List<AgentAttributeSet> attributeSetsFor(String agentName);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSyncedAgentReadsAnotherCoresAgentProperty() {
        Config config = config(2, 2, true, name -> List.of(
                new AgentAttributeSet("value",
                        (List<Attribute>) (List<?>) List.of(new ConstantValue())),
                new AgentAttributeSet("observation",
                        (List<Attribute>) (List<?>) List.of(new Observer()))
        ));

        Model model = new Model(config);
        model.run();

        ReadOnlyResults results = model.getResults();
        List<Double> observed = results.agents().attributeLogs("agent_1", "observation", "observed", Double.class);

        assertEquals(10, observed.size(), "One observation should be logged per tick.");

        // agent_1 lives on a different worker from agent_0 (agents are distributed
        // round-robin), so these reads go through the model-wide snapshot from tick 0 onward.
        for (double value : observed)
            assertEquals(REMOTE_VALUE, value, 1e-9,
                    "agent_1 should read agent_0's value across cores from the first tick.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSyncedFilteredAgentsSeeWholePopulation() {
        int population = 6;
        Config config = config(population, 3, true, name -> List.of(
                new AgentAttributeSet("census",
                        (List<Attribute>) (List<?>) List.of(new PopulationCounter()))
        ));

        Model model = new Model(config);
        model.run();

        ReadOnlyResults results = model.getResults();
        List<Integer> counts = results.agents().attributeLogs("agent_0", "census", "count", Integer.class);

        assertEquals(10, counts.size(), "One population count should be logged per tick.");
        // The coordinator is initialised with the complete population before workers start,
        // so a synchronised filter should see the whole population from tick 0 onward.
        for (int count : counts)
            assertEquals(population, count,
                    "A synchronised filter should see the whole population.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnsyncedFilteredAgentsAreLocalOnly() {
        Config config = config(4, 2, false, name -> List.of(
                new AgentAttributeSet("census",
                        (List<Attribute>) (List<?>) List.of(new PopulationCounter()))
        ));

        Model model = new Model(config);
        model.run();

        ReadOnlyResults results = model.getResults();
        List<Integer> counts = results.agents().attributeLogs("agent_0", "census", "count", Integer.class);

        assertEquals(10, counts.size());
        // Two workers, four agents, round-robin: each core owns exactly two agents,
        // and an unsynchronised filter never leaves the local core.
        for (int count : counts)
            assertEquals(2, count, "An unsynchronised filter should see only local agents.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnsyncedMissingAgentFailsRun() {
        Config config = config(2, 2, false, name -> List.of(
                new AgentAttributeSet("lookup",
                        (List<Attribute>) (List<?>) List.of(new Reader("ghost")))
        ));

        Model model = new Model(config);

        ModelRunException exception = assertThrows(ModelRunException.class, model::run);

        Throwable rootCause = exception;
        while (rootCause.getCause() != null)
            rootCause = rootCause.getCause();

        assertInstanceOf(AgentNotFoundException.class, rootCause);
        assertTrue(rootCause.getMessage().contains("(threads are not synced)"),
                "Unsynchronised lookups of unknown agents should explain the thread mode.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSyncedMissingAgentIsReportedThroughCoordinator() {
        // A single worker keeps this deterministic: the failing worker is the only
        // worker, so nothing is left waiting at the tick barrier.
        Config config = config(1, 1, true, name -> List.of(
                new AgentAttributeSet("lookup",
                        (List<Attribute>) (List<?>) List.of(new Reader("ghost")))
        ));

        Model model = new Model(config);

        ModelRunException exception = assertThrows(ModelRunException.class, model::run);

        // The coordinator fails to resolve the agent, reports the error back to the
        // requesting worker, and the worker surfaces it as an AgentNotFoundException.
        assertInstanceOf(AgentNotFoundException.class, exception.getCause());
        assertInstanceOf(CoordinatorErrorException.class, exception.getCause().getCause());
        assertInstanceOf(AgentNotFoundException.class, exception.getCause().getCause().getCause());
    }
}
