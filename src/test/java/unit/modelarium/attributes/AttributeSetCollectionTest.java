package unit.modelarium.attributes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link AttributeSetCollection} class.
 *
 * <p>Verifies setup, attribute set management, result recording, and execution behaviours.
 */
public class AttributeSetCollectionTest {

    private AttributeSetCollection collection;
    private AttributeSet mockAttributeSet;

    @BeforeEach
    public void setup() {
        collection = new AttributeSetCollection();
        mockAttributeSet = mock(AttributeSet.class);

        when(mockAttributeSet.getName()).thenReturn("TestSet");
        when(mockAttributeSet.getProperties()).thenReturn(new Properties());
        when(mockAttributeSet.getPreEvents()).thenReturn(new Events());
        when(mockAttributeSet.getPostEvents()).thenReturn(new Events());

        when(mockAttributeSet.deepCopy()).thenReturn(mockAttributeSet);
    }

    @Test
    public void testAddAndRetrieveAttributeSet() {
        collection.add(mockAttributeSet);
        assertEquals(1, collection.size());
        assertEquals(mockAttributeSet, collection.get("TestSet"));
    }

    @Test
    public void testAddMultipleAttributeSets() {
        AttributeSet anotherSet = mock(AttributeSet.class);
        when(anotherSet.getName()).thenReturn("AnotherSet");

        collection.add(Collections.singletonList(mockAttributeSet));
        collection.add(Collections.singletonList(anotherSet));

        assertEquals(2, collection.size());
        assertEquals(mockAttributeSet, collection.get("TestSet"));
        assertEquals(anotherSet, collection.get("AnotherSet"));
    }

    @Test
    public void testSetupInitialisesResultsCorrectly() {
        collection.add(mockAttributeSet);
        collection.setup("Agent_1");

        AttributeSetCollectionResults results = collection.getResults();
        assertNotNull(results);
        assertEquals("Agent_1", results.getModelElementName());
    }

    @Test
    public void testRunExecutesEachAttributeSetWithCorrespondingResults() {
        collection.add(mockAttributeSet);
        collection.setup("Agent_1");

        collection.run();

        // Ensure run() on AttributeSet is called with correct AttributeSetResults
        verify(mockAttributeSet).run(any());
    }

    @Test
    public void testDeepCopyProducesEqualButDistinctCollection() {
        collection.add(mockAttributeSet);
        AttributeSetCollection copy = collection.deepCopy();

        assertNotSame(collection, copy);
        assertEquals(collection.size(), copy.size());
    }
}
