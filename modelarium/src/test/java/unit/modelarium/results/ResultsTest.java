package unit.modelarium.results;

import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.environments.Environment;
import modelarium.results.immutable.ReadOnlyResults;
import modelarium.results.mutable.Results;
import modelarium.results.mutable.ResultsForAgents;
import modelarium.results.mutable.ResultsForEnvironment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.results.ResultsTestHelpers.*;

public class ResultsTest {
    @Test
    public void testSetAgentNames_WithAgentSet() {
        Results results = new Results();

        results.setAgentNames(agentSet(
                agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0"),
                agentWithLoggedProperty("Agent_1", "AttributeSet_0", "Property_0")
        ));

        assertEquals(List.of("Agent_0", "Agent_1"), results.getAgentNames());
    }

    @Test
    public void testSetAgentNames_WithAgentSetList() {
        Results results = new Results();

        results.setAgentNames(List.of(
                agentSet(agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0")),
                agentSet(agentWithLoggedProperty("Agent_1", "AttributeSet_0", "Property_0"))
        ));

        assertEquals(List.of("Agent_0", "Agent_1"), results.getAgentNames());
    }

    @Test
    public void testGetAgentNames_ReturnsDefensiveCopy() {
        Results results = new Results();
        results.setAgentNames(agentSet(agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0")));

        results.getAgentNames().clear();

        assertEquals(List.of("Agent_0"), results.getAgentNames());
    }

    @Test
    public void testSetAgentResults() {
        Results results = new Results();
        ResultsForAgents agentsResults = agentResults(
                agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0")
        );

        results.setAgentResults(agentsResults);

        assertSame(agentsResults, results.agents());
    }

    @Test
    public void testSetEnvironmentResults() {
        Results results = new Results();
        ResultsForEnvironment environmentResults = environmentResults(
                environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0")
        );

        results.setEnvironmentResults(environmentResults);

        assertSame(environmentResults, results.environment());
    }

    @Test
    public void testAgents_NullBeforeSet() {
        Results results = new Results();

        assertNull(results.agents());
    }

    @Test
    public void testEnvironment_NullBeforeSet() {
        Results results = new Results();

        assertNull(results.environment());
    }

    @Test
    public void testDisconnectDatabases() {
        Agent agent = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        record(agent, "AttributeSet_0", "Property_0", 1.0);
        Environment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        Results results = mutableResults(agentResults(agent), environmentResults(environment));

        results.disconnectDatabases();

        assertEquals(0, results.agents().agentLogCount());
        assertNull(agent.getAttributeSet("AttributeSet_0").getLog().getValues("Property_0"));
        assertNull(environment.getAttributeSet("AttributeSet_0").getLog().getValues("Property_0"));
    }

    @Test
    public void testDisconnectDatabases_NotConnected() {
        Results results = new Results();

        assertDoesNotThrow(results::disconnectDatabases);
    }

    @Test
    public void testDisconnectDatabases_CalledTwice() {
        Results results = mutableResults(
                agentResults(agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0")),
                environmentResults(environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0"))
        );

        results.disconnectDatabases();

        assertDoesNotThrow(results::disconnectDatabases);
    }

    @Test
    public void testSetAgentResults_NullDoesNotConnect() {
        Results results = new Results();

        results.setAgentResults(null);

        assertNull(results.agents());
        assertDoesNotThrow(results::disconnectDatabases);
    }

    @Test
    public void testSetEnvironmentResults_NullDoesNotConnect() {
        Results results = new Results();

        results.setEnvironmentResults(null);

        assertNull(results.environment());
        assertDoesNotThrow(results::disconnectDatabases);
    }

    @Test
    public void testMergeAgentsWith() {
        Agent agent0 = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        Agent agent1 = agentWithLoggedProperty("Agent_1", "AttributeSet_0", "Property_0");
        record(agent1, "AttributeSet_0", "Property_0", 2.0);

        Results results = new Results();
        results.setAgentResults(agentResults(agent0));

        Results otherResults = new Results();
        otherResults.setAgentResults(agentResults(agent1));

        results.mergeAgentsWith(otherResults);

        assertEquals(2, results.agents().agentLogCount());
        assertEquals(List.of(2.0), results.agents().attributeLogs("Agent_1", "AttributeSet_0", "Property_0"));
    }

    @Test
    public void testGetAsImmutable() {
        Agent agent = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        record(agent, "AttributeSet_0", "Property_0", 1.0);
        Environment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        Results results = mutableResults(agentResults(agent), environmentResults(environment));

        ReadOnlyResults immutableResults = results.getAsImmutable();

        assertEquals(results.agents().agentLogCount(), immutableResults.agents().agentLogCount());
        assertEquals(results.environment().attributeSetLogCount(), immutableResults.environment().attributeSetLogCount());
    }
}
