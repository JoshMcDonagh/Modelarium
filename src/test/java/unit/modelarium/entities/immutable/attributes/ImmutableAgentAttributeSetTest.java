package unit.modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.immutable.attributes.ImmutableAgentAttributeSet;
import modelarium.entities.immutable.attributes.ImmutableAttributeSet;
import modelarium.entities.logging.AttributeSetLog;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static unit.modelarium.entities.immutable.attributes.ImmutableAttributeSetTestHelpers.makeAttributeSet;
import static unit.modelarium.entities.immutable.attributes.ImmutableAttributeSetTestHelpers.makeImmutableAttributeSet;

public class ImmutableAgentAttributeSetTest {
    @Test
    public void testGetMutableAttributeSet() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        AgentAttributeSet attributeSet = makeAttributeSet(
                AgentAttributeSet.class,
                "testAttributeSetName",
                new ArrayList<>()
        );

        Method getMutableAttributeSetMethod = ImmutableAttributeSet.class.getDeclaredMethod("getMutableAttributeSet");
        getMutableAttributeSetMethod.setAccessible(true);
        AgentAttributeSet returnedAttributeSet = (AgentAttributeSet) getMutableAttributeSetMethod.invoke(
                new ImmutableAgentAttributeSet(attributeSet)
        );

        assertSame(attributeSet, returnedAttributeSet);
    }

    @Test
    public void testName() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String attributeSetName = "testAttributeSetName";
        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                attributeSetName,
                new ArrayList<>()
        );

        assertEquals(attributeSetName, immutableAttributeSet.name());
    }

    @Test
    public void testGetLog() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        AttributeSetLog<?> attributeSetLog = mock(AttributeSetLog.class);
        AgentAttributeSet attributeSet = spy(makeAttributeSet(
                AgentAttributeSet.class,
                "testAttributeSetName",
                new ArrayList<>()
        ));
        doReturn(attributeSetLog).when(attributeSet).getLog();

        ImmutableAgentAttributeSet immutableAgentAttributeSet = new ImmutableAgentAttributeSet(attributeSet);

        assertSame(attributeSetLog, immutableAgentAttributeSet.getLog());
    }

    @Test
    public void testGetEvent_WithIndex() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetEvent_WithName() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetRoutine_WithIndex() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetRoutine_WithName() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetProperty_WithIndex() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetProperty_WithName() {
        fail("Not yet implemented");
    }
}
