package integration.agentEnvironmentSyncIntegrationTest.attributes;

import integration.agentEnvironmentSyncIntegrationTest.attributes.environment.properties.EnvTick;
import integration.agentEnvironmentSyncIntegrationTest.attributes.agent.properties.SeenEnvTick;

public class ModelAttributes {

    public static AttributeSetCollection getAgentAttributeSetCollection() {
        // Empty pre/post event lists (same pattern as test1/test2)
        Events preEvents = new Events();
        Events postEvents = new Events();

        // Add the property to the Properties collection (use add, not put)
        Properties properties = new Properties();
        properties.add(new SeenEnvTick());

        // Build the attribute set and add it to the collection (use add, not addAttributeSet)
        AttributeSet perception = new AttributeSet("perception", preEvents, properties, postEvents);

        AttributeSetCollection c = new AttributeSetCollection();
        c.add(perception);
        return c;
    }

    public static AttributeSetCollection getEnvironmentAttributeSetCollection() {
        Events preEvents = new Events();
        Events postEvents = new Events();

        Properties properties = new Properties();
        properties.add(new EnvTick());

        AttributeSet climate = new AttributeSet("climate", preEvents, properties, postEvents);

        AttributeSetCollection c = new AttributeSetCollection();
        c.add(climate);
        return c;
    }
}
