package integration.agentEnvironmentSyncIntegrationTest.attributes.agent.properties;

import modelarium.AccessibleContext;
import modelarium.environments.Environment;

public class SeenEnvTick extends Property<Integer> {
    private int seen = -1;
    public SeenEnvTick() { super("SeenEnvTick", true, Integer.TYPE); }

    public SeenEnvTick(SeenEnvTick other) {
        super(other);
        this.seen = other.seen;
    }

    @Override public Integer get() { return seen; }
    @Override public void set(Integer v) { seen = v; }

    @Override
    public void run() {
        AccessibleContext acc = getAssociatedModelElement().getModelElementAccessor();
        Environment env = acc.getEnvironment();  // goes through coordinator/cache
        if (env != null) {
            Integer envTick = (Integer) env.getAttributeSetCollection()
                    .get("climate").getProperties().get("EnvTick").get();
            seen = envTick;
        }
    }
}
