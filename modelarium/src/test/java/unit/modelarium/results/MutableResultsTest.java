package unit.modelarium.results;

import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.results.immutable.ImmutableResults;
import modelarium.results.mutable.MutableResults;
import modelarium.results.mutable.MutableResultsForAgents;
import modelarium.results.mutable.MutableResultsForEnvironment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.results.ResultsTestHelpers.*;

public class MutableResultsTest {
    @Test
    public void testSetAgentNames_WithAgentSet() {
        MutableResults results = new MutableResults();

        results.setAgentNames(agentSet(
                agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0"),
                agentWithLoggedProperty("Agent_1", "AttributeSet_0", "Property_0")
        ));

        assertEquals(List.of("Agent_0", "Agent_1"), results.getAgentNames());
    }

    @Test
    public void testSetAgentNames_WithAgentSetList() {
        MutableResults results = new MutableResults();

        results.setAgentNames(List.of(
                agentSet(agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0")),
                agentSet(agentWithLoggedProperty("Agent_1", "AttributeSet_0", "Property_0"))
        ));

        assertEquals(List.of("Agent_0", "Agent_1"), results.getAgentNames());
    }

    @Test
    public void testGetAgentNames_ReturnsDefensiveCopy() {
        MutableResults results = new MutableResults();
        results.setAgentNames(agentSet(agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0")));

        results.getAgentNames().clear();

        assertEquals(List.of("Agent_0"), results.getAgentNames());
    }

    @Test
    public void testSetAgentResults() {
        MutableResults results = new MutableResults();
        MutableResultsForAgents agentsResults = agentResults(
                agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0")
        );

        results.setAgentResults(agentsResults);

        assertSame(agentsResults, results.agents());
    }

    @Test
    public void testSetEnvironmentResults() {
        MutableResults results = new MutableResults();
        MutableResultsForEnvironment environmentResults = environmentResults(
                environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0")
        );

        results.setEnvironmentResults(environmentResults);

        assertSame(environmentResults, results.environment());
    }

    @Test
    public void testAgents_NullBeforeSet() {
        MutableResults results = new MutableResults();

        assertNull(results.agents());
    }

    @Test
    public void testEnvironment_NullBeforeSet() {
        MutableResults results = new MutableResults();

        assertNull(results.environment());
    }

    @Test
    public void testDisconnectDatabases() {
        MutableAgent agent = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        record(agent, "AttributeSet_0", "Property_0", 1.0);
        MutableEnvironment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        MutableResults results = mutableResults(agentResults(agent), environmentResults(environment));

        results.disconnectDatabases();

        assertEquals(0, results.agents().agentLogCount());
        assertNull(agent.getAttributeSet("AttributeSet_0").getLog().getValues("Property_0"));
        assertNull(environment.getAttributeSet("AttributeSet_0").getLog().getValues("Property_0"));
    }

    @Test
    public void testDisconnectDatabases_NotConnected() {
        MutableResults results = new MutableResults();

        assertDoesNotThrow(results::disconnectDatabases);
    }

    @Test
    public void testDisconnectDatabases_CalledTwice() {
        MutableResults results = mutableResults(
                agentResults(agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0")),
                environmentResults(environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0"))
        );

        results.disconnectDatabases();

        assertDoesNotThrow(results::disconnectDatabases);
    }

    @Test
    public void testSetAgentResults_NullDoesNotConnect() {
        MutableResults results = new MutableResults();

        results.setAgentResults(null);

        assertNull(results.agents());
        assertDoesNotThrow(results::disconnectDatabases);
    }

    @Test
    public void testSetEnvironmentResults_NullDoesNotConnect() {
        MutableResults results = new MutableResults();

        results.setEnvironmentResults(null);

        assertNull(results.environment());
        assertDoesNotThrow(results::disconnectDatabases);
    }

    @Test
    public void testMergeAgentsWith() {
        MutableAgent agent0 = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        MutableAgent agent1 = agentWithLoggedProperty("Agent_1", "AttributeSet_0", "Property_0");
        record(agent1, "AttributeSet_0", "Property_0", 2.0);

        MutableResults results = new MutableResults();
        results.setAgentResults(agentResults(agent0));

        MutableResults otherResults = new MutableResults();
        otherResults.setAgentResults(agentResults(agent1));

        results.mergeAgentsWith(otherResults);

        assertEquals(2, results.agents().agentLogCount());
        assertEquals(List.of(2.0), results.agents().attributeLogs("Agent_1", "AttributeSet_0", "Property_0"));
    }

    @Test
    public void testGetAsImmutable() {
        MutableAgent agent = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        record(agent, "AttributeSet_0", "Property_0", 1.0);
        MutableEnvironment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        MutableResults results = mutableResults(agentResults(agent), environmentResults(environment));

        ImmutableResults immutableResults = results.getAsImmutable();

        assertEquals(results.agents().agentLogCount(), immutableResults.agents().agentLogCount());
        assertEquals(results.environment().attributeSetLogCount(), immutableResults.environment().attributeSetLogCount());
    }
}
