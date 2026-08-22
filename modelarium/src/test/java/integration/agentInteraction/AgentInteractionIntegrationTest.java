package integration.agentInteraction;

import com.rits.cloning.Cloner;
import modelarium.Config;
import modelarium.Model;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.generators.FunctionalEnvironmentGenerator;
import modelarium.exceptions.AgentNotFoundException;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.ModelRunException;
import modelarium.results.immutable.ReadOnlyResults;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.BeforeAll;
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

    /** The first tick on which every core's agents are guaranteed to be visible model-wide. */
    private static final long FIRST_STABLE_TICK = 2;

    /** Logged on the ticks before a cross-core read is attempted. */
    private static final double NOT_YET_READ = -1.0;

    @BeforeAll
    static void openForCloning() {
        AgentInteractionIntegrationTest.class.getModule().addOpens(
                "integration.agentInteraction",
                Cloner.class.getModule()
        );
    }

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
            // The coordinator's global agent set is populated by each worker's initial
            // (fire-and-forget) broadcast, and workers are not held at a barrier until
            // those broadcasts have been processed. A remote read on the very first tick
            // can therefore race that broadcast, so cross-core reads only begin once the
            // population is guaranteed to be visible.
            if (context.getClock().currentTick() < FIRST_STABLE_TICK) {
                observed = NOT_YET_READ;
                return;
            }

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
        List<MutableAgentAttributeSet> attributeSetsFor(String agentName);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSyncedAgentReadsAnotherCoresAgentProperty() {
        Config config = config(2, 2, true, name -> List.of(
                new MutableAgentAttributeSet("value",
                        (List<Attribute>) (List<?>) List.of(new ConstantValue())),
                new MutableAgentAttributeSet("observation",
                        (List<Attribute>) (List<?>) List.of(new Observer()))
        ));

        Model model = new Model(config);
        model.run();

        ReadOnlyResults results = model.getResults();
        List<Double> observed = results.agents().attributeLogs("agent_1", "observation", "observed", Double.class);

        assertEquals(10, observed.size(), "One observation should be logged per tick.");

        // agent_1 lives on a different worker from agent_0 (agents are distributed
        // round-robin), so every one of these reads goes through the coordinator.
        for (int tick = (int) FIRST_STABLE_TICK; tick < observed.size(); tick++)
            assertEquals(REMOTE_VALUE, observed.get(tick), 1e-9,
                    "agent_1 should read agent_0's value across cores.");

        for (int tick = 0; tick < FIRST_STABLE_TICK; tick++)
            assertEquals(NOT_YET_READ, observed.get(tick), 1e-9,
                    "Ticks before the population is model-wide visible are deliberately not read.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSyncedFilteredAgentsSeeWholePopulation() {
        int population = 6;
        Config config = config(population, 3, true, name -> List.of(
                new MutableAgentAttributeSet("census",
                        (List<Attribute>) (List<?>) List.of(new PopulationCounter()))
        ));

        Model model = new Model(config);
        model.run();

        ReadOnlyResults results = model.getResults();
        List<Integer> counts = results.agents().attributeLogs("agent_0", "census", "count", Integer.class);

        assertEquals(10, counts.size(), "One population count should be logged per tick.");
        // Once every worker's initial broadcast has been processed, a synchronised
        // filter is answered from the coordinator's global agent set and therefore
        // sees the entire population, not just this core's share.
        for (int tick = (int) FIRST_STABLE_TICK; tick < counts.size(); tick++)
            assertEquals(population, counts.get(tick),
                    "A synchronised filter should see the whole population.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnsyncedFilteredAgentsAreLocalOnly() {
        Config config = config(4, 2, false, name -> List.of(
                new MutableAgentAttributeSet("census",
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
                new MutableAgentAttributeSet("lookup",
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
                new MutableAgentAttributeSet("lookup",
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
