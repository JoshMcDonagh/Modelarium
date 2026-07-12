package unit.modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.events.functional.FunctionalAgentEvent;
import modelarium.entities.attributes.properties.functional.FunctionalAgentProperty;
import modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine;
import modelarium.entities.immutable.attributes.ImmutableAgentAttributeSet;
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
        List<Attribute<?>> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_2"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_3"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_1"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_3"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_4"));

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
    public void testGetEvent_WithIndex() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int index = 1;
        AgentEvent event = makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_2");

        List<Attribute<?>> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_1"));
        attributeList.add(event);
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_3"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_1"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_3"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_4"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(event.name(), immutableAttributeSet.getEvent(index).name());
    }

    @Test
    public void testGetEvent_WithName() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String eventName = "testEventName";

        List<Attribute<?>> attributeList = new ArrayList<>();
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_1"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,"Event_2"));
        attributeList.add(makeEmptyFunctionalEvent(FunctionalAgentEvent.class,eventName));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_1"));
        attributeList.add(makeEmptyFunctionalProperty(FunctionalAgentProperty.class, "Property_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_1"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_2"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_3"));
        attributeList.add(makeEmptyFunctionalRoutine(FunctionalAgentRoutine.class, "Routine_4"));

        ImmutableAgentAttributeSet immutableAttributeSet = makeImmutableAttributeSet(
                ImmutableAgentAttributeSet.class,
                "testAttributeSetName",
                attributeList
        );

        assertEquals(eventName, immutableAttributeSet.getEvent(eventName).name());
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
