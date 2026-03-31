package unit.modelarium.results;

import modelarium.Entity;
import modelarium.attributes.results.AttributeSetRunLog;
import modelarium.results.ModelElementResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ModelElementResults}.
 */
public class EntityResultsTest {
    private Entity mockElement;
    private AttributeSetCollection mockCollection;
    private AttributeSetCollectionResults mockCollectionResults;
    private AttributeSetRunLog mockSetResults;

    @BeforeEach
    void setUp() {
        // Mocks for attribute set structure
        mockElement = mock(Entity.class);
        mockCollection = mock(AttributeSetCollection.class); // NEW: mock the collection
        mockCollectionResults = mock(AttributeSetCollectionResults.class);
        mockSetResults = mock(AttributeSetRunLog.class);

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
        ModelElementResults results = new ModelElementResults(mockElement);
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

        ModelElementResults results = new ModelElementResults(List.of(mockElement, anotherElement));
        assertEquals(2, results.getAttributeSetCollectionSetCount());
        assertEquals(anotherResults, results.getAttributeSetCollectionResults("Agent2"));
    }

    @Test
    void testMergeWithAddsAllElements() {
        ModelElementResults results1 = new ModelElementResults(mockElement);

        Entity mockOther = mock(Entity.class);
        AttributeSetCollection mockOtherCollection = mock(AttributeSetCollection.class);
        AttributeSetCollectionResults mockOtherResults = mock(AttributeSetCollectionResults.class);

        when(mockOther.getAttributeSetCollection()).thenReturn(mockOtherCollection);
        when(mockOtherCollection.getResults()).thenReturn(mockOtherResults);
        when(mockOtherResults.getModelElementName()).thenReturn("Other");

        ModelElementResults results2 = new ModelElementResults(mockOther);
        results1.mergeWith(results2);

        assertEquals(2, results1.getAttributeSetCollectionSetCount());
        assertEquals(mockOtherResults, results1.getAttributeSetCollectionResults("Other"));
    }

    @Test
    void testGetPropertyValuesDelegatesCorrectly() {
        ModelElementResults results = new ModelElementResults(mockElement);
        List<Object> values = results.getPropertyValues("Agent1", "set1", "prop1");
        assertEquals(List.of("value1"), values);
    }

    @Test
    void testGetPreEventValuesDelegatesCorrectly() {
        ModelElementResults results = new ModelElementResults(mockElement);
        List<Boolean> values = results.getPreEventValues("Agent1", "set1", "eventA");
        assertEquals(List.of(true), values);
    }

    @Test
    void testGetPostEventValuesDelegatesCorrectly() {
        ModelElementResults results = new ModelElementResults(mockElement);
        List<Boolean> values = results.getPostEventValues("Agent1", "set1", "eventB");
        assertEquals(List.of(false), values);
    }

    @Test
    void testDisconnectDatabasesClearsAll() {
        ModelElementResults results = new ModelElementResults(mockElement);
        results.disconnectDatabases();
        verify(mockCollectionResults).disconnectDatabases();
        assertEquals(0, results.getAttributeSetCollectionSetCount());
    }
}
