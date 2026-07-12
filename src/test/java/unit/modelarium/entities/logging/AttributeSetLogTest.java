package unit.modelarium.entities.logging;

import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.logging.LoggingTestHelpers.*;

public class AttributeSetLogTest {
    @Test
    public void testGetOwnerName() {
        String ownerName = "testOwner";

        AttributeSetLog<AgentSimulationContext> log = attributeSetLog(
                ownerName,
                "testAttributeSetName",
                loggedProperty("Property_0")
        );

        assertEquals(ownerName, log.getOwnerName());
    }

    @Test
    public void testGetAttributeSetName() {
        String attributeSetName = "testAttributeSetName";

        AttributeSetLog<AgentSimulationContext> log = attributeSetLog(
                "testOwner",
                attributeSetName,
                loggedProperty("Property_0")
        );

        assertEquals(attributeSetName, log.getAttributeSetName());
    }

    @Test
    public void testGetAttributeNamesList_OnlyLoggedAttributesRegistered() {
        AttributeSetLog<AgentSimulationContext> log = attributeSetLog(
                "testOwner",
                "testAttributeSetName",
                loggedProperty("Property_0"),
                unloggedProperty("Property_1"),
                loggedEvent("Event_0")
        );

        List<String> attributeNames = log.getAttributeNamesList();

        assertEquals(2, attributeNames.size());
        assertTrue(attributeNames.contains("Property_0"));
        assertTrue(attributeNames.contains("Event_0"));
        assertFalse(attributeNames.contains("Property_1"));
    }

    @Test
    public void testGetAttributeNamesList_ReturnsDefensiveCopy() {
        AttributeSetLog<AgentSimulationContext> log = attributeSetLog(
                "testOwner",
                "testAttributeSetName",
                loggedProperty("Property_0")
        );

        log.getAttributeNamesList().clear();

        assertEquals(1, log.getAttributeNamesList().size());
    }

    @Test
    public void testGetPropertyType() {
        AttributeSetLog<AgentSimulationContext> log = attributeSetLog(
                "testOwner",
                "testAttributeSetName",
                loggedProperty("Property_0")
        );

        assertEquals(Double.class, log.getPropertyType("Property_0"));
    }

    @Test
    public void testGetPropertyType_EventReturnsNull() {
        AttributeSetLog<AgentSimulationContext> log = attributeSetLog(
                "testOwner",
                "testAttributeSetName",
                loggedEvent("Event_0")
        );

        assertNull(log.getPropertyType("Event_0"));
    }

    @Test
    public void testAttributeLogCount() {
        AttributeSetLog<AgentSimulationContext> log = attributeSetLog(
                "testOwner",
                "testAttributeSetName",
                loggedProperty("Property_0"),
                loggedProperty("Property_1"),
                unloggedProperty("Property_2")
        );

        assertEquals(2, log.attributeLogCount());
    }

    @Test
    public void testRecordAndGetValues_PreservesOrder() {
        AttributeSetLog<AgentSimulationContext> log = attributeSetLog(
                "testOwner",
                "testAttributeSetName",
                loggedProperty("Property_0")
        );

        log.record("Property_0", 1.0);
        log.record("Property_0", 2.0);
        log.record("Property_0", 3.0);

        List<Object> values = log.getValues("Property_0");

        assertEquals(3, values.size());
        assertEquals(1.0, values.get(0));
        assertEquals(2.0, values.get(1));
        assertEquals(3.0, values.get(2));
    }

    @Test
    public void testGetValues_ReturnsIndependentDeepCopy() {
        AttributeSetLog<AgentSimulationContext> log = attributeSetLog(
                "testOwner",
                "testAttributeSetName",
                loggedProperty("Property_0")
        );

        List<String> recordedValue = new ArrayList<>();
        recordedValue.add("original");
        log.record("Property_0", recordedValue);

        List<Object> earlierRead = log.getValues("Property_0");
        earlierRead.clear();
        // noinspection unchecked
        ((List<String>) log.getValues("Property_0").get(0)).add("mutated");

        List<Object> laterRead = log.getValues("Property_0");

        assertEquals(1, laterRead.size());
        assertEquals(List.of("original"), laterRead.get(0));
    }

    @Test
    public void testDisconnectDatabase() {
        AttributeSetLog<AgentSimulationContext> log = attributeSetLog(
                "testOwner",
                "testAttributeSetName",
                loggedProperty("Property_0")
        );

        log.record("Property_0", 1.0);

        log.disconnectDatabase();

        assertNull(log.getValues("Property_0"));
    }
}
