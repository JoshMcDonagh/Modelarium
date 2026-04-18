package unit.modelarium.multithreading;

import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.entities.contexts.Context;
import modelarium.multithreading.WorkerThread;
import modelarium.multithreading.requestresponse.RequestResponseController;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.results.mutable.MutableResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link WorkerThread} class.
 *
 * <p>Verifies correct behaviour of the simulation loop in controlled conditions using mocks.
 */
public class WorkerThreadTest {

    private Config settings;
    private MutableAgentSet agents;
    private MutableAgentSet duplicatedAgents;
    private RequestResponseController controller;
    private RequestResponseInterface requestInterface;

    @BeforeEach
    void setUp() throws Exception {
        settings = mock(Config.class);
        agents = mock(MutableAgentSet.class);
        duplicatedAgents = mock(MutableAgentSet.class);
        controller = mock(RequestResponseController.class);
        requestInterface = mock(RequestResponseInterface.class);

        when(settings.getIsCacheUsed()).thenReturn(false);
        when(settings.getAreProcessesSynced()).thenReturn(false);
        when(settings.getTotalNumOfTicks()).thenReturn(2);  // keep it small for test speed
        when(settings.getModelScheduler()).thenReturn(tick -> {}); // no-op scheduler
        when(settings.getResults()).thenReturn(new DummyMutableResults());
        when(agents.duplicate()).thenReturn(duplicatedAgents);
        when(controller.getInterface(any())).thenReturn(requestInterface);

        Agent mockAgent = mock(Agent.class);
        when(mockAgent.getModelElementAccessor()).thenReturn(mock(Context.class));
        when(mockAgent.name()).thenReturn("Agent1");
        when(agents.iterator()).thenReturn(List.of(mockAgent).iterator());
    }

    @Test
    void testWorkerThreadCallReturnsValidResults() throws Exception {
        WorkerThread<MutableResults> worker = new WorkerThread<>("Worker-Test", settings, controller, agents);
        MutableResults results = worker.call();

        assertNotNull(results, "Worker should return non-null results.");
        assertTrue(results instanceof DummyMutableResults, "Results should be of type DummyResults.");
    }

    /**
     * A concrete implementation of {@link MutableResults} for test purposes.
     * The accumulation methods return empty lists to satisfy abstract requirements.
     */
    public static class DummyMutableResults extends MutableResults {
        @Override
        protected List<?> accumulateAgentPropertyResults(String attributeSetName, String propertyName,
                                                         List<?> accumulatedValues, List<?> valuesToBeProcessed) {
            return List.of(); // Minimal implementation for testing
        }

        @Override
        protected List<?> accumulateAgentPreEventResults(String attributeSetName, String preEventName,
                                                         List<?> accumulatedValues, List<Boolean> valuesToBeProcessed) {
            return List.of();
        }

        @Override
        protected List<?> accumulateAgentPostEventResults(String attributeSetName, String postEventName,
                                                          List<?> accumulatedValues, List<Boolean> valuesToBeProcessed) {
            return List.of();
        }
    }
}
