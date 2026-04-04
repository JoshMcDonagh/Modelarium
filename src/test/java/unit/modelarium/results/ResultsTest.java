package unit.modelarium.results;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import modelarium.results.ResultsForAgents;
import modelarium.results.ResultsForEnvironment;
import modelarium.results.Results;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link Results} class using a minimal concrete subclass.
 */
public class ResultsTest {

    private Agent agent;
    private AgentSet agentSet;
    private ResultsForAgents mockAgentsResults;
    private ResultsForEnvironment mockEnvironmentResults;
    private TestResults results;

    @BeforeEach
    void setUp() {
        agent = mock(Agent.class);
        when(agent.name()).thenReturn("Agent1");

        agentSet = new AgentSet();
        agentSet.add(agent);

        mockAgentsResults = mock(ResultsForAgents.class);
        mockEnvironmentResults = mock(ResultsForEnvironment.class);

        results = new TestResults();
    }

    @Test
    void testSetAgentNamesStoresNames() {
        results.setAgentNames(agentSet);
        assertEquals(Collections.singletonList("Agent1"), results.getAgentNames());
    }

    @Test
    void testSealPreventsFurtherModification() {
        results.setAgentNames(agentSet);
        results.seal();
        assertThrows(IllegalStateException.class, () -> results.setAgentNames(agentSet));
    }

    @Test
    void testSetAgentResultsConnectsDatabase() {
        results.setAgentResults(mockAgentsResults);
        when(mockAgentsResults.getPropertyValues("Agent1", "attr", "prop")).thenReturn(List.of("val"));

        List<Object> values = results.getAgentPropertyValues("Agent1", "attr", "prop");
        assertEquals(List.of("val"), values);
    }

    @Test
    void testSetEnvironmentResultsConnectsDatabase() {
        results.setEnvironmentResults(mockEnvironmentResults);
        when(mockEnvironmentResults.getPropertyValues("attr", "prop")).thenReturn(List.of("envVal"));

        List<Object> values = results.getEnvironmentPropertyValues("attr", "prop");
        assertEquals(List.of("envVal"), values);
    }

    @Test
    void testAccessWithoutAgentDataThrows() {
        assertThrows(IllegalStateException.class,
                () -> results.getAgentPropertyValues("Agent1", "set", "prop"));
    }

    @Test
    void testAccessWithoutEnvironmentDataThrows() {
        assertThrows(IllegalStateException.class,
                () -> results.getEnvironmentPropertyValues("set", "prop"));
    }

    @Test
    void testDisconnectDatabasesDisconnectsAgentsAndEnvironment() {
        results.setAgentResults(mockAgentsResults);
        results.setEnvironmentResults(mockEnvironmentResults);

        results.disconnectDatabases();

        verify(mockAgentsResults).disconnectDatabases();
        verify(mockEnvironmentResults).disconnectDatabases();
    }

    @Test
    void testDisconnectAccumulatedDatabasesClearsInternalMaps() {
        AttributeSetLogDatabase mockDb = mock(AttributeSetLogDatabase.class);

        results.injectMockAccumulatedAgentDb("mockSet", mockDb);
        results.injectMockProcessedEnvironmentDb("mockEnv", mockDb);

        results.disconnectAccumulatedDatabases();

        verify(mockDb, times(2)).disconnect();
    }

    /**
     * Minimal concrete subclass of {@link Results} for testing.
     */
    static class TestResults extends Results {
        @Override
        protected List<?> accumulateAgentPropertyResults(String attributeSetName, String propertyName,
                                                         List<?> accumulatedValues, List<?> valuesToBeProcessed) {
            return List.of();
        }

        @Override
        protected List<?> accumulateAgentPreEventResults(String attributeSetName, String preEventName,
                                                         List<?> accumulatedValues, List<Boolean> valuesToBeProcessed) {
            return List.of();
        }

        @Override
        protected List<?> accumulateAgentPostEventResults(String attributeSetName, String postEventName,
                                                          List<?> accumulatedValues, List<Boolean> valuesToBeProcessed) {
            return List.of();
        }

        // Inject mock databases using reflection since private fields are not accessible via subclass
        void injectMockAccumulatedAgentDb(String name, AttributeSetLogDatabase db) {
            try {
                Field mapField = Results.class.getDeclaredField("accumulatedAgentAttributeSetResultsDatabaseMap");
                Field listField = Results.class.getDeclaredField("accumulatedAgentAttributeSetResultsDatabaseList");
                Field flagField = Results.class.getDeclaredField("isAccumulatedAgentAttributeSetDataConnected");

                mapField.setAccessible(true);
                listField.setAccessible(true);
                flagField.setAccessible(true);

                ((Map<String, AttributeSetLogDatabase>) mapField.get(this)).put(name, db);
                ((List<AttributeSetLogDatabase>) listField.get(this)).add(db);
                flagField.set(this, true);
            } catch (Exception e) {
                throw new RuntimeException("Reflection injection failed", e);
            }
        }

        void injectMockProcessedEnvironmentDb(String name, AttributeSetLogDatabase db) {
            try {
                Field mapField = Results.class.getDeclaredField("processedEnvironmentAttributeSetResultsDatabaseMap");
                Field listField = Results.class.getDeclaredField("processedEnvironmentAttributeSetResultsDatabaseList");
                Field flagField = Results.class.getDeclaredField("isProcessedEnvironmentAttributeSetDataConnected");

                mapField.setAccessible(true);
                listField.setAccessible(true);
                flagField.setAccessible(true);

                ((Map<String, AttributeSetLogDatabase>) mapField.get(this)).put(name, db);
                ((List<AttributeSetLogDatabase>) listField.get(this)).add(db);
                flagField.set(this, true);
            } catch (Exception e) {
                throw new RuntimeException("Reflection injection failed", e);
            }
        }
    }
}
