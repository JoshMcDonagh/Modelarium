package unit.modelarium.results;

import modelarium.entities.environments.MutableEnvironment;
import modelarium.results.immutable.ImmutableResultsForEnvironment;
import modelarium.results.mutable.MutableResultsForEnvironment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.results.ResultsTestHelpers.*;

public class MutableResultsForEnvironmentTest {
    @Test
    public void testAttributeSetLogCount() {
        MutableEnvironment environment = environmentWithMemoryLogs(
                "Environment_0",
                environmentAttributeSet("Environment_0", "AttributeSet_0", "Property_0"),
                environmentAttributeSet("Environment_0", "AttributeSet_1", "Property_1")
        );

        MutableResultsForEnvironment results = environmentResults(environment);

        assertEquals(2, results.attributeSetLogCount());
    }

    @Test
    public void testAttributeLogCount() {
        MutableEnvironment environment = environmentWithMemoryLogs(
                "Environment_0",
                environmentAttributeSet("Environment_0", "AttributeSet_0", "Property_0", "Property_1", "Property_2")
        );

        MutableResultsForEnvironment results = environmentResults(environment);

        assertEquals(3, results.attributeLogCount("AttributeSet_0"));
    }

    @Test
    public void testAttributeLogs_PreservesOrder() {
        MutableEnvironment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1, 2, 3);

        MutableResultsForEnvironment results = environmentResults(environment);

        List<Object> values = results.attributeLogs("AttributeSet_0", "Property_0");

        assertEquals(3, values.size());
        assertEquals(1, values.get(0));
        assertEquals(2, values.get(1));
        assertEquals(3, values.get(2));
    }

    @Test
    public void testAttributeLogs_Typed() {
        MutableEnvironment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1, 2);

        MutableResultsForEnvironment results = environmentResults(environment);

        List<Integer> values = results.attributeLogs("AttributeSet_0", "Property_0", Integer.class);

        assertEquals(List.of(1, 2), values);
    }

    @Test
    public void testAttributeLogs_Typed_WrongType_ClassCastException() {
        MutableEnvironment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        MutableResultsForEnvironment results = environmentResults(environment);

        assertThrows(
                ClassCastException.class,
                () -> results.attributeLogs("AttributeSet_0", "Property_0", String.class)
        );
    }

    @Test
    public void testAttributeSetLogs() {
        MutableEnvironment environment = environmentWithMemoryLogs(
                "Environment_0",
                environmentAttributeSet("Environment_0", "AttributeSet_0", "Property_0", "Property_1")
        );
        record(environment, "AttributeSet_0", "Property_0", 1, 2);
        record(environment, "AttributeSet_0", "Property_1", 3);

        MutableResultsForEnvironment results = environmentResults(environment);

        Map<String, List<Object>> attributeSetLogs = results.attributeSetLogs("AttributeSet_0");

        assertEquals(2, attributeSetLogs.size());
        assertEquals(List.of(1, 2), attributeSetLogs.get("Property_0"));
        assertEquals(List.of(3), attributeSetLogs.get("Property_1"));
    }

    @Test
    public void testEnvironmentLogs() {
        MutableEnvironment environment = environmentWithMemoryLogs(
                "Environment_0",
                environmentAttributeSet("Environment_0", "AttributeSet_0", "Property_0"),
                environmentAttributeSet("Environment_0", "AttributeSet_1", "Property_1")
        );
        record(environment, "AttributeSet_0", "Property_0", 1);
        record(environment, "AttributeSet_1", "Property_1", 2);

        MutableResultsForEnvironment results = environmentResults(environment);

        Map<String, Map<String, List<Object>>> environmentLogs = results.environmentLogs();

        assertEquals(2, environmentLogs.size());
        assertEquals(List.of(1), environmentLogs.get("AttributeSet_0").get("Property_0"));
        assertEquals(List.of(2), environmentLogs.get("AttributeSet_1").get("Property_1"));
    }

    @Test
    public void testDisconnectDatabases() {
        MutableEnvironment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        MutableResultsForEnvironment results = environmentResults(environment);

        results.disconnectDatabases();

        assertNull(environment.getAttributeSet("AttributeSet_0").getLog().getValues("Property_0"));
    }

    @Test
    public void testGetAsImmutable() {
        MutableEnvironment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        MutableResultsForEnvironment results = environmentResults(environment);

        ImmutableResultsForEnvironment immutableResults = results.getAsImmutable();

        assertEquals(results.attributeSetLogCount(), immutableResults.attributeSetLogCount());
        assertEquals(
                results.attributeLogs("AttributeSet_0", "Property_0"),
                immutableResults.attributeLogs("AttributeSet_0", "Property_0")
        );
    }
}
