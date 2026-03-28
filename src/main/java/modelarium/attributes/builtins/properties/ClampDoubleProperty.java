package modelarium.attributes.builtins.properties;

import modelarium.attributes.Property;
import modelarium.attributes.builtins.refs.DoubleValueRef;

public class ClampDoubleProperty extends Property<Double> {
    private double value;
    private final DoubleValueRef min;
    private final DoubleValueRef max;


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

    public ClampDoubleProperty(ClampDoubleProperty other) {
        super(other);
        this.value = other.value;
        this.min = other.min;
        this.max = other.max;
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
        double minValue = min.resolve(getAssociatedModelElement());
        double maxValue = max.resolve(getAssociatedModelElement());

        if (minValue > maxValue)
            throw new IllegalStateException("ClampDoubleProperty min cannot be greater than max");

        value = Math.max(minValue, Math.min(maxValue, value));
    }
}
