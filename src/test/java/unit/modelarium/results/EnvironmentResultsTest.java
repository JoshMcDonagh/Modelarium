package unit.modelarium.results;

import modelarium.attributes.results.AttributeSetRunLog;
import modelarium.environments.Environment;
import modelarium.results.EnvironmentResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EnvironmentResults}.
 */
public class EnvironmentResultsTest {

    private Environment mockEnvironment;
    private AttributeSetCollectionResults mockCollectionResults;
    private AttributeSetRunLog mockSetResults;
    private AttributeSetCollection mockCollection;

    @BeforeEach
    void setUp() {
        mockEnvironment = mock(Environment.class);
        mockCollection = mock(AttributeSetCollection.class); // ← mock it here
        mockCollectionResults = mock(AttributeSetCollectionResults.class);
        mockSetResults = mock(AttributeSetRunLog.class);

        // Chain setup correctly
        when(mockEnvironment.getAttributeSetCollection()).thenReturn(mockCollection);
        when(mockCollection.getResults()).thenReturn(mockCollectionResults);

        // Stub rest as before
        when(mockEnvironment.name()).thenReturn("Env1");
        when(mockCollectionResults.getModelElementName()).thenReturn("Env1");
        when(mockCollectionResults.getAttributeSetResults("Weather")).thenReturn(mockSetResults);
        when(mockSetResults.getPropertyValues("temperature")).thenReturn(List.of(23.5));
        when(mockSetResults.getPreEventValues("stormWarning")).thenReturn(List.of(true));
        when(mockSetResults.getPostEventValues("rainEnd")).thenReturn(List.of(false));
    }

    @Test
    void testGetPropertyValuesReturnsExpectedValues() {
        EnvironmentResults results = new EnvironmentResults(mockEnvironment);

        List<Object> values = results.getPropertyValues("Weather", "temperature");
        assertEquals(List.of(23.5), values);
    }

    @Test
    void testGetPreEventValuesReturnsExpectedValues() {
        EnvironmentResults results = new EnvironmentResults(mockEnvironment);

        List<Boolean> values = results.getPreEventValues("Weather", "stormWarning");
        assertEquals(List.of(true), values);
    }

    @Test
    void testGetPostEventValuesReturnsExpectedValues() {
        EnvironmentResults results = new EnvironmentResults(mockEnvironment);

        List<Boolean> values = results.getPostEventValues("Weather", "rainEnd");
        assertEquals(List.of(false), values);
    }

    @Test
    void testGetAttributeSetCollectionResultsReturnsCorrectResults() {
        EnvironmentResults results = new EnvironmentResults(mockEnvironment);

        AttributeSetCollectionResults retrieved = results.getAttributeSetCollectionResults();
        assertEquals(mockCollectionResults, retrieved);
    }
}
