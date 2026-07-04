package modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.utils.Cloners;

public final class ImmutableAgentAttributeSet extends ImmutableAttributeSet<AgentSimulationContext, AgentContext> {
    public ImmutableAgentAttributeSet(AgentAttributeSet attributeSet) {
        super(attributeSet);
    }

    @Override
    public AgentEvent getEvent(int eventIndex) {
        return Cloners.standard().deepClone(((AgentAttributeSet) getMutableAttributeSet()).getEvent(eventIndex));
    }

    @Override
    public AgentEvent getEvent(String eventName) {
        return Cloners.standard().deepClone(((AgentAttributeSet) getMutableAttributeSet()).getEvent(eventName));
    }

    @Override
    public AgentRoutine getRoutine(int routineIndex) {
        return Cloners.standard().deepClone(((AgentAttributeSet) getMutableAttributeSet()).getRoutine(routineIndex));
    }

    @Override
    public AgentRoutine getRoutine(String routineName) {
        return Cloners.standard().deepClone(((AgentAttributeSet) getMutableAttributeSet()).getRoutine(routineName));
    }

    @Override
    public AgentProperty<?> getProperty(int propertyIndex) {
        return Cloners.standard().deepClone(((AgentAttributeSet) getMutableAttributeSet()).getProperty(propertyIndex));
    }

    @Override
    public AgentProperty<?> getProperty(String propertyName) {
        return Cloners.standard().deepClone(((AgentAttributeSet) getMutableAttributeSet()).getProperty(propertyName));
    }
}
