package unit.modelarium.multithreading.requestresponse;

import modelarium.Config;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.generators.EnvironmentGenerator;
import modelarium.exceptions.CoordinatorErrorException;
import modelarium.exceptions.CoordinatorTimeoutException;
import modelarium.multithreading.requestresponse.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

public class RequestResponseInterfaceTest {

    @Test
    public void testWaitUntilAllWorkersFinishTick_Unsynced_DoesNothing() throws InterruptedException {
        Config config = config(false, Duration.ofMillis(50));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");

        requestResponseInterface.waitUntilAllWorkersFinishTick();

        assertTrue(controller.getRequestQueue().isEmpty());
    }

    @Test
    public void testWaitUntilAllWorkersFinishTick_Synced_SendsRequestAndAcceptsResponse() throws InterruptedException {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");
        controller.getResponseQueue("worker").put(new Response(
                "coordinator",
                "worker",
                ResponseType.ALL_WORKERS_FINISH_TICK,
                null
        ));

        requestResponseInterface.waitUntilAllWorkersFinishTick();

        Request request = controller.getRequestQueue().take();
        assertEquals("worker", request.getRequester());
        assertEquals(RequestType.ALL_WORKERS_FINISH_TICK, request.getRequestType());
    }

    @Test
    public void testWaitUntilAllWorkersUpdateCoordinator_Synced_SendsCorrectRequest() throws InterruptedException {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");
        controller.getResponseQueue("worker").put(new Response(
                "coordinator",
                "worker",
                ResponseType.ALL_WORKERS_UPDATE_COORDINATOR,
                null
        ));

        requestResponseInterface.waitUntilAllWorkersUpdateCoordinator();

        assertEquals(
                RequestType.ALL_WORKERS_UPDATE_COORDINATOR,
                controller.getRequestQueue().take().getRequestType()
        );
    }

    @Test
    public void testGetAgentFromCoordinator_ReturnsPayloadAndBuildsCorrectRequest() throws InterruptedException {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");
        ReadOnlyAgent expected = new Agent("target", List.of()).getAsImmutable();
        controller.getResponseQueue("worker").put(new Response(
                "coordinator",
                "worker",
                ResponseType.AGENT_ACCESS,
                expected
        ));

        ReadOnlyAgent actual = requestResponseInterface.getAgentFromCoordinator("worker", "target");

        assertSame(expected, actual);
        Request request = controller.getRequestQueue().take();
        assertEquals("worker", request.getRequester());
        assertEquals(RequestType.AGENT_ACCESS, request.getRequestType());
        assertEquals("target", request.getPayload());
    }

    @Test
    public void testGetAgentFromCoordinator_ErrorResponse_ThrowsCoordinatorErrorException() throws InterruptedException {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");
        IllegalStateException cause = new IllegalStateException("boom");
        controller.getResponseQueue("worker").put(new Response(
                "coordinator",
                "worker",
                ResponseType.ERROR,
                cause
        ));

        CoordinatorErrorException exception = assertThrows(
                CoordinatorErrorException.class,
                () -> requestResponseInterface.getAgentFromCoordinator("worker", "target")
        );

        assertSame(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("AGENT_ACCESS"));
    }

    @Test
    public void testGetAgentFromCoordinator_NoResponse_ThrowsCoordinatorTimeoutException() {
        Config config = config(true, Duration.ofMillis(10));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");

        CoordinatorTimeoutException exception = assertThrows(
                CoordinatorTimeoutException.class,
                () -> requestResponseInterface.getAgentFromCoordinator("worker", "target")
        );

        assertTrue(exception.getMessage().contains("Timed out waiting for AGENT_ACCESS"));
    }

    @Test
    public void testGetAgentFromCoordinator_ResponseForDifferentDestination_IsRequeued() throws InterruptedException {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");
        BlockingQueue<Response> queue = controller.getResponseQueue("worker");
        Response unrelated = new Response("coordinator", "someone_else", ResponseType.AGENT_ACCESS, null);
        ReadOnlyAgent expected = new Agent("target", List.of()).getAsImmutable();
        queue.put(unrelated);
        queue.put(new Response("coordinator", "worker", ResponseType.AGENT_ACCESS, expected));

        ReadOnlyAgent actual = requestResponseInterface.getAgentFromCoordinator("worker", "target");

        assertSame(expected, actual);
        assertSame(unrelated, queue.take());
    }

    @Test
    public void testGetAgentFromCoordinator_UnrelatedResponseType_IsRequeued() throws InterruptedException {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");
        BlockingQueue<Response> queue = controller.getResponseQueue("worker");
        Response unrelated = new Response(
                "coordinator",
                "worker",
                ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS,
                null
        );
        ReadOnlyAgent expected = new Agent("target", List.of()).getAsImmutable();
        queue.put(unrelated);
        queue.put(new Response("coordinator", "worker", ResponseType.AGENT_ACCESS, expected));

        ReadOnlyAgent actual = requestResponseInterface.getAgentFromCoordinator("worker", "target");

        assertSame(expected, actual);
        assertSame(unrelated, queue.take());
    }


    @Test
    public void testGetFilteredAgentsFromCoordinator_ReturnsPayloadAndBuildsCorrectRequest() throws InterruptedException {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");
        AgentSet agents = new AgentSet(List.of(new Agent("A", List.of())));
        var expected = agents.getAsImmutable();
        java.util.function.Predicate<ReadOnlyAgent> filter = agent -> true;
        controller.getResponseQueue("worker").put(new Response(
                "coordinator",
                "worker",
                ResponseType.FILTERED_AGENTS_ACCESS,
                expected
        ));

        var actual = requestResponseInterface.getFilteredAgentsFromCoordinator("worker", filter);

        assertSame(expected, actual);
        Request request = controller.getRequestQueue().take();
        assertEquals(RequestType.FILTERED_AGENTS_ACCESS, request.getRequestType());
        assertSame(filter, request.getPayload());
    }

    @Test
    public void testGetEnvironmentFromCoordinator_ReturnsPayloadAndBuildsCorrectRequest() throws InterruptedException {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");
        var expected = new Environment("environment", List.of()).getAsImmutable();
        controller.getResponseQueue("worker").put(new Response(
                "coordinator",
                "worker",
                ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS,
                expected
        ));

        var actual = requestResponseInterface.getEnvironmentFromCoordinator("worker");

        assertSame(expected, actual);
        Request request = controller.getRequestQueue().take();
        assertEquals(RequestType.ENVIRONMENT_ATTRIBUTES_ACCESS, request.getRequestType());
        assertNull(request.getPayload());
    }

    @Test
    public void testUpdateCoordinatorAgents_QueuesUpdateRequest() throws InterruptedException {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");
        AgentSet agentSet = new AgentSet(List.of(new Agent("A", List.of())));

        requestResponseInterface.updateCoordinatorAgents(agentSet);

        Request request = controller.getRequestQueue().take();
        assertEquals(RequestType.UPDATE_COORDINATOR_AGENTS, request.getRequestType());
        assertSame(agentSet, request.getPayload());
    }

    @Test
    public void testUpdateCoordinatorAgents_NullAgentSet_NullPointerException() {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");

        assertThrows(NullPointerException.class, () -> requestResponseInterface.updateCoordinatorAgents(null));
    }

    @Test
    public void testKillCoordinatorAgent_QueuesKillRequest() throws InterruptedException {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");

        requestResponseInterface.killCoordinatorAgent("A");

        Request request = controller.getRequestQueue().take();
        assertEquals(RequestType.KILL_AGENT, request.getRequestType());
        assertEquals("A", request.getPayload());
    }

    @Test
    public void testKillCoordinatorAgents_QueuesKillRequest() throws InterruptedException {
        Config config = config(true, Duration.ofSeconds(1));
        RequestResponseController controller = new RequestResponseController(config);
        RequestResponseInterface requestResponseInterface = controller.getInterface("worker");
        List<String> names = List.of("A", "B");

        requestResponseInterface.killCoordinatorAgents(names);

        Request request = controller.getRequestQueue().take();
        assertEquals(RequestType.KILL_AGENTS, request.getRequestType());
        assertSame(names, request.getPayload());
    }

    private static Config config(boolean synced, Duration timeout) {
        return Config.builder()
                .populationSize(2)
                .tickCount(5)
                .threadCount(2)
                .threadTimeout(timeout)
                .areThreadsSynced(synced)
                .agentGenerator(new DefaultAgentGenerator() {
                    @Override
                    protected Agent generateAgent(Config config, RandomGenerator random) {
                        return new Agent("generated", List.of());
                    }
                })
                .environmentGenerator(new EnvironmentGenerator() {
                    @Override
                    public Environment generateEnvironment(Config config, RandomGenerator random) {
                        return new Environment("environment", List.of());
                    }
                })
                .build();
    }
}
