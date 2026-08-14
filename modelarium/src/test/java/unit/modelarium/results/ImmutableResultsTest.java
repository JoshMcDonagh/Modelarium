package unit.modelarium.results;

import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.results.immutable.ImmutableResults;
import modelarium.results.immutable.ImmutableResultsForAgents;
import modelarium.results.immutable.ImmutableResultsForEnvironment;
import modelarium.results.mutable.MutableResults;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.results.ResultsTestHelpers.*;

public class ImmutableResultsTest {
    private MutableResults populatedMutableResults() {
        MutableAgent agent = agentWithLoggedProperty("Agent_0", "AttributeSet_0", "Property_0");
        record(agent, "AttributeSet_0", "Property_0", 1.0);
        MutableEnvironment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        return mutableResults(agentResults(agent), environmentResults(environment));
    }

    @Test
    public void testAgents_ReturnsImmutableWrapper() {
        ImmutableResults immutableResults = new ImmutableResults(populatedMutableResults());

        assertInstanceOf(ImmutableResultsForAgents.class, immutableResults.agents());
    }

    @Test
    public void testEnvironment_ReturnsImmutableWrapper() {
        ImmutableResults immutableResults = new ImmutableResults(populatedMutableResults());

        assertInstanceOf(ImmutableResultsForEnvironment.class, immutableResults.environment());
    }

    @Test
    public void testAgents_DelegatesToWrappedResults() {
        MutableResults results = populatedMutableResults();

        ImmutableResults immutableResults = new ImmutableResults(results);

        assertEquals(results.agents().agentLogCount(), immutableResults.agents().agentLogCount());
        assertEquals(
                List.of(1.0),
                immutableResults.agents().attributeLogs("Agent_0", "AttributeSet_0", "Property_0")
        );
    }

    @Test
    public void testEnvironment_DelegatesToWrappedResults() {
        MutableResults results = populatedMutableResults();

        ImmutableResults immutableResults = new ImmutableResults(results);

        assertEquals(results.environment().attributeSetLogCount(), immutableResults.environment().attributeSetLogCount());
        assertEquals(
                List.of(1),
                immutableResults.environment().attributeLogs("AttributeSet_0", "Property_0")
        );
    }
}
