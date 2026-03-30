package integration.agentEnvironmentSyncIntegrationTest.attributes.environment.properties;

public class EnvTick extends Property<Integer> {
    public EnvTick() { super("EnvTick", true, Integer.TYPE); }

    public EnvTick(EnvTick other) {
        super(other);
    }

    @Override public Integer get() {
        var acc = getAssociatedModelElement().getModelElementAccessor();
        var clock = (acc == null) ? null : acc.getModelClock();
        if (clock == null) return 0;
        // use whatever exposes the current *recorded* tick in your clock
        return clock.getTick();   // or clock.getTicksCompleted()
    }

    @Override public void set(Integer v) { /* no-op */ }

    @Override public void run() { /* no-op – derived from clock */ }
}
