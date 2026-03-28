package modelarium.attributes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Represents a stateful, typed property attribute.
 *
 * <p>Properties hold and expose a value of type {@code T}, and are updated during each tick
 * via the {@link #run()} method. Property values may represent counters, flags, metrics, etc.
 *
 * @param <T> the type of value this property holds
 */
public abstract class Property<T> extends Attribute {

    /** Global counter to assign default property names */
    private static int propertyCount = 0;

    /** The runtime type of the property's value */
    private final Class<T> type;

    /**
     * Constructs a property with a specific name, recording flag, and type.
     *
     * @param name the property name
     * @param isRecorded whether it is recorded
     * @param type the class of the property's value type
     */
    public Property(String name, boolean isRecorded, Class<T> type) {
        super(name != null ? name : "Property " + propertyCount, isRecorded);
        this.type = type;
        propertyCount++;
    }

    /**
     * Constructs a property with a generated name, a recording flag, and type.
     *
     * @param isRecorded whether the property is recorded
     * @param type the class of the property's value type
     */
    public Property(boolean isRecorded, Class<T> type) {
        this("Property " + propertyCount++, isRecorded, type);
    }

    /**
     * Constructs a property with a specific name, recording enabled, and type.
     *
     * @param name the property name
     * @param type the class of the property's value type
     */
    public Property(String name, Class<T> type) {
        this(name, true, type);
    }

    /**
     * Constructs a property with a generated name, recording enabled, and type.
     *
     * @param type the class of the property's value type
     */
    public Property(Class<T> type) {
        this("Property " + propertyCount++, true, type);
    }

    /**
     * Constructs a deep copy property from a given property.
     *
     * @param other the property to deep copy
     */
    protected Property(Property<T> other) {
        super(other);
        this.type = other.type;
    }

    /**
     * Gets the runtime type of this property's value.
     *
     * @return the value type class
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Updates the value of this property.
     *
     * @param value the new value to assign
     */
    public abstract void set(T value);

    /**
     * Retrieves the current value of this property.
     *
     * @return the value of the property
     */
    public abstract T get();

    /**
     * Executes logic for updating or using the property's value.
     */
    @Override
    public abstract void run();

    @Override
    @SuppressWarnings("unchecked")
    public Property<T> deepCopy() {
        try {
            Class<? extends Property> clazz = this.getClass();
            Constructor<? extends Property> copyCtor = clazz.getDeclaredConstructor(clazz);
            copyCtor.setAccessible(true);
            return copyCtor.newInstance(this);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "Property type " + this.getClass().getName()
                            + " must provide a copy constructor taking its own type",
                    e
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to deep copy Property of type " + this.getClass().getName(),
                    e
            );
        }
    }
}
