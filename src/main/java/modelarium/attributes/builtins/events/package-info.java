/**
 * Provides built-in {@link modelarium.attributes.Event} implementations for common
 * event-triggering patterns.
 *
 * <p>
 * These event classes allow model behaviour to be expressed using reusable
 * time-based and state-based triggers rather than requiring a custom event class
 * for each case. Examples include triggering at fixed tick intervals or when a
 * property value crosses a threshold.
 * </p>
 *
 * <p>
 * Built-in events typically operate on properties belonging to an associated
 * {@link modelarium.Entity} and may execute one or more
 * built-in or custom actions when triggered.
 * Some event implementations maintain internal state in order to distinguish
 * between a new trigger condition and one that is simply continuing to hold.
 * </p>
 *
 * <p>
 * These events are intended to support common modelling scenarios while remaining
 * composable with the rest of the Modelarium attribute system. Where more specific
 * or domain-dependent behaviour is required, users may implement custom
 * {@link modelarium.attributes.Event} subclasses.
 * </p>
 */
package modelarium.attributes.builtins.events;