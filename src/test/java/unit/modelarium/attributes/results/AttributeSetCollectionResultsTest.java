package unit.modelarium.attributes.results;

import modelarium.logging.AttributeSetLog;
import modelarium.logging.databases.AttributeSetRunLogDatabase;
import modelarium.logging.databases.AttributeSetRunLogDatabaseFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttributeSetCollectionResultsTest {

    private AttributeSetCollectionResults collectionResults;

    @BeforeEach
    void setUp() {
        collectionResults = new AttributeSetCollectionResults();

        // Use a mock database implementation to avoid real disk I/O
        AttributeSetRunLogDatabaseFactory.setCustomFactory(() -> mock(AttributeSetRunLogDatabase.class));
    }

    @AfterEach
    void tearDown() {
        AttributeSetRunLogDatabaseFactory.clearCustomFactory();
    }

    @Test
    void testSetupInitialisesResultsCorrectly() {
        // Given an attribute set with a known name
        AttributeSet attributeSet = new AttributeSet("MyAttributes");

        // When setup is called
        collectionResults.setup("TestElement", Collections.singletonList(attributeSet));

        // Then the attribute set count should be 1
        assertEquals(1, collectionResults.getAttributeSetCount());

        // And the model element name should be correct
        assertEquals("TestElement", collectionResults.getModelElementName());

        // And the attribute set results should be retrievable by name
        assertNotNull(collectionResults.getAttributeSetResults("MyAttributes"));

        // And the attribute set results should be retrievable by index
        assertNotNull(collectionResults.getAttributeSetResults(0));
    }

    @Test
    void testDisconnectDatabasesClearsResultsAndCallsDisconnect() {
        // Given an attribute set initialised in the collection
        AttributeSet attributeSet = new AttributeSet("ToDisconnect");

        // Set up a mock factory to return a mocked database
        AttributeSetRunLogDatabase mockDatabase = mock(AttributeSetRunLogDatabase.class);
        AttributeSetRunLogDatabaseFactory.setCustomFactory(() -> mockDatabase);

        collectionResults.setup("MockElement", Collections.singletonList(attributeSet));

        AttributeSetLog results = collectionResults.getAttributeSetResults(0);
        assertNotNull(results); // Sanity check

        // When disconnectDatabases is called
        collectionResults.disconnectDatabases();

        // Then the internal list should be cleared
        assertEquals(0, collectionResults.getAttributeSetCount());

        // And the database's disconnect method should have been called
        verify(mockDatabase).disconnect();
    }
}
