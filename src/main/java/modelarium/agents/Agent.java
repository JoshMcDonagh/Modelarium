package modelarium.agents;

import modelarium.Entity;

/**
 * Represents an agent in the agent-based model.
 *
 * <p>An agent is a simulation entity with a unique name and a collection of attributes
 * that define its behaviour and internal state. The agent's `run()` method delegates to
 * its attribute set, allowing attribute-driven logic to control execution.
 */
public class Agent extends Entity {

    /**
     * Constructs an agent with the given name and attribute set collection.
     *
     * @param name the unique name of the agent
     * @param attributeSets the attribute set defining the agent's internal state and behaviour
     */
    public Agent(String name, AttributeSetCollection attributeSets) {
        super(name, attributeSets);
    }

    /**
     * Executes one simulation step for this agent by invoking the `run()` method
     * of its attribute set collection.
     *
     * <p>This method is called once per tick during the model execution.
     */
    @Override
    public void run() {
        getAttributeSetCollection().run();
    }

    @Override
    public Agent deepCopy() {
        return new Agent(name(), getAttributeSetCollection().deepCopy());
    }
}
