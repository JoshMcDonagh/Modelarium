package unit.modelarium.attributes.builtins;

import modelarium.Clock;
import modelarium.entities.Entity;
import modelarium.entities.attributes.properties.functional.FunctionalAgentProperty;
import modelarium.entities.contexts.Context;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class BuiltinTestSupport {
    private BuiltinTestSupport() {}

    public static class TestEntity extends Entity {
        public TestEntity(AttributeSetCollection attributeSetCollection) {
            super("test-element", attributeSetCollection);
        }

        @Override
        public void run() {
            // no-op
        }

        @Override
        public Entity clone() {
            return null;
        }
    }

    public static TestEntity elementWith(AttributeSet... attributeSets) {
        AttributeSetCollection collection = new AttributeSetCollection();
        for (AttributeSet attributeSet : attributeSets)
            collection.add(attributeSet);
        return new TestEntity(collection);
    }

    public static AttributeSet attributeSet(String name, Property<?>... properties) {
        Properties props = new Properties();
        for (Property<?> property : properties)
            props.add(property);
        return new AttributeSet(
                name,
                new modelarium.entities.attributes.Events(),
                props,
                new modelarium.entities.attributes.Events()
        );
    }

    public static FunctionalAgentProperty<Double> mutableDoubleProperty(String name, double initialValue) {
        FunctionalAgentProperty<Double> property = new FunctionalAgentProperty<>(
                name,
                true,
                Double.class,
                (element, value) -> value,
                (element, currentValue, newValue) -> newValue,
                (element, currentValue) -> currentValue
        );
        property.set(initialValue);
        return property;
    }

    public static FunctionalAgentProperty<Boolean> mutableBooleanProperty(String name, boolean initialValue) {
        FunctionalAgentProperty<Boolean> property = new FunctionalAgentProperty<>(
                name,
                true,
                Boolean.class,
                (element, value) -> value,
                (element, currentValue, newValue) -> newValue,
                (element, currentValue) -> currentValue
        );
        property.set(initialValue);
        return property;
    }

    public static void attachWithoutClock(TestEntity element) {
        Context accessor = mock(Context.class);
        element.setModelElementAccessor(accessor);
        element.getAttributeSetCollection().setAssociatedModelElement(element);
    }

    public static void attachClock(TestEntity element, int tick) {
        Context accessor = mock(Context.class);
        Clock clock = mock(Clock.class);
        when(accessor.getClock()).thenReturn(clock);
        when(clock.currentTick()).thenReturn(tick);

        element.setModelElementAccessor(accessor);
        element.getAttributeSetCollection().setAssociatedModelElement(element);
    }
}
