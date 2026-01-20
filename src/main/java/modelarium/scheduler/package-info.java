/**
 * Scheduling and execution control for Modelarium simulations.
 *
 * <p>This package defines how simulation steps are ordered and executed
 * over time. The scheduler is responsible for coordinating when agents,
 * environments, and other model components are updated during a simulation run.</p>
 *
 * <p>The scheduler governs execution order and update frequency, but does not
 * define the behaviour of individual model components. Behaviour is implemented
 * within agents and other model elements, and invoked according to the
 * scheduling policy in effect.</p>
 *
 * <p>Different scheduler implementations may provide varying guarantees with
 * respect to ordering, determinism, or performance, and can be selected or
 * configured at model setup time.</p>
 */
package modelarium.scheduler;
