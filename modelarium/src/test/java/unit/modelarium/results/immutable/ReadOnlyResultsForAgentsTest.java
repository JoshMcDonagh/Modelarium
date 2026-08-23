package unit.modelarium.results.immutable;

import modelarium.entities.agents.mutable.Agent;
import modelarium.results.immutable.ReadOnlyResultsForAgents;
import modelarium.results.mutable.ResultsForAgents;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static unit.modelarium.results.ResultsTestHelpers.*;

public class ReadOnlyResultsForAgentsTest {
    private ReadOnlyResultsForAgents populatedImmutableResults() {
        Agent agent = agentWithMemoryLogs(
                "Agent_0",
                agentAttributeSet("Agent_0", "AttributeSet_0", "Property_0", "Property_1")
        );
        record(agent, "AttributeSet_0", "Property_0", 1.0, 2.0);
        record(agent, "AttributeSet_0", "Property_1", 3.0);

        return new ReadOnlyResultsForAgents(agentResults(agent));
    }

    @Test
    public void testAgentLogCount() {
        ResultsForAgents results = agentResults(
                agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0"),
                agentWithLoggedProperty("Agent_1", "AttributeSet_0", "Property_0")
        );

        ReadOnlyResultsForAgents immutableResults = new ReadOnlyResultsForAgents(results);

        assertEquals(2, immutableResults.agentLogCount());
    }

    @Test
    public void testAttributeSetLogCount() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        assertEquals(1, immutableResults.attributeSetLogCount("Agent_0"));
    }

    @Test
    public void testAttributeLogCount() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        assertEquals(2, immutableResults.attributeLogCount("Agent_0", "AttributeSet_0"));
    }

    @Test
    public void testAttributeLogs() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        List<Object> values = immutableResults.attributeLogs("Agent_0", "AttributeSet_0", "Property_0");

        assertEquals(List.of(1.0, 2.0), values);
    }

    @Test
    public void testAttributeLogs_Unmodifiable() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        List<Object> values = immutableResults.attributeLogs("Agent_0", "AttributeSet_0", "Property_0");

        assertThrows(UnsupportedOperationException.class, () -> values.add(4.0));
    }

    @Test
    public void testAttributeLogs_Typed() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        List<Double> values = immutableResults.attributeLogs("Agent_0", "AttributeSet_0", "Property_0", Double.class);

        assertEquals(List.of(1.0, 2.0), values);
    }

    @Test
    public void testAttributeLogs_Typed_Unmodifiable() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        List<Double> values = immutableResults.attributeLogs("Agent_0", "AttributeSet_0", "Property_0", Double.class);

        assertThrows(UnsupportedOperationException.class, () -> values.add(4.0));
    }

    @Test
    public void testAttributeSetLogs() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        Map<String, List<Object>> attributeSetLogs = immutableResults.attributeSetLogs("Agent_0", "AttributeSet_0");

        assertEquals(2, attributeSetLogs.size());
        assertEquals(List.of(1.0, 2.0), attributeSetLogs.get("Property_0"));
        assertEquals(List.of(3.0), attributeSetLogs.get("Property_1"));
    }

    @Test
    public void testAttributeSetLogs_UnmodifiableAtEveryLevel() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        Map<String, List<Object>> attributeSetLogs = immutableResults.attributeSetLogs("Agent_0", "AttributeSet_0");

        assertThrows(UnsupportedOperationException.class, () -> attributeSetLogs.put("Property_2", List.of()));
        assertThrows(UnsupportedOperationException.class, () -> attributeSetLogs.get("Property_0").add(4.0));
    }

    @Test
    public void testAgentLogs() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        Map<String, Map<String, List<Object>>> agentLogs = immutableResults.agentLogs("Agent_0");

        assertEquals(1, agentLogs.size());
        assertEquals(List.of(1.0, 2.0), agentLogs.get("AttributeSet_0").get("Property_0"));
    }

    @Test
    public void testAgentLogs_UnmodifiableAtEveryLevel() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        Map<String, Map<String, List<Object>>> agentLogs = immutableResults.agentLogs("Agent_0");

        assertThrows(UnsupportedOperationException.class, () -> agentLogs.put("AttributeSet_1", Map.of()));
        assertThrows(UnsupportedOperationException.class, () -> agentLogs.get("AttributeSet_0").put("Property_2", List.of()));
        assertThrows(UnsupportedOperationException.class, () -> agentLogs.get("AttributeSet_0").get("Property_0").add(4.0));
    }

    @Test
    public void testAllLogs() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        Map<String, Map<String, Map<String, List<Object>>>> allLogs = immutableResults.allLogs();

        assertEquals(1, allLogs.size());
        assertEquals(List.of(1.0, 2.0), allLogs.get("Agent_0").get("AttributeSet_0").get("Property_0"));
    }

    @Test
    public void testAllLogs_UnmodifiableAtEveryLevel() {
        ReadOnlyResultsForAgents immutableResults = populatedImmutableResults();

        Map<String, Map<String, Map<String, List<Object>>>> allLogs = immutableResults.allLogs();

        assertThrows(UnsupportedOperationException.class, () -> allLogs.put("Agent_1", Map.of()));
        assertThrows(UnsupportedOperationException.class, () -> allLogs.get("Agent_0").put("AttributeSet_1", Map.of()));
        assertThrows(UnsupportedOperationException.class, () -> allLogs.get("Agent_0").get("AttributeSet_0").put("Property_2", List.of()));
        assertThrows(UnsupportedOperationException.class, () -> allLogs.get("Agent_0").get("AttributeSet_0").get("Property_0").add(4.0));
    }
}
