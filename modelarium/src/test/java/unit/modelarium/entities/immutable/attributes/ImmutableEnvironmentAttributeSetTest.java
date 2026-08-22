package unit.modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttribute;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.events.functional.FunctionalEnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.properties.functional.FunctionalEnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.attributes.routines.functional.FunctionalEnvironmentRoutine;
import modelarium.entities.attributes.sets.immutable.ImmutableEnvironmentAttributeSet;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static unit.modelarium.entities.immutable.attributes.ImmutableAttributeSetTestHelpers.*;

public class ImmutableEnvironmentAttributeSetTest {
    @Test
    public void testGetClonedAttribute() throws Throwable {
        int index = 7;
        EnvironmentAttribute attribute = makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_7");

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Attribute_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Attribute_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Attribute_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Attribute_3"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Attribute_4"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_5"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_6"));
        attributeList.add(attribute);
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_8"));

        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        EnvironmentAttribute returnedAttribute = runGetClonedAttribute(
                immutableAttributeSet,
                MutableEnvironmentAttributeSet.class,
                EnvironmentAttribute.class,
                "get",
                int.class,
                index
        );

        assertEquals(attribute.name(), returnedAttribute.name());
    }

    @Test
    public <T> void testSetClonedAttribute_IllegalArgumentException() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Attribute_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Attribute_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Attribute_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Attribute_3"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Attribute_4"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_5"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_6"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_7"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_8"));

        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
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
                        MutableEnvironmentAttributeSet.class,
                        EnvironmentAttribute.class,
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
        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
                attributeSetName,
                new ArrayList<>()
        );

        assertEquals(attributeSetName, immutableAttributeSet.name());
    }


    @Test
    public void testSize() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Property_0"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Property_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Routine_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Routine_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Routine_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Routine_3"));

        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(attributeList.size(), immutableAttributeSet.size());
    }

    @Test
    public void testGetLog() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        MutableEnvironmentAttributeSet attributeSet = makeAttributeSet(
                MutableEnvironmentAttributeSet.class,
                "testAttributeSetName",
                new ArrayList<>()
        );
        attributeSet.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());

        ImmutableEnvironmentAttributeSet immutableAttributeSet = new ImmutableEnvironmentAttributeSet(attributeSet);

        assertSame(attributeSet.getLog(), immutableAttributeSet.getLog());
    }

    @Test
    public void testGet_WithIndex() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int index = 7;
        EnvironmentAttribute attribute = makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_7");

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Attribute_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Attribute_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Attribute_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Attribute_3"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Attribute_4"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_5"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_6"));
        attributeList.add(attribute);
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Attribute_8"));

        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(attribute.name(), immutableAttributeSet.get(index).name());
    }

    @Test
    public void testGet_WithName() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String name = "testAttribute";

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Attribute_0"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Attribute_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, name));

        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(name, immutableAttributeSet.get(name).name());
    }

    @Test
    public void testGetEvent_WithIndex() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int index = 1;
        EnvironmentEvent event = makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_1");

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_0"));
        attributeList.add(event);
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Property_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Routine_0"));

        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(event.name(), immutableAttributeSet.getEvent(index).name());
    }

    @Test
    public void testGetEvent_WithName() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String name = "testEventName";

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,name));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Property_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Routine_0"));

        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(name, immutableAttributeSet.getEvent(name).name());
    }

    @Test
    public void testGetRoutine_WithIndex() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int index = 2;
        EnvironmentRoutine routine = makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class,"Routine_2");

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_1"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Property_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Routine_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Routine_1"));
        attributeList.add(routine);

        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(routine.name(), immutableAttributeSet.getRoutine(index).name());
    }

    @Test
    public void testGetRoutine_WithName() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String name = "testRoutineName";

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Property_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Routine_0"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, name));

        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(name, immutableAttributeSet.getRoutine(name).name());
    }

    @Test
    public void testGetProperty_WithIndex() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int index = 1;
        EnvironmentProperty<?> property = makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class,"Property_1");

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_2"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class, "Property_0"));
        attributeList.add(property);
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Routine_0"));

        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(property.name(), immutableAttributeSet.getProperty(index).name());
    }

    @Test
    public void testGetProperty_WithName() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String name = "testPropertyName";

        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalEnvironmentEvent.class,"Event_0"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class,"Property_0"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalEnvironmentProperty.class,name));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalEnvironmentRoutine.class, "Routine_0"));

        ImmutableEnvironmentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableEnvironmentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(name, immutableAttributeSet.getProperty(name).name());
    }
}
