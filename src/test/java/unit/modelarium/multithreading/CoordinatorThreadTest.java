package unit.modelarium.multithreading;

import modelarium.ModelConfig;
import modelarium.agents.Agent;
import modelarium.agents.sets.AgentSet;
import modelarium.environments.Environment;
import modelarium.multithreading.CoordinatorThread;
import modelarium.multithreading.requestresponse.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link CoordinatorThread} class.
 *
 * <p>Verifies correct setup behaviour and basic coordination logic using mocked dependencies.
 */
public class CoordinatorThreadTest {

    private ModelConfig settings;
    private Environment environment;
    private RequestResponseController controller;
    private BlockingQueue<Request> requestQueue;
    private BlockingQueue<Response> responseQueue;

    @BeforeEach
    void setUp() {
        settings = mock(ModelConfig.class);
        environment = mock(Environment.class);
        controller = mock(RequestResponseController.class);
        requestQueue = new LinkedBlockingQueue<>();
        responseQueue = new LinkedBlockingQueue<>();

        when(controller.getRequestQueue()).thenReturn(requestQueue);
        when(controller.getResponseQueue()).thenReturn(responseQueue);
    }

    @Test
    void testCoordinatorThreadInitialisesCorrectly() {
        CoordinatorThread coordinatorThread = new CoordinatorThread(
                "Coordinator-1",
                settings,
                environment,
                controller
        );

        assertNotNull(coordinatorThread, "Coordinator thread should be instantiated without error.");
    }

    @Test
    void testCoordinatorThreadHandlesAgentAccessRequest() throws InterruptedException {
        when(settings.getNumOfCores()).thenReturn(1);  // For barrier-based handlers if needed

        // Mock request
        Request mockRequest = mock(Request.class);
        when(mockRequest.getRequestType()).thenReturn(RequestType.AGENT_ACCESS);
        when(mockRequest.getRequester()).thenReturn("Worker-1");
        when(mockRequest.getPayload()).thenReturn("agent-123");

        // Create dummy agent and ensure its name matches the payload
        Agent dummyAgent = mock(Agent.class);
        when(dummyAgent.name()).thenReturn("agent-123");

        // Add dummy agent to a real agent set
        AgentSet realAgentSet = new AgentSet();
        realAgentSet.add(dummyAgent);

        // Let the static initialise method run normally
        try (MockedStatic<CoordinatorRequestHandler> handler = mockStatic(CoordinatorRequestHandler.class)) {
            handler.when(() -> CoordinatorRequestHandler.initialise(any(), any(), any(), any(), any()))
                    .thenCallRealMethod();

            // Inject the agent set
            CoordinatorThread coordinatorThread = new CoordinatorThread(
                    "TestThread",
                    settings,
                    environment,
                    controller,
                    realAgentSet  // Injected global agent set
            );

            Thread thread = new Thread(coordinatorThread::run);
            thread.start();

            // Send the request
            requestQueue.offer(mockRequest);

            // Give time to process
            Thread.sleep(300);

            // Shutdown
            coordinatorThread.shutdown();
            thread.join();

            // Check response
            Response response = responseQueue.poll();
            assertNotNull(response, "Expected a response to be placed in the queue");
            assertEquals(ResponseType.AGENT_ACCESS, response.getResponseType());
            assertEquals("Worker-1", response.getDestination());
        }
    }
}
