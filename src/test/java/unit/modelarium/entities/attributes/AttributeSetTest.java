package unit.modelarium.entities.attributes;

import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.exceptions.AttributeAccessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.attributes.AttributeTestHelpers.*;

public class AttributeSetTest {
    @Test
    public void testName() {
        AgentAttributeSet attributeSet = singlePropertyAgentSet("owner", "food", "hunger");

        assertEquals("food", attributeSet.name());
    }

    @Test
    public void testSize() {
        AgentAttributeSet attributeSet = agentAttributeSet(
                "owner",
                "s",
                new AgentCounterProperty("a"),
                new AgentCounterProperty("b")
        );

        assertEquals(2, attributeSet.size());
    }

    @Test
    public void testSize_Empty() {
        AgentAttributeSet attributeSet = agentAttributeSet("owner", "empty");

        assertEquals(0, attributeSet.size());
    }

    @Test
    public void testGetProperty_PublicAccessLevel() {
        AgentAttributeSet attributeSet = agentAttributeSet("owner", "s", new AgentCounterProperty("counter"));

        assertDoesNotThrow(() -> attributeSet.getProperty("counter"));
    }

    @Test
    public void testGetProperty_PrivateAccessLevel_AttributeAccessException() {
        AgentAttributeSet attributeSet = agentAttributeSet("owner", "s", new PrivateCounterProperty("secret"));

        assertThrows(AttributeAccessException.class, () -> attributeSet.getProperty("secret"));
    }

    @Test
    public void testGetEvent() {
        AgentAttributeSet attributeSet = agentAttributeSetFromEvents(
                "owner",
                "food",
                new AlwaysTriggeredAgentEvent("eatFood")
        );

        assertDoesNotThrow(() -> attributeSet.getEvent("eatFood"));
    }

    @Test
    public void testGetEvent_GivenPropertyName_AttributeAccessException() {
        AgentAttributeSet attributeSet = agentAttributeSet("owner", "s", new AgentCounterProperty("hp"));

        assertThrows(AttributeAccessException.class, () -> attributeSet.getEvent("hp"));
    }

    @Test
    public void testGetRoutine() {
        AgentAttributeSet attributeSet = agentAttributeSetFromRoutines(
                "owner",
                "sim",
                new EmptyAgentRoutine("tick")
        );

        assertDoesNotThrow(() -> attributeSet.getRoutine("tick"));
    }

    @Test
    public void testEnvironmentAttributeSetName() {
        EnvironmentAttributeSet attributeSet = emptyEnvironmentAttributeSet("env", "weather");

        assertEquals("weather", attributeSet.name());
    }

    @Test
    public void testEnvironmentAttributeSetSize() {
        EnvironmentAttributeSet attributeSet = environmentAttributeSet(
                "env",
                "timing",
                new EnvironmentTickProperty("tick")
        );

        assertEquals(1, attributeSet.size());
    }
}
