package unit.modelarium.multithreading;

import modelarium.Config;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.immutable.ReadOnlyAgentSet;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.CoordinatorThread;
import modelarium.multithreading.requestresponse.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CoordinatorThreadTest {

    @Test
    public void testAgentSetAccessRequest_IsHandledByGlobalAgentSetHandler() throws InterruptedException {
        Config config = mock(Config.class);
        Environment environment = mock(Environment.class);
        RequestResponseController controller = new RequestResponseController(config);
        Agent alive = new Agent("alive", List.of());
        Agent dead = new Agent("dead", List.of());
        dead.kill();
        AgentSet globalAgentSet = new AgentSet(List.of(alive, dead));
        CoordinatorThread coordinator = new CoordinatorThread(
                "coordinator",
                config,
                environment,
                controller,
                new MutableClock(10),
                globalAgentSet,
                Map.of()
        );
        Thread coordinatorJavaThread = new Thread(coordinator);
        coordinatorJavaThread.start();

        try {
            controller.getRequestQueue().put(
                    new Request("worker", "coordinator", RequestType.AGENT_SET_ACCESS, null)
            );

            Response response = controller.getResponseQueue("worker").poll(1, TimeUnit.SECONDS);

            assertNotNull(response, "The coordinator should answer AGENT_SET_ACCESS rather than leaving the worker blocked.");
            assertEquals(ResponseType.AGENT_SET_ACCESS, response.getResponseType());
            assertInstanceOf(ReadOnlyAgentSet.class, response.getPayload());
            ReadOnlyAgentSet returned = (ReadOnlyAgentSet) response.getPayload();
            assertSame(globalAgentSet.getAsImmutable(), returned);
            assertEquals(2, returned.size());
            assertTrue(returned.get("dead").isDead());
        } finally {
            coordinator.shutdown();
            coordinatorJavaThread.join(1_000);
            assertFalse(coordinatorJavaThread.isAlive(), "Coordinator thread should shut down cleanly after the test.");
        }
    }
}
