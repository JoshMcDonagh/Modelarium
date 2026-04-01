package unit.modelarium.multithreading.requestresponse;

import modelarium.ModelConfig;
import modelarium.agents.Agent;
import modelarium.agents.sets.AgentSet;
import modelarium.environments.Environment;
import modelarium.multithreading.requestresponse.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoordinatorRequestHandlerTest {

    private BlockingQueue<Response> responseQueue;
    private AgentSet agentSet;
    private Environment environment;
    private ModelConfig settings;
    private final String coordinatorThreadName = "Coordinator";

    @BeforeEach
    void setUp() {
        responseQueue = new LinkedBlockingQueue<>();
        agentSet = mock(AgentSet.class);
        environment = mock(Environment.class);
        settings = mock(ModelConfig.class);

        when(settings.getNumOfCores()).thenReturn(2);

        CoordinatorRequestHandler.initialise(
                coordinatorThreadName,
                settings,
                responseQueue,
                agentSet,
                environment
        );
    }

    @Test
    void testAgentAccessHandler() throws InterruptedException {
        Agent agent = mock(Agent.class);
        when(agentSet.get("Agent_1")).thenReturn(agent);

        Request request = new Request("Worker_1", "Agent_1", RequestType.AGENT_ACCESS, "Agent_1");
        CoordinatorRequestHandler.handleCoordinatorRequest(request);

        Response response = responseQueue.take();
        assertEquals(ResponseType.AGENT_ACCESS, response.getResponseType());
        assertEquals("Worker_1", response.getDestination());
        assertSame(agent, response.getPayload());
    }

    @Test
    void testUpdateCoordinatorAgentsHandler() throws InterruptedException {
        AgentSet updateSet = mock(AgentSet.class);
        Request request = new Request("Worker_1", null, RequestType.UPDATE_COORDINATOR_AGENTS, updateSet);

        CoordinatorRequestHandler.handleCoordinatorRequest(request);

        verify(agentSet).update(updateSet);
    }

    @Test
    void testFilteredAgentsAccessHandler() throws InterruptedException {
        Predicate<Agent> predicate = agent -> true;
        AgentSet filteredSet = mock(AgentSet.class);
        when(agentSet.getFilteredAgents(predicate)).thenReturn(filteredSet);

        Request request = new Request("Worker_1", null, RequestType.FILTERED_AGENTS_ACCESS, predicate);
        CoordinatorRequestHandler.handleCoordinatorRequest(request);

        Response response = responseQueue.take();
        assertEquals(ResponseType.FILTERED_AGENTS_ACCESS, response.getResponseType());
        assertEquals(filteredSet, response.getPayload());
    }

    @Test
    void testEnvironmentAttributesAccessHandler() throws InterruptedException {
        Request request = new Request("Worker_1", null, RequestType.ENVIRONMENT_ATTRIBUTES_ACCESS, null);
        CoordinatorRequestHandler.handleCoordinatorRequest(request);

        Response response = responseQueue.take();
        assertEquals(ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS, response.getResponseType());
        assertEquals(environment, response.getPayload());
    }

    @Test
    void testAllWorkersFinishTickSynchronisation() throws InterruptedException {
        Request request1 = new Request("Worker_1", null, RequestType.ALL_WORKERS_FINISH_TICK, null);
        Request request2 = new Request("Worker_2", null, RequestType.ALL_WORKERS_FINISH_TICK, null);

        CoordinatorRequestHandler.handleCoordinatorRequest(request1);
        CoordinatorRequestHandler.handleCoordinatorRequest(request2);

        assertEquals(2, responseQueue.size());
        assertEquals(ResponseType.ALL_WORKERS_FINISH_TICK, responseQueue.take().getResponseType());
        assertEquals(ResponseType.ALL_WORKERS_FINISH_TICK, responseQueue.take().getResponseType());
    }

    @Test
    void testAllWorkersUpdateCoordinatorSynchronisation() throws InterruptedException {
        Request request1 = new Request("Worker_1", null, RequestType.ALL_WORKERS_UPDATE_COORDINATOR, null);
        Request request2 = new Request("Worker_2", null, RequestType.ALL_WORKERS_UPDATE_COORDINATOR, null);

        CoordinatorRequestHandler.handleCoordinatorRequest(request1);
        CoordinatorRequestHandler.handleCoordinatorRequest(request2);

        assertEquals(2, responseQueue.size());
        assertEquals(ResponseType.ALL_WORKERS_UPDATE_COORDINATOR, responseQueue.take().getResponseType());
        assertEquals(ResponseType.ALL_WORKERS_UPDATE_COORDINATOR, responseQueue.take().getResponseType());
        verify(environment).run();
    }
}
