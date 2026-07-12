package unit.modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.*;
import modelarium.entities.attributes.events.functional.FunctionalEnvironmentEvent;
import modelarium.entities.attributes.properties.functional.FunctionalEnvironmentProperty;
import modelarium.entities.attributes.routines.functional.FunctionalEnvironmentRoutine;
import modelarium.entities.immutable.attributes.ImmutableEnvironmentAttributeSet;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static unit.modelarium.entities.immutable.attributes.ImmutableAttributeSetTestHelpers.*;
import static unit.modelarium.entities.immutable.attributes.ImmutableAttributeSetTestHelpers.makeEmptyFunctionalProperty;
import static unit.modelarium.entities.immutable.attributes.ImmutableAttributeSetTestHelpers.makeEmptyFunctionalRoutine;
import static unit.modelarium.entities.immutable.attributes.ImmutableAttributeSetTestHelpers.makeImmutableAttributeSet;
import static unit.modelarium.entities.immutable.attributes.ImmutableAttributeSetTestHelpers.runGetClonedAttribute;

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
                EnvironmentAttributeSet.class,
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
                        EnvironmentAttributeSet.class,
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
}
