package unit.modelarium.results.immutable;

import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.environments.Environment;
import modelarium.results.immutable.ReadOnlyResults;
import modelarium.results.immutable.ReadOnlyResultsForAgents;
import modelarium.results.immutable.ReadOnlyResultsForEnvironment;
import modelarium.results.mutable.Results;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static unit.modelarium.results.ResultsTestHelpers.*;

public class ReadOnlyResultsTest {
    private Results populatedMutableResults() {
        Agent agent = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        record(agent, "AttributeSet_0", "Property_0", 1.0);
        Environment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        return mutableResults(agentResults(agent), environmentResults(environment));
    }

    @Test
    public void testAgents_ReturnsImmutableWrapper() {
        ReadOnlyResults immutableResults = new ReadOnlyResults(populatedMutableResults());

        assertInstanceOf(ReadOnlyResultsForAgents.class, immutableResults.agents());
    }

    @Test
    public void testEnvironment_ReturnsImmutableWrapper() {
        ReadOnlyResults immutableResults = new ReadOnlyResults(populatedMutableResults());

        assertInstanceOf(ReadOnlyResultsForEnvironment.class, immutableResults.environment());
    }

    @Test
    public void testAgents_DelegatesToWrappedResults() {
        Results results = populatedMutableResults();

        ReadOnlyResults immutableResults = new ReadOnlyResults(results);

        assertEquals(results.agents().agentLogCount(), immutableResults.agents().agentLogCount());
        assertEquals(
                List.of(1.0),
                immutableResults.agents().attributeLogs("Agent_0", "AttributeSet_0", "Property_0")
        );
    }

    @Test
    public void testEnvironment_DelegatesToWrappedResults() {
        Results results = populatedMutableResults();

        ReadOnlyResults immutableResults = new ReadOnlyResults(results);

        assertEquals(results.environment().attributeSetLogCount(), immutableResults.environment().attributeSetLogCount());
        assertEquals(
                List.of(1),
                immutableResults.environment().attributeLogs("AttributeSet_0", "Property_0")
        );
    }
}
