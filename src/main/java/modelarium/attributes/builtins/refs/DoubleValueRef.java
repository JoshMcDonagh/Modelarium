package modelarium.attributes.builtins.refs;

import modelarium.ModelElement;

/**
 * Resolves a {@code double} value in the context of a {@link modelarium.ModelElement}.
 *
 * <p>Built-in properties and actions use {@code DoubleValueRef} to allow numeric inputs to be
 * specified either as literal constants or as values obtained dynamically from another property on
 * the model element.</p>
 *
 * <p>This indirection keeps built-in behaviours simple while still allowing them to be parameterised
 * by model state.</p>
 */
public interface DoubleValueRef {
    /**
     * Resolves a numeric value for the supplied model element.
     *
     * @param element the model element against which the value should be resolved
     * @return the resolved value
     */
    double resolve(ModelElement element);
}
