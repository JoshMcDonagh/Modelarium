package modelarium.attributes.functional;

import modelarium.attributes.Property;

/**
 * A property whose behaviour is defined using functional interfaces.
 *
 * <p>This implementation allows dynamic configuration of property behaviour,
 * useful when working with external systems or when subclassing is not feasible
 * (e.g., in Python via JPype).</p>
 *
 * @param <T> the type of value this property holds
 */
public class FunctionalProperty<T> extends Property<T> {

    private final PropertyGetterFunction<T> getter;
    private final PropertySetterFunction<T> setter;
    private final PropertyRunFunction<T> runLogic;

    private T propertyValue = null;

    /**
     * Constructs a functional property with the given name, type, recording flag, and logic.
     *
     * @param name        the property name
     * @param isRecorded  whether the property is recorded
     * @param type        the value type of the property
     * @param getter      logic to retrieve the property value
     * @param setter      logic to update the property value
     * @param runLogic    logic to execute during the {@link #run()} call
     */
    public FunctionalProperty(
            String name,
            boolean isRecorded,
            Class<T> type,
            PropertyGetterFunction<T> getter,
            PropertySetterFunction<T> setter,
            PropertyRunFunction<T> runLogic
    ) {
        super(name, isRecorded, type);
        this.getter = getter;
        this.setter = setter;
        this.runLogic = runLogic;
    }

    public FunctionalProperty(FunctionalProperty<T> other) {
        super(other);
        this.getter = other.getter;
        this.setter = other.setter;
        this.runLogic = other.runLogic;
        this.propertyValue = other.propertyValue;
    }

    @Override
    public T get() {
        return getter.get(getAssociatedModelElement(), propertyValue);
    }

    @Override
    public void set(T value) {
        propertyValue = setter.set(getAssociatedModelElement(), propertyValue, value);
    }

    @Override
    public void run() {
        propertyValue = runLogic.run(getAssociatedModelElement(), propertyValue);
    }

    @Override
    public FunctionalProperty<T> deepCopy() {
        return new FunctionalProperty<>(this);
    }
}
