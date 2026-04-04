package unit.modelarium.results;

import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.environments.Environment;
import modelarium.results.ResultsForEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ResultsForEnvironment}.
 */
public class EnvironmentResultsTest {

    private Environment mockEnvironment;
    private AttributeSetCollectionResults mockCollectionResults;
    private AttributeSetLog mockSetResults;
    private AttributeSetCollection mockCollection;

    @BeforeEach
    void setUp() {
        mockEnvironment = mock(Environment.class);
        mockCollection = mock(AttributeSetCollection.class); // ← mock it here
        mockCollectionResults = mock(AttributeSetCollectionResults.class);
        mockSetResults = mock(AttributeSetLog.class);

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
        ResultsForEnvironment results = new ResultsForEnvironment(mockEnvironment);

        List<Object> values = results.getPropertyValues("Weather", "temperature");
        assertEquals(List.of(23.5), values);
    }

    @Test
    void testGetPreEventValuesReturnsExpectedValues() {
        ResultsForEnvironment results = new ResultsForEnvironment(mockEnvironment);

        List<Boolean> values = results.getPreEventValues("Weather", "stormWarning");
        assertEquals(List.of(true), values);
    }

    @Test
    void testGetPostEventValuesReturnsExpectedValues() {
        ResultsForEnvironment results = new ResultsForEnvironment(mockEnvironment);

        List<Boolean> values = results.getPostEventValues("Weather", "rainEnd");
        assertEquals(List.of(false), values);
    }

    @Test
    void testGetAttributeSetCollectionResultsReturnsCorrectResults() {
        ResultsForEnvironment results = new ResultsForEnvironment(mockEnvironment);

        AttributeSetCollectionResults retrieved = results.getAttributeSetCollectionResults();
        assertEquals(mockCollectionResults, retrieved);
    }
}
