package unit.modelarium.attributes.functional;

import modelarium.entities.attributes.functional.properties.FunctionalProperty;
import modelarium.entities.attributes.functional.properties.PropertyGetterFunction;
import modelarium.entities.attributes.functional.properties.PropertyRunFunction;
import modelarium.entities.attributes.functional.properties.PropertySetterFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link FunctionalProperty}.
 *
 * <p>Verifies that the get, set, and run behaviours execute as expected when provided via functional interfaces.</p>
 */
public class FunctionalPropertyTest {

    @Test
    void testGetReturnsCurrentValue() {
        String value = "initial";

        PropertyGetterFunction<String> getter = (associatedModelElement, propertyValue) -> propertyValue;
        PropertySetterFunction<String> setter = (associatedModelElement, currentPropertyValue, newValue) -> newValue;
        PropertyRunFunction<String> runLogic = mock(PropertyRunFunction.class);

        FunctionalProperty<String> property = new FunctionalProperty<>(
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

        PropertyGetterFunction<Integer> getter = (associatedModelElement, propertyValue) -> propertyValue;
        PropertySetterFunction<Integer> setter = (associatedModelElement, currentPropertyValue, newValue) -> newValue;
        PropertyRunFunction<Integer> runLogic = mock(PropertyRunFunction.class);

        FunctionalProperty<Integer> property = new FunctionalProperty<>(
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
        PropertyGetterFunction<Double> getter = (associatedModelElement, propertyValue) -> 0.0;
        PropertySetterFunction<Double> setter = (associatedModelElement, currentPropertyValue, newValue) -> null;
        PropertyRunFunction<Double> runLogic = mock(PropertyRunFunction.class);

        FunctionalProperty<Double> property = new FunctionalProperty<>(
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
        PropertyGetterFunction<Boolean> getter = (associatedModelElement, propertyValue) -> true;
        PropertySetterFunction<Boolean> setter = (associatedModelElement, currentPropertyValue, newValue) -> null;
        PropertyRunFunction<Boolean> runLogic = mock(PropertyRunFunction.class);

        FunctionalProperty<Boolean> property = new FunctionalProperty<>(
                "Flag", true, Boolean.class, getter, setter, runLogic
        );

        // Assert
        assertEquals("Flag", property.getName());
        assertEquals(Boolean.class, property.getType());
        assertTrue(property.isRecorded());
    }
}
