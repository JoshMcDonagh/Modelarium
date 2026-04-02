package unit.modelarium.attributes;

import modelarium.Clock;
import modelarium.Entity;
import modelarium.contexts.Context;
import modelarium.attributes.*;
import modelarium.attributes.AttributeSet;
import modelarium.logging.AttributeSetLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link AttributeSet} class.
 *
 * <p>Verifies execution order and conditional recording of pre-events, properties, and post-events.
 */
public class AttributeSetTest {

    private AttributeSet attributeSet;
    private Events preEvents;
    private Properties properties;
    private Events postEvents;

    private AttributeSetLog results;

    @BeforeEach
    public void setup() {
        Entity mockEntity = mock(Entity.class);
        Context mockContext = mock(Context.class);
        Clock mockClock = mock(Clock.class);

        when(mockEntity.getModelElementAccessor()).thenReturn(mockContext);
        when(mockContext.getClock()).thenReturn(mockClock);

        preEvents = spy(new Events());
        properties = spy(new Properties());
        postEvents = spy(new Events());

        when(preEvents.getAssociatedModelElement()).thenReturn(mockEntity);
        when(properties.getAssociatedModelElement()).thenReturn(mockEntity);
        when(postEvents.getAssociatedModelElement()).thenReturn(mockEntity);

        attributeSet = new AttributeSet("TestSet", preEvents, properties, postEvents);
        results = mock(AttributeSetLog.class);
    }

    @Test
    public void testNameIsAssignedCorrectly() {
        assertEquals("TestSet", attributeSet.getName());
    }

    @Test
    public void testRunExecutesAllAttributeGroups() {
        attributeSet.run(results);

        verify(preEvents).run();
        verify(properties).run();
        verify(postEvents).run();
    }

    @Test
    public void testPreEventRecordingWhenMarkedAsRecorded() {
        Event recordedEvent = mock(Event.class);
        when(recordedEvent.isRecorded()).thenReturn(true);
        when(recordedEvent.getName()).thenReturn("PreEvent");
        when(recordedEvent.isTriggered()).thenReturn(true);

        preEvents.add(recordedEvent);

        attributeSet.run(results);

        verify(results).recordPreEvent("PreEvent", true);
    }

    @Test
    public void testPropertyRecordingWhenMarkedAsRecorded() {
        Property<Integer> recordedProperty = mock(Property.class);
        when(recordedProperty.isRecorded()).thenReturn(true);
        when(recordedProperty.getName()).thenReturn("Health");
        when(recordedProperty.get()).thenReturn(42);

        properties.add(recordedProperty);

        attributeSet.run(results);

        verify(results).recordProperty("Health", 42);
    }

    @Test
    public void testPostEventRecordingWhenMarkedAsRecorded() {
        Event recordedEvent = mock(Event.class);
        when(recordedEvent.isRecorded()).thenReturn(true);
        when(recordedEvent.getName()).thenReturn("PostEvent");
        when(recordedEvent.isTriggered()).thenReturn(false);

        postEvents.add(recordedEvent);

        attributeSet.run(results);

        verify(results).recordPostEvent("PostEvent", false);
    }

    @Test
    public void testAttributesNotRecordedWhenNotMarked() {
        Event unrecordedEvent = mock(Event.class);
        when(unrecordedEvent.isRecorded()).thenReturn(false);
        preEvents.add(unrecordedEvent);

        Property<Double> unrecordedProperty = mock(Property.class);
        when(unrecordedProperty.isRecorded()).thenReturn(false);
        properties.add(unrecordedProperty);

        attributeSet.run(results);

        verify(results, never()).recordPreEvent(anyString(), anyBoolean());
        verify(results, never()).recordProperty(anyString(), any());
    }
}
