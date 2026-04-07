package unit.modelarium.multithreading.requestresponse;

import modelarium.multithreading.requestresponse.Response;
import modelarium.multithreading.requestresponse.ResponseType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the {@link Response} class.
 *
 * <p>Verifies correct construction and field access behaviour, including cases with null payloads or destinations.
 */
public class ResponseTest {

    @Test
    public void testResponseFieldsAreSetCorrectly() {
        String requester = "Worker_1";
        String destination = "Coordinator";
        ResponseType type = ResponseType.AGENT_ACCESS;
        Object payload = "Agent_007";

        Response response = new Response(requester, destination, type, payload);

        assertEquals(requester, response.getRequester(), "Requester should match expected value");
        assertEquals(destination, response.getDestination(), "Destination should match expected value");
        assertEquals(type, response.getResponseType(), "Response type should match expected value");
        assertEquals(payload, response.getPayload(), "Payload should match expected value");
    }

    @Test
    public void testResponseWithNullPayload() {
        Response response = new Response("Coordinator", "Worker_2", ResponseType.ALL_WORKERS_UPDATE_COORDINATOR, null);

        assertEquals("Coordinator", response.getRequester());
        assertEquals("Worker_2", response.getDestination());
        assertEquals(ResponseType.ALL_WORKERS_UPDATE_COORDINATOR, response.getResponseType());
        assertNull(response.getPayload(), "Payload should be null when not provided");
    }

    @Test
    public void testResponseWithNullDestination() {
        Response response = new Response("Coordinator", null, ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS, "Some Data");

        assertEquals("Coordinator", response.getRequester());
        assertNull(response.getDestination(), "Destination should allow null if not relevant");
        assertEquals(ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS, response.getResponseType());
        assertEquals("Some Data", response.getPayload());
    }
}
