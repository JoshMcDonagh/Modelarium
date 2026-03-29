/**
 * Provides built-in reference types for resolving numeric values used by built-in
 * properties, events, and actions.
 *
 * <p>
 * The classes in this package allow a value to be supplied either as a literal
 * constant or by dynamically reading from a property on an associated
 * {@link modelarium.ModelElement}. This makes it possible to build
 * reusable built-in components whose behaviour can depend on either fixed values
 * or model state.
 * </p>
 *
 * <p>
 * Reference types in this package are especially useful for declarative model
 * configuration, where the same built-in action or property implementation may be
 * reused with different value sources.
 * </p>
 *
 * <p>
 * Implementations are expected to resolve values at runtime and may therefore
 * depend on the associated model element containing the referenced attribute set
 * and property.
 * </p>
 */
package modelarium.attributes.builtins.refs;