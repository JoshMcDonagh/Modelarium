/**
 * Provides built-in {@link modelarium.attributes.Property} implementations for
 * common property behaviours.
 *
 * <p>
 * The classes in this package support reusable property patterns such as constant
 * values, additive updates, clamped numeric values, and properties whose value is
 * derived from another property or value reference.
 * These built-in properties are intended to reduce boilerplate for simple models
 * and to provide standard implementations of frequently needed behaviour.
 * </p>
 *
 * <p>
 * Some built-in properties are directly mutable, whereas others are derived or
 * constrained and therefore impose restrictions on how their value may be set.
 * Users should consult the documentation of each class to understand whether the
 * property stores its own value, derives it dynamically, or enforces bounds or
 * immutability.
 * </p>
 *
 * <p>
 * These classes can be used directly in models or serve as examples of how custom
 * {@link modelarium.attributes.Property} subclasses may be implemented.
 * </p>
 */
package modelarium.attributes.builtins.properties;