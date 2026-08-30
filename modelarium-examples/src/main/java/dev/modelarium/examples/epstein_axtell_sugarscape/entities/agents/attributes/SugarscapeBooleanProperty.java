package dev.modelarium.examples.epstein_axtell_sugarscape.entities.agents.attributes;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** Simple logged boolean state used by reusable Sugarscape agent slots. */
public final class SugarscapeBooleanProperty extends AgentProperty<Boolean> {
    private boolean value;

    public SugarscapeBooleanProperty(String name, boolean initialValue) {
        super(name, true, AttributeAccessLevel.PUBLIC, Boolean.class);
        value = initialValue;
    }

    @Override
    protected void set(AgentContext context, Boolean value) {
        this.value = value;
    }

    @Override
    protected Boolean get(AgentContext context) {
        return value;
    }
}
