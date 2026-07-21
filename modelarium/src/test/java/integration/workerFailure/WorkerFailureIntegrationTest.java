package integration.workerFailure;

import com.rits.cloning.Cloner;
import modelarium.Config;
import modelarium.Model;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.*;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.FunctionalEnvironmentGenerator;
import modelarium.exceptions.ModelRunException;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: when an agent throws during its tick, the failure must not
 * hang the simulation. Instead it should surface to the caller of {@link Model#run()}
 * as a {@link ModelRunException} whose cause chain still carries the original
 * exception. This is checked for a single-threaded run and for multithreaded runs
 * in both synchronisation modes.
 */
public class WorkerFailureIntegrationTest {

    @BeforeAll
    static void openForCloning() {
        WorkerFailureIntegrationTest.class.getModule().addOpens(
                "integration.workerFailure",
                Cloner.class.getModule()
        );
    }

    /** Throws on its third tick, but only for {@code agent_0}. */
    static class Bomb extends AgentProperty<Integer> {
        private int value = 0;

        Bomb() {
            super("bomb", true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(AgentContext context) {
            value++;
            if (value == 3 && context.getThisEntity().name().equals("agent_0"))
                throw new IllegalStateException("boom at tick 3");
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
    private Model model(int threads, boolean synced) {
        DefaultAgentGenerator agentGenerator = new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                String name = "agent_" + index++;
                AgentAttributeSet set = new AgentAttributeSet("danger",
                        (List<Attribute>) (List<?>) List.of(new Bomb()));
                return new Agent(name, List.of(set));
            }
        };

        Config config = Config.builder()
                .populationSize(4)
                .tickCount(10)
                .threadCount(threads)
                .areThreadsSynced(synced)
                .agentGenerator(agentGenerator)
                .environmentGenerator(new FunctionalEnvironmentGenerator((c, random) -> new Environment("env", List.of())))
                .scheduler(new InOrderScheduler())
                .threadTimeout(Duration.ofSeconds(5))
                .build();

        return new Model(config);
    }

    private void assertRunFailsWithBoom(Model model) {
        ModelRunException exception = assertThrows(ModelRunException.class, model::run,
                "A worker exception should surface as a ModelRunException.");

        Throwable rootCause = exception;
        while (rootCause.getCause() != null)
            rootCause = rootCause.getCause();

        assertInstanceOf(IllegalStateException.class, rootCause);
        assertEquals("boom at tick 3", rootCause.getMessage(),
                "The original failure should be preserved in the cause chain.");
    }

    @Test
    public void testUnsyncedMultiThreadedWorkerFailurePropagates() {
        assertRunFailsWithBoom(model(2, false));
    }

    @Test
    public void testSyncedSingleThreadedWorkerFailurePropagates() {
        assertRunFailsWithBoom(model(1, true));
    }

    @Test
    public void testSyncedMultiThreadedWorkerFailurePropagates() {
        assertRunFailsWithBoom(model(2, true));
    }
}
