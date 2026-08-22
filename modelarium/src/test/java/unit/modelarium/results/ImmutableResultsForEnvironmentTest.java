package unit.modelarium.results;

import modelarium.entities.environments.Environment;
import modelarium.results.immutable.ImmutableResultsForEnvironment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static unit.modelarium.results.ResultsTestHelpers.*;

public class ImmutableResultsForEnvironmentTest {
    private ImmutableResultsForEnvironment populatedImmutableResults() {
        Environment environment = environmentWithMemoryLogs(
                "Environment_0",
                environmentAttributeSet("Environment_0", "AttributeSet_0", "Property_0", "Property_1")
        );
        record(environment, "AttributeSet_0", "Property_0", 1, 2);
        record(environment, "AttributeSet_0", "Property_1", 3);

        return new ImmutableResultsForEnvironment(environmentResults(environment));
    }

    @Test
    public void testAttributeSetLogCount() {
        ImmutableResultsForEnvironment immutableResults = populatedImmutableResults();

        assertEquals(1, immutableResults.attributeSetLogCount());
    }

    @Test
    public void testAttributeLogCount() {
        ImmutableResultsForEnvironment immutableResults = populatedImmutableResults();

        assertEquals(2, immutableResults.attributeLogCount("AttributeSet_0"));
    }

    @Test
    public void testAttributeLogs() {
        ImmutableResultsForEnvironment immutableResults = populatedImmutableResults();

        List<Object> values = immutableResults.attributeLogs("AttributeSet_0", "Property_0");

        assertEquals(List.of(1, 2), values);
    }

    @Test
    public void testAttributeLogs_Unmodifiable() {
        ImmutableResultsForEnvironment immutableResults = populatedImmutableResults();

        List<Object> values = immutableResults.attributeLogs("AttributeSet_0", "Property_0");

        assertThrows(UnsupportedOperationException.class, () -> values.add(4));
    }

    @Test
    public void testAttributeLogs_Typed() {
        ImmutableResultsForEnvironment immutableResults = populatedImmutableResults();

        List<Integer> values = immutableResults.attributeLogs("AttributeSet_0", "Property_0", Integer.class);

        assertEquals(List.of(1, 2), values);
    }

    @Test
    public void testAttributeLogs_Typed_Unmodifiable() {
        ImmutableResultsForEnvironment immutableResults = populatedImmutableResults();

        List<Integer> values = immutableResults.attributeLogs("AttributeSet_0", "Property_0", Integer.class);

        assertThrows(UnsupportedOperationException.class, () -> values.add(4));
    }

    @Test
    public void testAttributeSetLogs() {
        ImmutableResultsForEnvironment immutableResults = populatedImmutableResults();

        Map<String, List<Object>> attributeSetLogs = immutableResults.attributeSetLogs("AttributeSet_0");

        assertEquals(2, attributeSetLogs.size());
        assertEquals(List.of(1, 2), attributeSetLogs.get("Property_0"));
        assertEquals(List.of(3), attributeSetLogs.get("Property_1"));
    }

    @Test
    public void testAttributeSetLogs_UnmodifiableAtEveryLevel() {
        ImmutableResultsForEnvironment immutableResults = populatedImmutableResults();

        Map<String, List<Object>> attributeSetLogs = immutableResults.attributeSetLogs("AttributeSet_0");

        assertThrows(UnsupportedOperationException.class, () -> attributeSetLogs.put("Property_2", List.of()));
        assertThrows(UnsupportedOperationException.class, () -> attributeSetLogs.get("Property_0").add(4));
    }

    @Test
    public void testEnvironmentLogs() {
        ImmutableResultsForEnvironment immutableResults = populatedImmutableResults();

        Map<String, Map<String, List<Object>>> environmentLogs = immutableResults.environmentLogs();

        assertEquals(1, environmentLogs.size());
        assertEquals(List.of(1, 2), environmentLogs.get("AttributeSet_0").get("Property_0"));
    }

    @Test
    public void testEnvironmentLogs_UnmodifiableAtEveryLevel() {
        ImmutableResultsForEnvironment immutableResults = populatedImmutableResults();

        Map<String, Map<String, List<Object>>> environmentLogs = immutableResults.environmentLogs();

        assertThrows(UnsupportedOperationException.class, () -> environmentLogs.put("AttributeSet_1", Map.of()));
        assertThrows(UnsupportedOperationException.class, () -> environmentLogs.get("AttributeSet_0").put("Property_2", List.of()));
        assertThrows(UnsupportedOperationException.class, () -> environmentLogs.get("AttributeSet_0").get("Property_0").add(4));
    }
}
