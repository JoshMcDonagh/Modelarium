package unit.modelarium.results;

import modelarium.agents.Agent;
import modelarium.agents.sets.AgentSet;
import modelarium.logging.AttributeSetLog;
import modelarium.results.AgentLevelResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AgentLevelResults}.
 */
public class AgentResultsTest {

    private Agent mockAgent1;
    private Agent mockAgent2;
    private AttributeSetCollectionResults results1;
    private AttributeSetCollectionResults results2;
    private AttributeSetLog mockAttrResults1;
    private AttributeSetLog mockAttrResults2;

    @BeforeEach
    void setUp() {
        // Mock agents and their attribute collections
        mockAgent1 = mock(Agent.class);
        mockAgent2 = mock(Agent.class);

        // Mock AttributeSetCollection and its results
        results1 = mock(AttributeSetCollectionResults.class);
        results2 = mock(AttributeSetCollectionResults.class);
        AttributeSetCollection attrCollection1 = mock(AttributeSetCollection.class);
        AttributeSetCollection attrCollection2 = mock(AttributeSetCollection.class);

        // Connect agents to their attribute set collections
        when(mockAgent1.getAttributeSetCollection()).thenReturn(attrCollection1);
        when(mockAgent2.getAttributeSetCollection()).thenReturn(attrCollection2);

        // Connect attribute set collections to their results
        when(attrCollection1.getResults()).thenReturn(results1);
        when(attrCollection2.getResults()).thenReturn(results2);

        // Mock attribute set results
        mockAttrResults1 = mock(AttributeSetLog.class);
        mockAttrResults2 = mock(AttributeSetLog.class);

        when(results1.getModelElementName()).thenReturn("A1");
        when(results2.getModelElementName()).thenReturn("A2");

        when(results1.getAttributeSetResults("behaviour")).thenReturn(mockAttrResults1);
        when(results2.getAttributeSetResults("behaviour")).thenReturn(mockAttrResults2);

        when(mockAttrResults1.getPropertyValues("prop")).thenReturn(List.of("X"));
        when(mockAttrResults2.getPostEventValues("event")).thenReturn(List.of(true));
    }

    @Test
    void testConstructionStoresAllAgents() {
        AgentSet agentSet = mock(AgentSet.class);
        when(agentSet.getAsList()).thenReturn(List.of(mockAgent1, mockAgent2));

        AgentLevelResults agentResults = new AgentLevelResults(agentSet);

        assertEquals(2, agentResults.getAttributeSetCollectionSetCount());
        assertNotNull(agentResults.getAttributeSetCollectionResults("A1"));
        assertNotNull(agentResults.getAttributeSetCollectionResults("A2"));
    }

    @Test
    void testGetPropertyValuesDelegatesCorrectly() {
        AgentSet agentSet = mock(AgentSet.class);
        when(agentSet.getAsList()).thenReturn(List.of(mockAgent1));
        AgentLevelResults agentResults = new AgentLevelResults(agentSet);

        List<Object> values = agentResults.getPropertyValues("A1", "behaviour", "prop");
        assertEquals(List.of("X"), values);
    }

    @Test
    void testGetPostEventValuesDelegatesCorrectly() {
        AgentSet agentSet = mock(AgentSet.class);
        when(agentSet.getAsList()).thenReturn(List.of(mockAgent2));
        AgentLevelResults agentResults = new AgentLevelResults(agentSet);

        List<Boolean> values = agentResults.getPostEventValues("A2", "behaviour", "event");
        assertEquals(List.of(true), values);
    }

    @Test
    void testDisconnectDatabasesDelegates() {
        AgentSet agentSet = mock(AgentSet.class);
        when(agentSet.getAsList()).thenReturn(List.of(mockAgent1, mockAgent2));

        AgentLevelResults agentResults = new AgentLevelResults(agentSet);
        agentResults.disconnectDatabases();

        verify(results1).disconnectDatabases();
        verify(results2).disconnectDatabases();
    }
}
