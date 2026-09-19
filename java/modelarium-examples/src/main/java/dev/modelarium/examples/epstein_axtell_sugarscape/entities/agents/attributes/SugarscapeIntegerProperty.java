package dev.modelarium.examples.epstein_axtell_sugarscape.entities.agents.attributes;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** Simple logged integer state used by Sugarscape agents. */
public final class SugarscapeIntegerProperty extends AgentProperty<Integer> {
    private int value;

    public SugarscapeIntegerProperty(String name, int initialValue) {
        super(name, true, AttributeAccessLevel.PUBLIC, Integer.class);
        value = initialValue;
    }

    @Override
    protected void set(AgentContext context, Integer value) {
        this.value = value;
    }

    @Override
    protected Integer get(AgentContext context) {
        return value;
    }
}
