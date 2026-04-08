package unit.modelarium.attributes.functional;

import modelarium.entities.attributes.events.functional.AgentEventIsTriggeredFunction;
import modelarium.entities.attributes.events.functional.AgentEventRunFunction;
import modelarium.entities.attributes.events.functional.FunctionalAgentEvent;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Unit test for {@link FunctionalAgentEvent}.
 *
 * <p>Verifies that run logic is executed and the trigger logic behaves as expected.</p>
 */
public class FunctionalAgentEventTest {

    @Test
    void testRunLogicIsExecuted() {
        // Arrange: use a flag to verify the run logic is called

        AtomicBoolean hasRun = new AtomicBoolean(false);
        AgentEventRunFunction runLogic = (associatedModelElement) -> hasRun.set(true);

        AgentEventIsTriggeredFunction triggerLogic = (associatedModelElement) -> false;  // doesn't matter for this test

        FunctionalAgentEvent event = new FunctionalAgentEvent("RunEvent", true, runLogic, triggerLogic);

        // Act
        event.run();

        // Assert
        assertTrue(hasRun.get(), "The run logic should have been executed");
    }

    @Test
    void testIsTriggeredReturnsTrue() {
        // Arrange: use a BooleanSupplier that returns true
        AgentEventRunFunction runLogic = mock(AgentEventRunFunction.class);
        AgentEventIsTriggeredFunction triggerLogic = (associatedModelElement) -> true;

        FunctionalAgentEvent event = new FunctionalAgentEvent("TriggerTrueEvent", false, runLogic, triggerLogic);

        // Act & Assert
        assertTrue(event.isTriggered(), "Expected trigger logic to return true");
    }

    @Test
    void testIsTriggeredReturnsFalse() {
        // Arrange: use a BooleanSupplier that returns false
        AgentEventRunFunction runLogic = mock(AgentEventRunFunction.class);
        AgentEventIsTriggeredFunction triggerLogic = (associatedModelElement) -> false;

        FunctionalAgentEvent event = new FunctionalAgentEvent("TriggerFalseEvent", false, runLogic, triggerLogic);

        // Act & Assert
        assertFalse(event.isTriggered(), "Expected trigger logic to return false");
    }

    @Test
    void testNameAndRecordingFlagsAreSet() {
        // Arrange
        String name = "TestEvent";
        boolean isRecorded = true;
        AgentEventRunFunction runLogic = mock(AgentEventRunFunction.class);
        AgentEventIsTriggeredFunction triggerLogic = (associatedModelElement) -> false;

        FunctionalAgentEvent event = new FunctionalAgentEvent(name, isRecorded, runLogic, triggerLogic);

        // Act & Assert
        assertEquals(name, event.getName());
        assertTrue(event.isRecorded());
    }
}
