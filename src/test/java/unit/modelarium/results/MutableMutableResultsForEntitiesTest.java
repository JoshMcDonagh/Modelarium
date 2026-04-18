package unit.modelarium.results;

import modelarium.entities.Entity;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.results.mutable.MutableResultsForEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MutableResultsForEntities}.
 */
public class MutableMutableResultsForEntitiesTest {
    private Entity mockElement;
    private AttributeSetCollection mockCollection;
    private AttributeSetCollectionResults mockCollectionResults;
    private AttributeSetLog mockSetResults;

    @BeforeEach
    void setUp() {
        // Mocks for attribute set structure
        mockElement = mock(Entity.class);
        mockCollection = mock(AttributeSetCollection.class); // NEW: mock the collection
        mockCollectionResults = mock(AttributeSetCollectionResults.class);
        mockSetResults = mock(AttributeSetLog.class);

        // Setup hierarchy
        when(mockElement.getAttributeSetCollection()).thenReturn(mockCollection); // ← FIXED
        when(mockCollection.getResults()).thenReturn(mockCollectionResults);      // ← FIXED

        // Attribute set result behavior
        when(mockCollectionResults.getModelElementName()).thenReturn("Agent1");
        when(mockCollectionResults.getAttributeSetResults("set1")).thenReturn(mockSetResults);
        when(mockSetResults.getPropertyValues("prop1")).thenReturn(List.of("value1"));
        when(mockSetResults.getPreEventValues("eventA")).thenReturn(List.of(true));
        when(mockSetResults.getPostEventValues("eventB")).thenReturn(List.of(false));
    }

    @Test
    void testSingleModelElementConstruction() {
        MutableResultsForEntities results = new MutableResultsForEntities(mockElement);
        assertEquals(1, results.getAttributeSetCollectionSetCount());
        assertEquals(mockCollectionResults, results.getAttributeSetCollectionResults("Agent1"));
        assertEquals(mockCollectionResults, results.getAttributeSetCollectionResults(0));
    }

    @Test
    void testMultipleModelElementConstruction() {
        Entity anotherElement = mock(Entity.class);
        AttributeSetCollection mockAnotherCollection = mock(AttributeSetCollection.class);
        AttributeSetCollectionResults anotherResults = mock(AttributeSetCollectionResults.class);

        when(anotherElement.getAttributeSetCollection()).thenReturn(mockAnotherCollection);
        when(mockAnotherCollection.getResults()).thenReturn(anotherResults);
        when(anotherResults.getModelElementName()).thenReturn("Agent2");

        MutableResultsForEntities results = new MutableResultsForEntities(List.of(mockElement, anotherElement));
        assertEquals(2, results.getAttributeSetCollectionSetCount());
        assertEquals(anotherResults, results.getAttributeSetCollectionResults("Agent2"));
    }

    @Test
    void testMergeWithAddsAllElements() {
        MutableResultsForEntities results1 = new MutableResultsForEntities(mockElement);

        Entity mockOther = mock(Entity.class);
        AttributeSetCollection mockOtherCollection = mock(AttributeSetCollection.class);
        AttributeSetCollectionResults mockOtherResults = mock(AttributeSetCollectionResults.class);

        when(mockOther.getAttributeSetCollection()).thenReturn(mockOtherCollection);
        when(mockOtherCollection.getResults()).thenReturn(mockOtherResults);
        when(mockOtherResults.getModelElementName()).thenReturn("Other");

        MutableResultsForEntities results2 = new MutableResultsForEntities(mockOther);
        results1.mergeWith(results2);

        assertEquals(2, results1.getAttributeSetCollectionSetCount());
        assertEquals(mockOtherResults, results1.getAttributeSetCollectionResults("Other"));
    }

    @Test
    void testGetPropertyValuesDelegatesCorrectly() {
        MutableResultsForEntities results = new MutableResultsForEntities(mockElement);
        List<Object> values = results.getPropertyValues("Agent1", "set1", "prop1");
        assertEquals(List.of("value1"), values);
    }

    @Test
    void testGetPreEventValuesDelegatesCorrectly() {
        MutableResultsForEntities results = new MutableResultsForEntities(mockElement);
        List<Boolean> values = results.getPreEventValues("Agent1", "set1", "eventA");
        assertEquals(List.of(true), values);
    }

    @Test
    void testGetPostEventValuesDelegatesCorrectly() {
        MutableResultsForEntities results = new MutableResultsForEntities(mockElement);
        List<Boolean> values = results.getPostEventValues("Agent1", "set1", "eventB");
        assertEquals(List.of(false), values);
    }

    @Test
    void testDisconnectDatabasesClearsAll() {
        MutableResultsForEntities results = new MutableResultsForEntities(mockElement);
        results.disconnectDatabases();
        verify(mockCollectionResults).disconnectDatabases();
        assertEquals(0, results.getAttributeSetCollectionSetCount());
    }
}
