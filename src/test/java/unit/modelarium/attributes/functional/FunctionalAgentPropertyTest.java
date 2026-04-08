package unit.modelarium.attributes.functional;

import modelarium.entities.attributes.properties.functional.FunctionalAgentProperty;
import modelarium.entities.attributes.properties.functional.AgentPropertyGetterFunction;
import modelarium.entities.attributes.properties.functional.AgentPropertyRunFunction;
import modelarium.entities.attributes.properties.functional.AgentPropertySetterFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link FunctionalAgentProperty}.
 *
 * <p>Verifies that the get, set, and run behaviours execute as expected when provided via functional interfaces.</p>
 */
public class FunctionalAgentPropertyTest {

    @Test
    void testGetReturnsCurrentValue() {
        String value = "initial";

        AgentPropertyGetterFunction<String> getter = (associatedModelElement, propertyValue) -> propertyValue;
        AgentPropertySetterFunction<String> setter = (associatedModelElement, currentPropertyValue, newValue) -> newValue;
        AgentPropertyRunFunction<String> runLogic = mock(AgentPropertyRunFunction.class);

        FunctionalAgentProperty<String> property = new FunctionalAgentProperty<>(
                "TestProperty", true, String.class, getter, setter, runLogic
        );

        // Act
        property.set(value);
        String result = property.get();

        // Assert
        assertEquals("initial", result);
    }

    @Test
    void testSetUpdatesValueCorrectly() {
        int value = 0;

        AgentPropertyGetterFunction<Integer> getter = (associatedModelElement, propertyValue) -> propertyValue;
        AgentPropertySetterFunction<Integer> setter = (associatedModelElement, currentPropertyValue, newValue) -> newValue;
        AgentPropertyRunFunction<Integer> runLogic = mock(AgentPropertyRunFunction.class);

        FunctionalAgentProperty<Integer> property = new FunctionalAgentProperty<>(
                "Counter", true, Integer.class, getter, setter, runLogic
        );

        // Act
        property.set(value);
        property.set(99);

        // Assert
        assertEquals(99, property.get());
    }

    @Test
    void testRunExecutesRunLogic() {
        AgentPropertyGetterFunction<Double> getter = (associatedModelElement, propertyValue) -> 0.0;
        AgentPropertySetterFunction<Double> setter = (associatedModelElement, currentPropertyValue, newValue) -> null;
        AgentPropertyRunFunction<Double> runLogic = mock(AgentPropertyRunFunction.class);

        FunctionalAgentProperty<Double> property = new FunctionalAgentProperty<>(
                "RunTest", false, Double.class, getter, setter, runLogic
        );

        // Act
        property.run();

        // Assert
        verify(runLogic, times(1)).run(null, null);
    }

    @Test
    void testMetadataIsCorrect() {
        // Arrange
        AgentPropertyGetterFunction<Boolean> getter = (associatedModelElement, propertyValue) -> true;
        AgentPropertySetterFunction<Boolean> setter = (associatedModelElement, currentPropertyValue, newValue) -> null;
        AgentPropertyRunFunction<Boolean> runLogic = mock(AgentPropertyRunFunction.class);

        FunctionalAgentProperty<Boolean> property = new FunctionalAgentProperty<>(
                "Flag", true, Boolean.class, getter, setter, runLogic
        );

        // Assert
        assertEquals("Flag", property.getName());
        assertEquals(Boolean.class, property.getType());
        assertTrue(property.isRecorded());
    }
}
