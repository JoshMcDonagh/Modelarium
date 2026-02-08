package modelarium.attributes.builtins.properties;

import modelarium.attributes.Property;

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
