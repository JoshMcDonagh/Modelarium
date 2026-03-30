package modelarium.attributes.builtins.properties;

import modelarium.attributes.builtins.refs.DoubleValueRef;

/**
 * A mutable {@link modelarium.attributes.Property} whose current value is clamped into a resolved
 * minimum and maximum range whenever {@link #run()} is called.
 *
 * <p>The property stores an internal {@code double} value. Running the property does not otherwise
 * change that value; it only ensures that the current value lies within the bounds resolved from
 * the configured minimum and maximum
 * {@link modelarium.attributes.builtins.refs.DoubleValueRef DoubleValueRefs}.</p>
 *
 * <p>The bounds may be static or dynamic. If the resolved minimum exceeds the resolved maximum, an
 * {@link IllegalStateException} is thrown.</p>
 *
 * <p>This property is useful when some other part of the model may change a numeric value freely,
 * but the final value must remain within valid limits such as 0 to 100, a minimum stock level, or
 * a bounded physiological range.</p>
 */
public class ClampDoubleProperty extends Property<Double> {
    private double value;
    private final DoubleValueRef min;
    private final DoubleValueRef max;

    /**
     * Creates a clamped property with an explicit name and recording flag.
     *
     * @param name the property name
     * @param isRecorded whether this property should be recorded in results
     * @param initialValue the initial value
     * @param min the reference supplying the minimum allowed value
     * @param max the reference supplying the maximum allowed value
     */
    public ClampDoubleProperty(
            String name,
            boolean isRecorded,
            double initialValue,
            DoubleValueRef min,
            DoubleValueRef max
    ) {
        super(name, isRecorded, Double.class);
        this.value = initialValue;
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a clamped property with an auto-generated name and an explicit recording flag.
     *
     * @param isRecorded whether this property should be recorded in results
     * @param initialValue the initial value
     * @param min the reference supplying the minimum allowed value
     * @param max the reference supplying the maximum allowed value
     */
    public ClampDoubleProperty(
            boolean isRecorded,
            double initialValue,
            DoubleValueRef min,
            DoubleValueRef max
    ) {
        super(isRecorded, Double.class);
        this.value = initialValue;
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a clamped property with an explicit name and default recording behaviour.
     *
     * @param name the property name
     * @param initialValue the initial value
     * @param min the reference supplying the minimum allowed value
     * @param max the reference supplying the maximum allowed value
     */
    public ClampDoubleProperty(
            String name,
            double initialValue,
            DoubleValueRef min,
            DoubleValueRef max
    ) {
        super(name, Double.class);
        this.value = initialValue;
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a clamped property with default name and recording behaviour.
     *
     * @param initialValue the initial value
     * @param min the reference supplying the minimum allowed value
     * @param max the reference supplying the maximum allowed value
     */
    public ClampDoubleProperty(
            double initialValue,
            DoubleValueRef min,
            DoubleValueRef max
    ) {
        super(Double.class);
        this.value = initialValue;
        this.min = min;
        this.max = max;
    }

    /**
     * Copy constructor.
     *
     * @param other the property to copy
     */
    public ClampDoubleProperty(ClampDoubleProperty other) {
        super(other);
        this.value = other.value;
        this.min = other.min;
        this.max = other.max;
    }

    /**
     * Sets the property's current stored value directly.
     *
     * <p>This method does not automatically enforce the minimum or maximum bounds.
     * Bounds are enforced when {@link #run()} is called.</p>
     *
     * @param value the new value
     */
    @Override
    public void set(Double value) {
        this.value = value;
    }

    /**
     * Returns the property's current stored value.
     *
     * @return the current value
     */
    @Override
    public Double get() {
        return value;
    }

    /**
     * Clamps the current value into the resolved minimum and maximum range.
     *
     * @throws IllegalStateException if the resolved minimum is greater than the
     * resolved maximum
     */
    @Override
    public void run() {
        double minValue = min.resolve(getAssociatedModelElement());
        double maxValue = max.resolve(getAssociatedModelElement());

        if (minValue > maxValue)
            throw new IllegalStateException("ClampDoubleProperty min cannot be greater than max");

        value = Math.max(minValue, Math.min(maxValue, value));
    }
}
