package unit.modelarium.multithreading.requestresponse;

import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.environments.Environment;
import modelarium.multithreading.requestresponse.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestResponseInterfaceTest {

    private RequestResponseInterface requestResponseInterface;
    private BlockingQueue<Request> requestQueue;
    private BlockingQueue<Response> responseQueue;

    private final String threadName = "Worker_1";

    @BeforeEach
    void setUp() {
        Config settings = mock(Config.class);
        when(settings.getAreProcessesSynced()).thenReturn(true);

        requestQueue = new ArrayBlockingQueue<>(10);
        responseQueue = new ArrayBlockingQueue<>(10);

        RequestResponseController controller = mock(RequestResponseController.class);
        when(controller.getRequestQueue()).thenReturn(requestQueue);
        when(controller.getResponseQueue()).thenReturn(responseQueue);

        requestResponseInterface = new RequestResponseInterface(threadName, settings, controller);
    }

    @Test
    void testWaitUntilAllWorkersFinishTick() throws InterruptedException {
        Response response = new Response("Coordinator", threadName, ResponseType.ALL_WORKERS_FINISH_TICK, null);
        responseQueue.put(response);

        requestResponseInterface.waitUntilAllWorkersFinishTick();

        Request request = requestQueue.take();
        assertEquals(RequestType.ALL_WORKERS_FINISH_TICK, request.getRequestType(), "Should send a synchronisation request to finish tick.");
    }

    @Test
    void testGetAgentFromCoordinatorReturnsCorrectAgent() throws InterruptedException {
        Agent mockAgent = mock(Agent.class);
        responseQueue.put(new Response("Coordinator", threadName, ResponseType.AGENT_ACCESS, mockAgent));

        Agent result = requestResponseInterface.getAgentFromCoordinator(threadName, "Target_Agent");

        Request request = requestQueue.take();
        assertEquals(RequestType.AGENT_ACCESS, request.getRequestType());
        assertEquals("Target_Agent", request.getDestination());
        assertSame(mockAgent, result, "Should return the agent provided in the coordinator's response.");
    }

    @Test
    void testGetFilteredAgentsFromCoordinatorReturnsCorrectSet() throws InterruptedException {
        AgentSet mockSet = mock(AgentSet.class);
        Predicate<Agent> filter = agent -> true;

        responseQueue.put(new Response("Coordinator", threadName, ResponseType.FILTERED_AGENTS_ACCESS, mockSet));

        AgentSet result = requestResponseInterface.getFilteredAgentsFromCoordinator(threadName, filter);

        Request request = requestQueue.take();
        assertEquals(RequestType.FILTERED_AGENTS_ACCESS, request.getRequestType());
        assertSame(mockSet, result);
    }

    @Test
    void testGetEnvironmentFromCoordinatorReturnsCorrectEnvironment() throws InterruptedException {
        Environment mockEnv = mock(Environment.class);
        responseQueue.put(new Response("Coordinator", threadName, ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS, mockEnv));

        Environment result = requestResponseInterface.getEnvironmentFromCoordinator(threadName);

        Request request = requestQueue.take();
        assertEquals(RequestType.ENVIRONMENT_ATTRIBUTES_ACCESS, request.getRequestType());
        assertSame(mockEnv, result);
    }

    @Test
    void testUpdateCoordinatorAgentsPutsRequest() throws InterruptedException {
        AgentSet agentSet = new AgentSet();

        requestResponseInterface.updateCoordinatorAgents(agentSet);

        Request request = requestQueue.take();
        assertEquals(RequestType.UPDATE_COORDINATOR_AGENTS, request.getRequestType());
        assertSame(agentSet, request.getPayload());
    }
}
