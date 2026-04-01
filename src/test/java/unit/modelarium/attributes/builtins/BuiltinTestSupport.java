package unit.modelarium.attributes.builtins;

import modelarium.ModelClock;
import modelarium.Entity;
import modelarium.EntityAccessor;
import modelarium.attributes.functional.properties.FunctionalProperty;

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
                new modelarium.attributes.Events(),
                props,
                new modelarium.attributes.Events()
        );
    }

    public static FunctionalProperty<Double> mutableDoubleProperty(String name, double initialValue) {
        FunctionalProperty<Double> property = new FunctionalProperty<>(
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

    public static FunctionalProperty<Boolean> mutableBooleanProperty(String name, boolean initialValue) {
        FunctionalProperty<Boolean> property = new FunctionalProperty<>(
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
        EntityAccessor accessor = mock(EntityAccessor.class);
        element.setModelElementAccessor(accessor);
        element.getAttributeSetCollection().setAssociatedModelElement(element);
    }

    public static void attachClock(TestEntity element, int tick) {
        EntityAccessor accessor = mock(EntityAccessor.class);
        ModelClock clock = mock(ModelClock.class);
        when(accessor.getModelClock()).thenReturn(clock);
        when(clock.getTick()).thenReturn(tick);

        element.setModelElementAccessor(accessor);
        element.getAttributeSetCollection().setAssociatedModelElement(element);
    }
}
