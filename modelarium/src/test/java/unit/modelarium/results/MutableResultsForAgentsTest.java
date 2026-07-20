package unit.modelarium.results;

import modelarium.entities.agents.Agent;
import modelarium.results.immutable.ImmutableResultsForAgents;
import modelarium.results.mutable.MutableResultsForAgents;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.results.ResultsTestHelpers.*;

public class MutableResultsForAgentsTest {
    @Test
    public void testAgentLogCount() {
        MutableResultsForAgents results = agentResults(
                agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0"),
                agentWithLoggedProperty("Agent_1", "AttributeSet_0", "Property_0")
        );

        assertEquals(2, results.agentLogCount());
    }

    @Test
    public void testAttributeSetLogCount() {
        Agent agent = agentWithMemoryLogs(
                "Agent_0",
                agentAttributeSet("Agent_0", "AttributeSet_0", "Property_0"),
                agentAttributeSet("Agent_0", "AttributeSet_1", "Property_1")
        );

        MutableResultsForAgents results = agentResults(agent);

        assertEquals(2, results.attributeSetLogCount("Agent_0"));
    }

    @Test
    public void testAttributeLogCount() {
        Agent agent = agentWithMemoryLogs(
                "Agent_0",
                agentAttributeSet("Agent_0", "AttributeSet_0", "Property_0", "Property_1", "Property_2")
        );

        MutableResultsForAgents results = agentResults(agent);

        assertEquals(3, results.attributeLogCount("Agent_0", "AttributeSet_0"));
    }

    @Test
    public void testAttributeLogs_PreservesOrder() {
        Agent agent = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        record(agent, "AttributeSet_0", "Property_0", 1.0, 2.0, 3.0);

        MutableResultsForAgents results = agentResults(agent);

        List<Object> values = results.attributeLogs("Agent_0", "AttributeSet_0", "Property_0");

        assertEquals(3, values.size());
        assertEquals(1.0, values.get(0));
        assertEquals(2.0, values.get(1));
        assertEquals(3.0, values.get(2));
    }

    @Test
    public void testAttributeLogs_Typed() {
        Agent agent = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        record(agent, "AttributeSet_0", "Property_0", 1.0, 2.0);

        MutableResultsForAgents results = agentResults(agent);

        List<Double> values = results.attributeLogs("Agent_0", "AttributeSet_0", "Property_0", Double.class);

        assertEquals(List.of(1.0, 2.0), values);
    }

    @Test
    public void testAttributeLogs_Typed_WrongType_ClassCastException() {
        Agent agent = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        record(agent, "AttributeSet_0", "Property_0", 1.0);

        MutableResultsForAgents results = agentResults(agent);

        assertThrows(
                ClassCastException.class,
                () -> results.attributeLogs("Agent_0", "AttributeSet_0", "Property_0", String.class)
        );
    }

    @Test
    public void testAttributeSetLogs() {
        Agent agent = agentWithMemoryLogs(
                "Agent_0",
                agentAttributeSet("Agent_0", "AttributeSet_0", "Property_0", "Property_1")
        );
        record(agent, "AttributeSet_0", "Property_0", 1.0, 2.0);
        record(agent, "AttributeSet_0", "Property_1", 3.0);

        MutableResultsForAgents results = agentResults(agent);

        Map<String, List<Object>> attributeSetLogs = results.attributeSetLogs("Agent_0", "AttributeSet_0");

        assertEquals(2, attributeSetLogs.size());
        assertEquals(List.of(1.0, 2.0), attributeSetLogs.get("Property_0"));
        assertEquals(List.of(3.0), attributeSetLogs.get("Property_1"));
    }

    @Test
    public void testAgentLogs() {
        Agent agent = agentWithMemoryLogs(
                "Agent_0",
                agentAttributeSet("Agent_0", "AttributeSet_0", "Property_0"),
                agentAttributeSet("Agent_0", "AttributeSet_1", "Property_1")
        );
        record(agent, "AttributeSet_0", "Property_0", 1.0);
        record(agent, "AttributeSet_1", "Property_1", 2.0);

        MutableResultsForAgents results = agentResults(agent);

        Map<String, Map<String, List<Object>>> agentLogs = results.agentLogs("Agent_0");

        assertEquals(2, agentLogs.size());
        assertEquals(List.of(1.0), agentLogs.get("AttributeSet_0").get("Property_0"));
        assertEquals(List.of(2.0), agentLogs.get("AttributeSet_1").get("Property_1"));
    }

    @Test
    public void testAllLogs() {
        Agent agent0 = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        Agent agent1 = agentWithLoggedProperty("Agent_1", "AttributeSet_0", "Property_0");
        record(agent0, "AttributeSet_0", "Property_0", 1.0);
        record(agent1, "AttributeSet_0", "Property_0", 2.0);

        MutableResultsForAgents results = agentResults(agent0, agent1);

        Map<String, Map<String, Map<String, List<Object>>>> allLogs = results.allLogs();

        assertEquals(2, allLogs.size());
        assertEquals(List.of(1.0), allLogs.get("Agent_0").get("AttributeSet_0").get("Property_0"));
        assertEquals(List.of(2.0), allLogs.get("Agent_1").get("AttributeSet_0").get("Property_0"));
    }

    @Test
    public void testMergeWith() {
        Agent agent0 = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        Agent agent1 = agentWithLoggedProperty("Agent_1", "AttributeSet_0", "Property_0");
        record(agent0, "AttributeSet_0", "Property_0", 1.0);
        record(agent1, "AttributeSet_0", "Property_0", 2.0);

        MutableResultsForAgents results = agentResults(agent0);
        MutableResultsForAgents otherResults = agentResults(agent1);

        results.mergeWith(otherResults);

        assertEquals(2, results.agentLogCount());
        assertEquals(List.of(2.0), results.attributeLogs("Agent_1", "AttributeSet_0", "Property_0"));
    }

    @Test
    public void testDisconnectDatabases() {
        Agent agent = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        record(agent, "AttributeSet_0", "Property_0", 1.0);

        MutableResultsForAgents results = agentResults(agent);

        results.disconnectDatabases();

        assertEquals(0, results.agentLogCount());
        assertNull(agent.getAttributeSet("AttributeSet_0").getLog().getValues("Property_0"));
    }

    @Test
    public void testGetAsImmutable() {
        Agent agent = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        record(agent, "AttributeSet_0", "Property_0", 1.0);

        MutableResultsForAgents results = agentResults(agent);

        ImmutableResultsForAgents immutableResults = results.getAsImmutable();

        assertEquals(results.agentLogCount(), immutableResults.agentLogCount());
        assertEquals(
                results.attributeLogs("Agent_0", "AttributeSet_0", "Property_0"),
                immutableResults.attributeLogs("Agent_0", "AttributeSet_0", "Property_0")
        );
    }
}
