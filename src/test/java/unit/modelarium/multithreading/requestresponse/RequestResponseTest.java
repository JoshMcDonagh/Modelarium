package unit.modelarium.multithreading.requestresponse;

import modelarium.entities.agents.AgentSet;
import modelarium.multithreading.requestresponse.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Request} and {@link Response}.
 */
public class RequestResponseTest {

    // ---- Request ----

    @Test
    void request_fieldsAreAssignedCorrectly() {
        Request req = new Request("worker_0", "coordinator", RequestType.AGENT_ACCESS, "agentA");

        assertEquals("worker_0", req.getRequester());
        assertEquals("coordinator", req.getDestination());
        assertEquals(RequestType.AGENT_ACCESS, req.getRequestType());
        assertEquals("agentA", req.getPayload());
    }

    @Test
    void request_allowsNullPayloadForNonUpdateRequests() {
        assertDoesNotThrow(() ->
                new Request("w", "c", RequestType.ALL_WORKERS_FINISH_TICK, null));
    }

    @Test
    void request_rejectsNonAgentSetPayloadForUpdateCoordinatorAgents() {
        assertThrows(IllegalArgumentException.class, () ->
                new Request("w", "c", RequestType.UPDATE_COORDINATOR_AGENTS, "not an agent set"),
                "UPDATE_COORDINATOR_AGENTS requires an AgentSet payload.");
    }

    @Test
    void request_acceptsAgentSetPayloadForUpdateCoordinatorAgents() {
        AgentSet set = new AgentSet();
        assertDoesNotThrow(() ->
                new Request("w", "c", RequestType.UPDATE_COORDINATOR_AGENTS, set));
    }

    // ---- Response ----

    @Test
    void response_fieldsAreAssignedCorrectly() {
        Response resp = new Response("coordinator", "worker_0", ResponseType.AGENT_ACCESS, "payload");

        assertEquals("coordinator", resp.getRequester());
        assertEquals("worker_0", resp.getDestination());
        assertEquals(ResponseType.AGENT_ACCESS, resp.getResponseType());
        assertEquals("payload", resp.getPayload());
    }

    @Test
    void response_allowsNullPayload() {
        Response resp = new Response("c", "w", ResponseType.ALL_WORKERS_FINISH_TICK, null);
        assertNull(resp.getPayload());
    }

    // ---- RequestType and ResponseType coverage ----

    @Test
    void requestType_allValuesExist() {
        // Guard against accidental removal of enum variants
        assertEquals(7, RequestType.values().length, "Expected 7 request types.");
    }

    @Test
    void responseType_allValuesExist() {
        assertEquals(6, ResponseType.values().length, "Expected 6 response types.");
    }
}
