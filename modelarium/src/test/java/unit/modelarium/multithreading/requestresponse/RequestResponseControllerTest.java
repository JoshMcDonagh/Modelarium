package unit.modelarium.multithreading.requestresponse;

import modelarium.multithreading.requestresponse.Request;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.multithreading.requestresponse.RequestType;
import modelarium.multithreading.requestresponse.Response;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.multithreading.requestresponse.RequestResponseTestHelpers.requestResponseController;

public class RequestResponseControllerTest {
    @Test
    public void testGetRequestQueue() {
        RequestResponseController controller = requestResponseController();

        assertNotNull(controller.getRequestQueue());
    }

    @Test
    public void testGetRequestQueue_AcceptsRequests() throws InterruptedException {
        RequestResponseController controller = requestResponseController();
        Request request = new Request("w", "c", RequestType.ALL_WORKERS_FINISH_TICK, null);

        controller.getRequestQueue().put(request);

        assertEquals(1, controller.getRequestQueue().size());
        assertSame(request, controller.getRequestQueue().take());
    }

    @Test
    public void testGetResponseQueue_SeparateQueuePerThread() {
        RequestResponseController controller = requestResponseController();

        BlockingQueue<Response> firstResponseQueue = controller.getResponseQueue("thread_0");
        BlockingQueue<Response> secondResponseQueue = controller.getResponseQueue("thread_1");

        assertNotNull(firstResponseQueue);
        assertNotNull(secondResponseQueue);
        assertNotSame(firstResponseQueue, secondResponseQueue);
    }

    @Test
    public void testGetResponseQueue_SameQueueForSameThread() {
        RequestResponseController controller = requestResponseController();

        BlockingQueue<Response> firstResponseQueue = controller.getResponseQueue("worker");
        BlockingQueue<Response> secondResponseQueue = controller.getResponseQueue("worker");

        assertSame(firstResponseQueue, secondResponseQueue);
    }

    @Test
    public void testGetInterface() {
        RequestResponseController controller = requestResponseController();

        RequestResponseInterface requestResponseInterface = controller.getInterface("worker_0");

        assertNotNull(requestResponseInterface);
    }
}
