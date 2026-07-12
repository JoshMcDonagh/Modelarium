package modelarium.entities.contexts;

import modelarium.entities.attributes.AttributeBase;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.environments.Environment;

public sealed interface EnvironmentContext extends EntityContext permits EnvironmentSimulationContext {
    Environment getThisEntity();
    EnvironmentAttributeSet getThisAttributeSet();
    AttributeBase<EnvironmentSimulationContext> getThisAttribute();
}
