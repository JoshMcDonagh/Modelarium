package unit.modelarium.multithreading.requestresponse;

import helpers.TestFixtures;
import modelarium.Config;
import modelarium.multithreading.requestresponse.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RequestResponseController}.
 */
public class RequestResponseControllerTest {

    private RequestResponseController controller;

    @BeforeEach
    void setUp() {
        Config config = TestFixtures.syncedConfig(2, 5, 2);
        controller = new RequestResponseController(config);
    }

    @Test
    void testRequestQueueIsNonNull() {
        assertNotNull(controller.getRequestQueue());
    }

    @Test
    void testResponseQueueCreatedPerThread() {
        BlockingQueue<Response> q1 = controller.getResponseQueue("thread_0");
        BlockingQueue<Response> q2 = controller.getResponseQueue("thread_1");

        assertNotNull(q1);
        assertNotNull(q2);
        assertNotSame(q1, q2, "Each thread should get its own response queue.");
    }

    @Test
    void testResponseQueueIsStableForSameName() {
        BlockingQueue<Response> first = controller.getResponseQueue("worker");
        BlockingQueue<Response> second = controller.getResponseQueue("worker");

        assertSame(first, second, "Same name should return the same queue instance.");
    }

    @Test
    void testGetInterfaceReturnsNonNull() {
        RequestResponseInterface iface = controller.getInterface("worker_0");
        assertNotNull(iface);
    }

    @Test
    void testRequestQueueCanAcceptMessages() throws InterruptedException {
        Request req = new Request("w", "c", RequestType.ALL_WORKERS_FINISH_TICK, null);
        controller.getRequestQueue().put(req);

        assertEquals(1, controller.getRequestQueue().size());
        assertSame(req, controller.getRequestQueue().take());
    }
}
