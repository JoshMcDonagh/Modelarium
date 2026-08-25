package integration.tickSnapshot;

import modelarium.Config;
import modelarium.Model;
import modelarium.entities.Agent;
import modelarium.entities.Environment;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.generators.AgentGenerator;
import modelarium.entities.generators.FunctionalEnvironmentGenerator;
import modelarium.entities.readonly.ReadOnlyAgent;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration coverage for the tick-boundary snapshot semantics used by workers.
 */
public class TickSnapshotIntegrationTest {

    private static AgentAttributeSet attributeSet(String name, Attribute... attributes) {
        return new AgentAttributeSet(name, List.of(attributes));
    }

    private static AgentGenerator fixedAgents(List<Agent> agents) {
        return new AgentGenerator() {
            @Override
            public AgentSet generateAgents(Config config, RandomGenerator random) {
                return new AgentSet(agents);
            }

            @Override
            public List<AgentSet> getAgentsForEachCore(Config config, RandomGenerator random) {
                List<AgentSet> result = new ArrayList<>();
                for (int i = 0; i < config.threadCount(); i++)
                    result.add(new AgentSet());

                for (int i = 0; i < agents.size(); i++)
                    result.get(i % config.threadCount()).add(agents.get(i));

                return result;
            }
        };
    }

    private static Config config(List<Agent> agents, int ticks, int threads, boolean synced) {
        return Config.builder()
                .populationSize(agents.size())
                .tickCount(ticks)
                .threadCount(threads)
                .areThreadsSynced(synced)
                .agentGenerator(fixedAgents(agents))
                .environmentGenerator(new FunctionalEnvironmentGenerator((c, random) -> new Environment(List.of())))
                .scheduler(new InOrderScheduler())
                .seed(1234L)
                .build();
    }

    private static class IncrementingValue extends AgentProperty<Integer> {
        private int value = 0;

        IncrementingValue() {
            super("value", false, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(AgentContext context) {
            value++;
        }

        @Override
        protected void set(AgentContext context, Integer value) {
            this.value = value;
        }

        @Override
        protected Integer get(AgentContext context) {
            return value;
        }
    }

    private static class ObserveAgentValue extends AgentProperty<Integer> {
        private final String targetName;
        private final List<Integer> observed = new ArrayList<>();

        ObserveAgentValue(String targetName) {
            super("observed", false, AttributeAccessLevel.PUBLIC, Integer.class);
            this.targetName = targetName;
        }

        @Override
        protected void run(AgentContext context) {
            ReadOnlyAgent target = context.getAgent(targetName);
            observed.add((Integer) target.getProperty("state", "value").get());
        }

        @Override
        protected void set(AgentContext context, Integer value) { }

        @Override
        protected Integer get(AgentContext context) {
            return observed.isEmpty() ? null : observed.get(observed.size() - 1);
        }

        List<Integer> observations() {
            return List.copyOf(observed);
        }
    }

    private static class RunCounter extends AgentProperty<Integer> {
        private int runs = 0;

        RunCounter() {
            super("runs", false, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(AgentContext context) {
            runs++;
        }

        @Override
        protected void set(AgentContext context, Integer value) {
            runs = value;
        }

        @Override
        protected Integer get(AgentContext context) {
            return runs;
        }

        int runs() {
            return runs;
        }
    }

    private static class LoggedRunCounter extends AgentProperty<Integer> {
        private int runs = 0;

        LoggedRunCounter() {
            super("runs", true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(AgentContext context) {
            runs++;
        }

        @Override
        protected void set(AgentContext context, Integer value) {
            runs = value;
        }

        @Override
        protected Integer get(AgentContext context) {
            return runs;
        }
    }

    private static class AddOnce extends AgentProperty<Boolean> {
        private final Agent agentToAdd;
        private boolean added = false;

        AddOnce(Agent agentToAdd) {
            super("addOnce", false, AttributeAccessLevel.PRIVATE, Boolean.class);
            this.agentToAdd = agentToAdd;
        }

        @Override
        protected void run(AgentContext context) {
            if (!added) {
                context.addAgent(agentToAdd);
                added = true;
            }
        }

        @Override
        protected void set(AgentContext context, Boolean value) {
            added = value;
        }

        @Override
        protected Boolean get(AgentContext context) {
            return added;
        }
    }

    private static class VisibilityRecorder extends AgentProperty<Boolean> {
        private final String targetName;
        private final List<Boolean> observations = new ArrayList<>();

        VisibilityRecorder(String targetName) {
            super("visible", false, AttributeAccessLevel.PRIVATE, Boolean.class);
            this.targetName = targetName;
        }

        @Override
        protected void run(AgentContext context) {
            observations.add(context.doesAgentExistInThisCore(targetName));
        }

        @Override
        protected void set(AgentContext context, Boolean value) { }

        @Override
        protected Boolean get(AgentContext context) {
            return observations.isEmpty() ? null : observations.get(observations.size() - 1);
        }

        List<Boolean> observations() {
            return List.copyOf(observations);
        }
    }

    private static class KillOnce extends AgentProperty<Boolean> {
        private final String targetName;
        private boolean killed = false;

        KillOnce(String targetName) {
            super("killOnce", false, AttributeAccessLevel.PRIVATE, Boolean.class);
            this.targetName = targetName;
        }

        @Override
        protected void run(AgentContext context) {
            if (!killed) {
                context.killAgent(targetName);
                killed = true;
            }
        }

        @Override
        protected void set(AgentContext context, Boolean value) {
            killed = value;
        }

        @Override
        protected Boolean get(AgentContext context) {
            return killed;
        }
    }

    @Test
    public void testUnsyncedSameWorkerReadSeesEndOfPreviousTickState() {
        IncrementingValue value = new IncrementingValue();
        ObserveAgentValue observer = new ObserveAgentValue("agent_0");

        Agent agent0 = new Agent("agent_0", List.of(attributeSet("state", value)));
        Agent agent1 = new Agent("agent_1", List.of(attributeSet("observer", observer)));

        new Model(config(List.of(agent0, agent1), 3, 1, false)).run();

        assertEquals(List.of(0, 1, 2), observer.observations());
    }

    @Test
    public void testSyncedCrossWorkerReadSeesEndOfPreviousTickState() {
        IncrementingValue value = new IncrementingValue();
        ObserveAgentValue observer = new ObserveAgentValue("agent_0");

        Agent agent0 = new Agent("agent_0", List.of(attributeSet("state", value)));
        Agent agent1 = new Agent("agent_1", List.of(attributeSet("observer", observer)));

        new Model(config(List.of(agent0, agent1), 3, 2, true)).run();

        assertEquals(List.of(0, 1, 2), observer.observations());
    }

    @Test
    public void testAddedAgentIsInvisibleUntilNextTickAndStartsWithContext() {
        RunCounter addedCounter = new RunCounter();
        Agent addedAgent = new Agent("added", List.of(attributeSet("state", addedCounter)));
        VisibilityRecorder visibility = new VisibilityRecorder("added");
        AddOnce addOnce = new AddOnce(addedAgent);

        Agent existing = new Agent(
                "existing",
                List.of(attributeSet("control", addOnce, visibility))
        );

        new Model(config(List.of(existing), 3, 1, false)).run();

        assertEquals(List.of(false, true, true), visibility.observations());
        assertEquals(2, addedCounter.runs(), "An agent added on tick 0 should first run on tick 1.");
        assertNotNull(addedAgent.context(), "A dynamically added agent must receive a context before its first run.");
    }

    @Test
    public void testAddedAgentWithLoggedPropertyProducesResults() {
        LoggedRunCounter counter = new LoggedRunCounter();
        Agent addedAgent = new Agent("added", List.of(attributeSet("state", counter)));
        AddOnce addOnce = new AddOnce(addedAgent);
        Agent existing = new Agent("existing", List.of(attributeSet("control", addOnce)));

        Model model = new Model(config(List.of(existing), 3, 1, false));
        model.run();

        assertEquals(
                List.of(1, 2),
                model.getResults().agents().attributeLogs("added", "state", "runs", Integer.class),
                "A dynamically added agent should have its log database initialised before its first run."
        );
    }

    @Test
    public void testUnsyncedKillTakesEffectAfterCurrentTick() {
        KillOnce killOnce = new KillOnce("victim");
        RunCounter victimCounter = new RunCounter();

        Agent killer = new Agent("killer", List.of(attributeSet("control", killOnce)));
        Agent victim = new Agent("victim", List.of(attributeSet("state", victimCounter)));

        new Model(config(List.of(killer, victim), 3, 1, false)).run();

        assertEquals(1, victimCounter.runs(), "The victim should still run on the tick in which its death is requested.");
        assertTrue(victim.isDead());
    }

    @Test
    public void testSyncedCrossWorkerKillReachesOwningWorkerBeforeNextTick() {
        KillOnce killOnce = new KillOnce("victim");
        RunCounter victimCounter = new RunCounter();

        // Round-robin distribution puts killer on worker 0 and victim on worker 1.
        Agent killer = new Agent("killer", List.of(attributeSet("control", killOnce)));
        Agent victim = new Agent("victim", List.of(attributeSet("state", victimCounter)));

        new Model(config(List.of(killer, victim), 3, 2, true)).run();

        assertEquals(1, victimCounter.runs(), "A remotely killed agent must not run again on the next tick.");
        assertTrue(victim.isDead(), "The owning worker should reconcile the coordinator's resolved death state.");
    }
}
