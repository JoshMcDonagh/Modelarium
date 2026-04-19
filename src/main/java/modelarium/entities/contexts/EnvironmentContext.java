package modelarium.entities.contexts;

import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;

public sealed interface EnvironmentContext extends EntityContext permits EnvironmentSimulationContext {
    Environment getThisEntity();
    EnvironmentAttributeSet getThisAttributeSet();
    Attribute<EnvironmentSimulationContext> getThisAttribute();
}
