package unit.modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.immutable.attributes.ImmutableAgentAttributeSet;
import modelarium.entities.immutable.attributes.ImmutableAttributeSet;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
}
