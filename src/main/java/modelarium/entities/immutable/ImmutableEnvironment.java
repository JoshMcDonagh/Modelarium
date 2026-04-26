package modelarium.entities.immutable;

import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.attributes.ImmutableEnvironmentAttributeSet;
import modelarium.entities.logging.AttributeSetLog;

public final class ImmutableEnvironment extends ImmutableEntity<EnvironmentSimulationContext, EnvironmentContext, EnvironmentAttributeSet, AttributeSetLog<EnvironmentSimulationContext>> {
    public ImmutableEnvironment(Environment entity) {
        super(entity);
    }

    @Override
    public ImmutableEnvironmentAttributeSet getAttributeSet(int attributeSetIndex) {
        return new ImmutableEnvironmentAttributeSet(getMutableEntity().getAttributeSet(attributeSetIndex));
    }

    @Override
    public ImmutableEnvironmentAttributeSet getAttributeSet(String attributeSetName) {
        return new ImmutableEnvironmentAttributeSet(getMutableEntity().getAttributeSet(attributeSetName));
    }
}
