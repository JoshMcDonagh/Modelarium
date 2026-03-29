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
    double resolve(ModelElement element);
}
