package modelarium.entities.attributes;

import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;

public sealed interface EnvironmentAttribute extends Attribute permits EnvironmentEvent, EnvironmentProperty, EnvironmentRoutine {
}
