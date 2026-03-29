package modelarium.attributes.builtins.properties;

import modelarium.attributes.Property;

/**
 * A {@link modelarium.attributes.Property} whose {@code Double} value is fixed at construction time
 * and cannot be changed afterwards.
 *
 * <p>This property always returns the same value from {@link #get()}. Calls to {@link #set(Double)}
 * are not permitted and result in an {@link UnsupportedOperationException}.</p>
 *
 * <p>{@link #run()} is a no-op because the property has no internal update behaviour.</p>
 *
 * <p>This class is useful for constants such as fixed rates, coefficients, thresholds, static agent
 * parameters, or any other immutable numeric value that should still be represented as a property
 * within the attribute system.</p>
 */
public class ConstantDoubleProperty extends Property<Double> {
    private final double value;

    public ConstantDoubleProperty(String name, boolean isRecorded, double value) {
        super(name, isRecorded, Double.class);
        this.value = value;
    }

    public ConstantDoubleProperty(double value) {
        super(Double.class);
        this.value = value;
    }

    public ConstantDoubleProperty(String name, double value) {
        super(name, Double.class);
        this.value = value;
    }

    public ConstantDoubleProperty(boolean isRecorded, double value) {
        super(isRecorded, Double.class);
        this.value = value;
    }

    public ConstantDoubleProperty(ConstantDoubleProperty other) {
        super(other);
        this.value = other.value;
    }

    @Override
    public void set(Double value) {
        throw new UnsupportedOperationException("ConstantDoubleProperty cannot be modified after construction");
    }

    @Override
    public Double get() {
        return value;
    }

    @Override
    public void run() {
        // no-op
    }
}
