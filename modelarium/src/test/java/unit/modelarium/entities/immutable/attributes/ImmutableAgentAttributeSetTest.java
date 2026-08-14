package unit.modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.AgentAttribute;
import modelarium.entities.attributes.sets.mutable.MutableAgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.events.functional.FunctionalAgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.functional.FunctionalAgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine;
import modelarium.entities.attributes.sets.immutable.ImmutableAgentAttributeSet;
import modelarium.entities.logging.AttributeSetLog;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static unit.modelarium.entities.immutable.attributes.ImmutableAttributeSetTestHelpers.*;

public class ImmutableAgentAttributeSetTest {
    @Test
    public void testGetClonedAttribute() throws Throwable {
        int index = 7;
        AgentAttribute attribute = makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_7");

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Attribute_3"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Attribute_4"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_5"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_6"));
        attributeList.add(attribute);
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_8"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        AgentAttribute returnedAttribute = runGetClonedAttribute(
                immutableAttributeSet,
                MutableAgentAttributeSet.class,
                AgentAttribute.class,
                "get",
                int.class,
                index
        );

        assertEquals(attribute.name(), returnedAttribute.name());
    }

    @Test
    public <T> void testSetClonedAttribute_IllegalArgumentException() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Attribute_3"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Attribute_4"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_5"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_6"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_7"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_8"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        String getterMethodName = "wrongGetterName";
        Class<T> attributeIdClass = (Class<T>) String.class;
        Object attributeId = "attributeId";

        assertCorrectExceptionThrown(
                IllegalArgumentException.class,
                () -> runGetClonedAttribute(
                        immutableAttributeSet,
                        MutableAgentAttributeSet.class,
                        AgentAttribute.class,
                        getterMethodName,
                        attributeIdClass,
                        attributeIdClass.cast(attributeId)
                ),
                "Method '" + getterMethodName + "' taking a '" + attributeIdClass.getName() + "' not found",
                NoSuchMethodException.class
        );
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
    public void testSize() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_0"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_3"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(attributeList.size(), immutableAttributeSet.size());
    }

    @Test
    public void testGetLog() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        AttributeSetLog<?> attributeSetLog = mock(AttributeSetLog.class);
        MutableAgentAttributeSet attributeSet = spy(makeAttributeSet(
                MutableAgentAttributeSet.class,
                "testAttributeSetName",
                new ArrayList<>()
        ));
        doReturn(attributeSetLog).when(attributeSet).getLog();

        ImmutableAgentAttributeSet immutableAgentAttributeSet = new ImmutableAgentAttributeSet(attributeSet);

        assertSame(attributeSetLog, immutableAgentAttributeSet.getLog());
    }

    @Test
    public void testGet_WithIndex() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int index = 7;
        AgentAttribute attribute = makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_7");

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Attribute_3"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Attribute_4"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_5"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_6"));
        attributeList.add(attribute);
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_8"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(attribute.name(), immutableAttributeSet.get(index).name());
    }

    @Test
    public void testGet_WithName() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String name = "testAttribute";

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Attribute_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Attribute_3"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Attribute_4"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_5"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_6"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, name));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Attribute_8"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(name, immutableAttributeSet.get(name).name());
    }

    @Test
    public void testGetEvent_WithIndex() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int index = 1;
        AgentEvent event = makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_1");

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_0"));
        attributeList.add(event);
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_0"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_3"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(event.name(), immutableAttributeSet.getEvent(index).name());
    }

    @Test
    public void testGetEvent_WithName() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String name = "testEventName";

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,name));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_0"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_3"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(name, immutableAttributeSet.getEvent(name).name());
    }

    @Test
    public void testGetRoutine_WithIndex() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int index = 2;
        AgentRoutine routine = makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class,"Routine_2");

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_0"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_1"));
        attributeList.add(routine);
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_3"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(routine.name(), immutableAttributeSet.getRoutine(index).name());
    }

    @Test
    public void testGetRoutine_WithName() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String name = "testRoutineName";

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_0"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, name));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(name, immutableAttributeSet.getRoutine(name).name());
    }

    @Test
    public void testGetProperty_WithIndex() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int index = 0;
        AgentProperty<?> property = makeEmptyFunctionalProperty(FunctionalAgentProperty.class,"Property_0");

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_2"));
        attributeList.add(property);
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_3"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(property.name(), immutableAttributeSet.getProperty(index).name());
    }

    @Test
    public void testGetProperty_WithName() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String name = "testPropertyName";

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class,"Property_0"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class,name));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_3"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(name, immutableAttributeSet.getProperty(name).name());
    }
}
