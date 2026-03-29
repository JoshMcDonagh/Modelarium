package modelarium.attributes.builtins.properties;

import modelarium.attributes.Property;
import modelarium.attributes.builtins.refs.DoubleValueRef;

/**
 * A mutable {@link modelarium.attributes.Property} whose value increases by a resolved delta each
 * time {@link #run()} is called.
 *
 * <p>The property stores an internal {@code double} value. On each run, that value is incremented
 * by the amount resolved from the configured
 * {@link modelarium.attributes.builtins.refs.DoubleValueRef}.</p>
 *
 * <p>The delta may be a literal number or may be obtained dynamically from another property on the
 * associated model element. This makes the property suitable for simple accumulators whose update
 * rule is additive.</p>
 *
 * <p>This class is useful for modelling quantities such as hunger, fatigue, elapsed exposure, stock
 * growth, or other values that change by repeated addition over time.</p>
 */
public class AddDoubleProperty extends Property<Double> {
    private double value;
    private final DoubleValueRef delta;

    public AddDoubleProperty(String name, boolean isRecorded, double initialValue, DoubleValueRef delta) {
        super(name, isRecorded, Double.class);
        this.value = initialValue;
        this.delta = delta;
    }

    public AddDoubleProperty(boolean isRecorded, double initialValue, DoubleValueRef delta) {
        super(isRecorded, Double.class);
        this.value = initialValue;
        this.delta = delta;
    }

    public AddDoubleProperty(String name, double initialValue, DoubleValueRef delta) {
        super(name, Double.class);
        this.value = initialValue;
        this.delta = delta;
    }

    public AddDoubleProperty(double initialValue, DoubleValueRef delta) {
        super(Double.class);
        this.value = initialValue;
        this.delta = delta;
    }

    public AddDoubleProperty(AddDoubleProperty other) {
        super(other);
        this.value = other.value;
        this.delta = other.delta;
    }

    @Override
    public void set(Double value) {
        this.value = value;
    }

    @Override
    public Double get() {
        return value;
    }

    @Override
    public void run() {
        value += delta.resolve(getAssociatedModelElement());
    }
}
