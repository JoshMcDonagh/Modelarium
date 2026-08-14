package unit.modelarium.entities.logging;

import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.logging.LoggingTestHelpers.*;

public class EntityLogTest {
    private EntityLog<AgentSimulationContext, AgentContext, MutableAgentAttributeSet, AttributeSetLog<AgentSimulationContext>> entityLog(
            String entityName,
            MutableAgentAttributeSet... attributeSets
    ) {
        return new EntityLog<>(entityName, List.of(attributeSets));
    }

    @Test
    public void testGetEntityName() {
        String entityName = "testEntity";

        EntityLog<AgentSimulationContext, AgentContext, MutableAgentAttributeSet, AttributeSetLog<AgentSimulationContext>> log = entityLog(
                entityName,
                agentAttributeSetWithMemoryLog(entityName, "AttributeSet_0", loggedProperty("Property_0"))
        );

        assertEquals(entityName, log.getEntityName());
    }

    @Test
    public void testGet_WithIndex() {
        String entityName = "testEntity";
        MutableAgentAttributeSet attributeSet0 = agentAttributeSetWithMemoryLog(entityName, "AttributeSet_0", loggedProperty("Property_0"));
        MutableAgentAttributeSet attributeSet1 = agentAttributeSetWithMemoryLog(entityName, "AttributeSet_1", loggedProperty("Property_1"));

        EntityLog<AgentSimulationContext, AgentContext, MutableAgentAttributeSet, AttributeSetLog<AgentSimulationContext>> log = entityLog(
                entityName,
                attributeSet0,
                attributeSet1
        );

        assertSame(attributeSet1.getLog(), log.get(1));
    }

    @Test
    public void testGet_WithName() {
        String entityName = "testEntity";
        MutableAgentAttributeSet attributeSet0 = agentAttributeSetWithMemoryLog(entityName, "AttributeSet_0", loggedProperty("Property_0"));
        MutableAgentAttributeSet attributeSet1 = agentAttributeSetWithMemoryLog(entityName, "AttributeSet_1", loggedProperty("Property_1"));

        EntityLog<AgentSimulationContext, AgentContext, MutableAgentAttributeSet, AttributeSetLog<AgentSimulationContext>> log = entityLog(
                entityName,
                attributeSet0,
                attributeSet1
        );

        assertSame(attributeSet0.getLog(), log.get("AttributeSet_0"));
    }

    @Test
    public void testAttributeSetLogCount() {
        String entityName = "testEntity";

        EntityLog<AgentSimulationContext, AgentContext, MutableAgentAttributeSet, AttributeSetLog<AgentSimulationContext>> log = entityLog(
                entityName,
                agentAttributeSetWithMemoryLog(entityName, "AttributeSet_0", loggedProperty("Property_0")),
                agentAttributeSetWithMemoryLog(entityName, "AttributeSet_1", loggedProperty("Property_1")),
                agentAttributeSetWithMemoryLog(entityName, "AttributeSet_2", loggedProperty("Property_2"))
        );

        assertEquals(3, log.attributeSetLogCount());
    }

    @Test
    public void testDisconnectDatabases() {
        String entityName = "testEntity";
        MutableAgentAttributeSet attributeSet0 = agentAttributeSetWithMemoryLog(entityName, "AttributeSet_0", loggedProperty("Property_0"));
        MutableAgentAttributeSet attributeSet1 = agentAttributeSetWithMemoryLog(entityName, "AttributeSet_1", loggedProperty("Property_1"));

        AttributeSetLog<AgentSimulationContext> attributeSetLog0 = attributeSet0.getLog();
        AttributeSetLog<AgentSimulationContext> attributeSetLog1 = attributeSet1.getLog();
        attributeSetLog0.record("Property_0", 1.0);
        attributeSetLog1.record("Property_1", 2.0);

        EntityLog<AgentSimulationContext, AgentContext, MutableAgentAttributeSet, AttributeSetLog<AgentSimulationContext>> log = entityLog(
                entityName,
                attributeSet0,
                attributeSet1
        );

        log.disconnectDatabases();

        assertEquals(0, log.attributeSetLogCount());
        assertNull(attributeSetLog0.getValues("Property_0"));
        assertNull(attributeSetLog1.getValues("Property_1"));
    }
}
