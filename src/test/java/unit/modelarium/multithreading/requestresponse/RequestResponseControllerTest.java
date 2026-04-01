package unit.modelarium.multithreading.requestresponse;

import modelarium.ModelConfig;
import modelarium.multithreading.requestresponse.Request;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.multithreading.requestresponse.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestResponseControllerTest {

    private ModelConfig mockSettings;
    private RequestResponseController controller;

    @BeforeEach
    void setUp() {
        mockSettings = mock(ModelConfig.class);
        controller = new RequestResponseController(mockSettings);
    }

    @Test
    void testGetRequestQueue_ReturnsNonNullQueue() {
        BlockingQueue<Request> queue = controller.getRequestQueue();
        assertNotNull(queue, "Request queue should not be null.");
    }

    @Test
    void testGetResponseQueue_ReturnsNonNullQueue() {
        BlockingQueue<Response> queue = controller.getResponseQueue();
        assertNotNull(queue, "Response queue should not be null.");
    }

    @Test
    void testGetInterface_CreatesValidInterface() {
        when(mockSettings.getAreProcessesSynced()).thenReturn(true);

        String threadName = "TestThread";
        RequestResponseInterface iface = controller.getInterface(threadName);

        assertNotNull(iface, "Interface should be successfully created.");
    }

    @Test
    void testQueuesAreSharedBetweenInterfaces() throws NoSuchFieldException, IllegalAccessException {
        BlockingQueue<Request> originalQueue = controller.getRequestQueue();
        RequestResponseInterface iface = controller.getInterface("ThreadA");

        // Access the private requestQueue field via reflection
        Field requestQueueField = RequestResponseInterface.class.getDeclaredField("requestQueue");
        requestQueueField.setAccessible(true);
        Object internalQueue = requestQueueField.get(iface);

        assertSame(originalQueue, internalQueue, "Request queue in interface should match the controller's shared queue.");
    }

}
