package modelarium.entities.attributes.routines;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.EnvironmentAttribute;
import modelarium.entities.contexts.EnvironmentContext;

public non-sealed abstract class EnvironmentRoutine extends Routine<EnvironmentContext> implements EnvironmentAttribute {
    public EnvironmentRoutine(String name, AttributeAccessLevel accessLevel) {
        super(name, accessLevel);
    }
}
