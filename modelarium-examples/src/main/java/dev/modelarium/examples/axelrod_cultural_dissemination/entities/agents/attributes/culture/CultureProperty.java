package dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.culture;

import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;

/** Stores the cultural feature vector at one fixed site. */
public final class CultureProperty extends AgentProperty<Culture> {
    private Culture culture;

    public CultureProperty() {
        // The aggregate environment metrics are logged instead; logging every site's culture for every micro-event
        // would produce millions of redundant values because only one site is activated per event.
        super("culture", false, AttributeAccessLevel.PUBLIC, Culture.class);
    }

    @Override
    protected void set(AgentContext context, Culture value) {
        culture = value;
    }

    @Override
    protected Culture get(AgentContext context) {
        return culture;
    }
}
