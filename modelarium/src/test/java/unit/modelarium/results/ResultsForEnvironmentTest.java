package unit.modelarium.results;

import modelarium.entities.environments.Environment;
import modelarium.results.immutable.ReadOnlyResultsForEnvironment;
import modelarium.results.mutable.ResultsForEnvironment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.results.ResultsTestHelpers.*;

public class ResultsForEnvironmentTest {
    @Test
    public void testAttributeSetLogCount() {
        Environment environment = environmentWithMemoryLogs(
                "Environment_0",
                environmentAttributeSet("Environment_0", "AttributeSet_0", "Property_0"),
                environmentAttributeSet("Environment_0", "AttributeSet_1", "Property_1")
        );

        ResultsForEnvironment results = environmentResults(environment);

        assertEquals(2, results.attributeSetLogCount());
    }

    @Test
    public void testAttributeLogCount() {
        Environment environment = environmentWithMemoryLogs(
                "Environment_0",
                environmentAttributeSet("Environment_0", "AttributeSet_0", "Property_0", "Property_1", "Property_2")
        );

        ResultsForEnvironment results = environmentResults(environment);

        assertEquals(3, results.attributeLogCount("AttributeSet_0"));
    }

    @Test
    public void testAttributeLogs_PreservesOrder() {
        Environment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1, 2, 3);

        ResultsForEnvironment results = environmentResults(environment);

        List<Object> values = results.attributeLogs("AttributeSet_0", "Property_0");

        assertEquals(3, values.size());
        assertEquals(1, values.get(0));
        assertEquals(2, values.get(1));
        assertEquals(3, values.get(2));
    }

    @Test
    public void testAttributeLogs_Typed() {
        Environment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1, 2);

        ResultsForEnvironment results = environmentResults(environment);

        List<Integer> values = results.attributeLogs("AttributeSet_0", "Property_0", Integer.class);

        assertEquals(List.of(1, 2), values);
    }

    @Test
    public void testAttributeLogs_Typed_WrongType_ClassCastException() {
        Environment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        ResultsForEnvironment results = environmentResults(environment);

        assertThrows(
                ClassCastException.class,
                () -> results.attributeLogs("AttributeSet_0", "Property_0", String.class)
        );
    }

    @Test
    public void testAttributeSetLogs() {
        Environment environment = environmentWithMemoryLogs(
                "Environment_0",
                environmentAttributeSet("Environment_0", "AttributeSet_0", "Property_0", "Property_1")
        );
        record(environment, "AttributeSet_0", "Property_0", 1, 2);
        record(environment, "AttributeSet_0", "Property_1", 3);

        ResultsForEnvironment results = environmentResults(environment);

        Map<String, List<Object>> attributeSetLogs = results.attributeSetLogs("AttributeSet_0");

        assertEquals(2, attributeSetLogs.size());
        assertEquals(List.of(1, 2), attributeSetLogs.get("Property_0"));
        assertEquals(List.of(3), attributeSetLogs.get("Property_1"));
    }

    @Test
    public void testEnvironmentLogs() {
        Environment environment = environmentWithMemoryLogs(
                "Environment_0",
                environmentAttributeSet("Environment_0", "AttributeSet_0", "Property_0"),
                environmentAttributeSet("Environment_0", "AttributeSet_1", "Property_1")
        );
        record(environment, "AttributeSet_0", "Property_0", 1);
        record(environment, "AttributeSet_1", "Property_1", 2);

        ResultsForEnvironment results = environmentResults(environment);

        Map<String, Map<String, List<Object>>> environmentLogs = results.environmentLogs();

        assertEquals(2, environmentLogs.size());
        assertEquals(List.of(1), environmentLogs.get("AttributeSet_0").get("Property_0"));
        assertEquals(List.of(2), environmentLogs.get("AttributeSet_1").get("Property_1"));
    }

    @Test
    public void testDisconnectDatabases() {
        Environment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        ResultsForEnvironment results = environmentResults(environment);

        results.disconnectDatabases();

        assertNull(environment.getAttributeSet("AttributeSet_0").getLog().getValues("Property_0"));
    }

    @Test
    public void testGetAsImmutable() {
        Environment environment = environmentWithLoggedProperty("Environment_0", "AttributeSet_0", "Property_0");
        record(environment, "AttributeSet_0", "Property_0", 1);

        ResultsForEnvironment results = environmentResults(environment);

        ReadOnlyResultsForEnvironment immutableResults = results.getAsImmutable();

        assertEquals(results.attributeSetLogCount(), immutableResults.attributeSetLogCount());
        assertEquals(
                results.attributeLogs("AttributeSet_0", "Property_0"),
                immutableResults.attributeLogs("AttributeSet_0", "Property_0")
        );
    }
}
