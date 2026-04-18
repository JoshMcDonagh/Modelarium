package unit.modelarium.results;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.MutableAgentSet;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.results.ResultsForAgents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ResultsForAgents}.
 */
public class AgentsResultsTest {

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
        MutableAgentSet agentSet = mock(MutableAgentSet.class);
        when(agentSet.getAsList()).thenReturn(List.of(mockAgent1, mockAgent2));

        ResultsForAgents agentsResults = new ResultsForAgents(agentSet);

        assertEquals(2, agentsResults.getAttributeSetCollectionSetCount());
        assertNotNull(agentsResults.getAttributeSetCollectionResults("A1"));
        assertNotNull(agentsResults.getAttributeSetCollectionResults("A2"));
    }

    @Test
    void testGetPropertyValuesDelegatesCorrectly() {
        MutableAgentSet agentSet = mock(MutableAgentSet.class);
        when(agentSet.getAsList()).thenReturn(List.of(mockAgent1));
        ResultsForAgents agentsResults = new ResultsForAgents(agentSet);

        List<Object> values = agentsResults.getPropertyValues("A1", "behaviour", "prop");
        assertEquals(List.of("X"), values);
    }

    @Test
    void testGetPostEventValuesDelegatesCorrectly() {
        MutableAgentSet agentSet = mock(MutableAgentSet.class);
        when(agentSet.getAsList()).thenReturn(List.of(mockAgent2));
        ResultsForAgents agentsResults = new ResultsForAgents(agentSet);

        List<Boolean> values = agentsResults.getPostEventValues("A2", "behaviour", "event");
        assertEquals(List.of(true), values);
    }

    @Test
    void testDisconnectDatabasesDelegates() {
        MutableAgentSet agentSet = mock(MutableAgentSet.class);
        when(agentSet.getAsList()).thenReturn(List.of(mockAgent1, mockAgent2));

        ResultsForAgents agentsResults = new ResultsForAgents(agentSet);
        agentsResults.disconnectDatabases();

        verify(results1).disconnectDatabases();
        verify(results2).disconnectDatabases();
    }
}
