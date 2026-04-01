package modelarium.agents;

import modelarium.Entity;
import modelarium.attributes.AttributeSet;

import java.util.List;

/**
 * Represents an agent in the agent-based model.
 *
 * <p>An agent is a simulation entity with a unique name and a collection of attributes
 * that define its behaviour and internal state. The agent's `run()` method delegates to
 * its attribute set, allowing attribute-driven logic to control execution.
 */
public class Agent extends Entity {
    public Agent(String name, List<AttributeSet> attributeSets) {
        super(name, attributeSets);
    }

    @Override
    public Agent clone() {
        return getCloner().deepClone(this);
    }
}
