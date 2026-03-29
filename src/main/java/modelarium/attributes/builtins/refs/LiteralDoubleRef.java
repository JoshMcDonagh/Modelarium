package modelarium.attributes.builtins.refs;

import modelarium.ModelElement;

/**
 * A {@link DoubleValueRef} that always resolves to the same literal numeric value.
 *
 * <p>This implementation ignores the supplied {@link modelarium.ModelElement} and simply returns
 * the constant value given at construction time.</p>
 *
 * <p>It is useful when a built-in property, action, or event requires a fixed numeric parameter,
 * such as an increment amount or threshold.</p>
 */
public final class LiteralDoubleRef implements DoubleValueRef {
    private final double value;

    /**
     * Creates a literal numeric value reference.
     *
     * @param value the constant value to return
     */
    public LiteralDoubleRef(double value) {
        this.value = value;
    }

    /**
     * Returns this reference's constant value.
     *
     * @param element ignored
     * @return the literal value
     */
    @Override
    public double resolve(ModelElement element) {
        return value;
    }
}
