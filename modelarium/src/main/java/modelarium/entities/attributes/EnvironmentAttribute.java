package modelarium.entities.attributes;

import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;

/**
 * Interface for marking an attribute as belonging to the environment.
 *
 * <p>This interface is implemented by the environment-flavoured attribute types: {@link EnvironmentEvent},
 * {@link EnvironmentProperty} and {@link EnvironmentRoutine}.
 */
public sealed interface EnvironmentAttribute extends Attribute permits EnvironmentEvent, EnvironmentProperty, EnvironmentRoutine {
}
