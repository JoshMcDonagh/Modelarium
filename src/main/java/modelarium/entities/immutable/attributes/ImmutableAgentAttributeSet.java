package modelarium.entities.immutable.attributes;

import com.rits.cloning.Cloner;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;

public final class ImmutableAgentAttributeSet extends ImmutableAttributeSet<AgentSimulationContext, AgentContext> {
    private static final Cloner cloner = new Cloner();

    public ImmutableAgentAttributeSet(AgentAttributeSet attributeSet) {
        super(attributeSet);
    }

    @Override
    AgentEvent getEvent(int eventIndex) {
        return cloner.deepClone(((AgentAttributeSet) getMutableAttributeSet()).getEvent(eventIndex));
    }

    @Override
    AgentEvent getEvent(String eventName) {
        return cloner.deepClone(((AgentAttributeSet) getMutableAttributeSet()).getEvent(eventName));
    }

    @Override
    AgentRoutine getRoutine(int routineIndex) {
        return cloner.deepClone(((AgentAttributeSet) getMutableAttributeSet()).getRoutine(routineIndex));
    }

    @Override
    AgentRoutine getRoutine(String routineName) {
        return cloner.deepClone(((AgentAttributeSet) getMutableAttributeSet()).getRoutine(routineName));
    }

    @Override
    AgentProperty<?> getProperty(int propertyIndex) {
        return cloner.deepClone(((AgentAttributeSet) getMutableAttributeSet()).getProperty(propertyIndex));
    }

    @Override
    AgentProperty<?> getProperty(String propertyName) {
        return cloner.deepClone(((AgentAttributeSet) getMutableAttributeSet()).getProperty(propertyName));
    }
}
