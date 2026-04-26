package modelarium.entities.immutable;

import modelarium.entities.Entity;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.immutable.attributes.ImmutableAttributeSet;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;

public sealed abstract class ImmutableEntity<SC extends SimulationContext, C extends Context, AS extends AttributeSet<SC,C>, ASL extends AttributeSetLog<SC>> permits ImmutableAgent, ImmutableEnvironment {
    private final Entity<SC,C,AS,ASL> entity;

    protected ImmutableEntity(Entity<SC,C,AS,ASL> entity) {
        this.entity = entity;
    }

    Entity<SC,C,AS,ASL> getMutableEntity() {
        return entity;
    }

    public String getName() {
        return entity.name();
    }

    public int attributeSetCount() {
        return entity.attributeSetCount();
    }

    public int attributeCount() {
        return entity.attributeCount();
    }

    public abstract ImmutableAttributeSet<SC,C> getAttributeSet(int attributeSetIndex);

    public abstract ImmutableAttributeSet<SC,C> getAttributeSet(String attributeSetName);

    public EntityLog<SC,C,AS,ASL> getLog() {
        return entity.getLog();
    }
}
