package modelarium.attributes.builtins.refs;

import modelarium.ModelElement;

public final class LiteralDoubleRef implements DoubleValueRef {
    private final double value;

    public LiteralDoubleRef(double value) {
        this.value = value;
    }

    @Override
    public double resolve(ModelElement element) {
        return value;
    }
}
