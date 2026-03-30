/**
 * Provides built-in {@link modelarium.attributes.builtins.actions.Action} implementations
 * that perform common mutations on properties belonging to a
 * {@link modelarium.Entity}.
 *
 * <p>
 * These actions are intended to be used by built-in or custom
 * {@link modelarium.attributes.Event} implementations in order to apply simple,
 * reusable state changes without requiring a bespoke action class for every model.
 * Typical uses include assigning a new value to a property, incrementing a numeric
 * property, or toggling a boolean property.
 * </p>
 *
 * <p>
 * Built-in actions generally resolve their target property at runtime from the
 * associated model element, usually by attribute set name and property name.
 * They therefore depend on the target model element containing a compatible
 * property with the expected type.
 * </p>
 *
 * <p>
 * This package is primarily intended for straightforward, declarative model logic.
 * For more complex behaviour, users may instead implement custom event or action
 * classes directly.
 * </p>
 */
package modelarium.attributes.builtins.actions;