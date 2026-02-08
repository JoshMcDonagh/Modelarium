package modelarium.attributes.builtins.properties;

import modelarium.attributes.Property;
import modelarium.attributes.builtins.refs.DoubleValueRef;

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
