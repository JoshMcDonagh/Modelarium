package unit.modelarium.multithreading.requestresponse;

import modelarium.entities.agentsets.AgentSet;
import modelarium.multithreading.requestresponse.Request;
import modelarium.multithreading.requestresponse.RequestType;
import modelarium.multithreading.requestresponse.Response;
import modelarium.multithreading.requestresponse.ResponseType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestResponseTest {
    @Test
    public void testRequestConstructor() {
        Request request = new Request("worker_0", "coordinator", RequestType.AGENT_ACCESS, "agentA");

        assertEquals("worker_0", request.getRequester());
        assertEquals("coordinator", request.getDestination());
        assertEquals(RequestType.AGENT_ACCESS, request.getRequestType());
        assertEquals("agentA", request.getPayload());
    }

    @Test
    public void testRequestConstructor_NullPayload() {
        assertDoesNotThrow(() -> new Request("w", "c", RequestType.ALL_WORKERS_FINISH_TICK, null));
    }

    @Test
    public void testRequestConstructor_UpdateCoordinatorAgentsWithAgentSetPayload() {
        AgentSet agentSet = new AgentSet();

        assertDoesNotThrow(() -> new Request("w", "c", RequestType.UPDATE_COORDINATOR_AGENTS, agentSet));
    }

    @Test
    public void testRequestConstructor_UpdateCoordinatorAgentsWithInvalidPayload_IllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Request("w", "c", RequestType.UPDATE_COORDINATOR_AGENTS, "not an agent set")
        );
    }

    @Test
    public void testResponseConstructor() {
        Response response = new Response("coordinator", "worker_0", ResponseType.AGENT_ACCESS, "payload");

        assertEquals("coordinator", response.getRequester());
        assertEquals("worker_0", response.getDestination());
        assertEquals(ResponseType.AGENT_ACCESS, response.getResponseType());
        assertEquals("payload", response.getPayload());
    }

    @Test
    public void testResponseConstructor_NullPayload() {
        Response response = new Response("c", "w", ResponseType.ALL_WORKERS_FINISH_TICK, null);

        assertNull(response.getPayload());
    }

    @Test
    public void testRequestTypeValues() {
        assertEquals(10, RequestType.values().length);
    }

    @Test
    public void testResponseTypeValues() {
        assertEquals(7, ResponseType.values().length);
    }
}
