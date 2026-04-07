package modelarium.entities.attributes;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Routine extends Attribute {
    private static final AtomicInteger processCount = new AtomicInteger(0);

    private static String defaultName() {
        return "process_" + processCount.intValue();
    }

    public static AttributeAccessLevel defaultAccessLevel() {
        return AttributeAccessLevel.PRIVATE;
    }

    public Routine(String name, AttributeAccessLevel accessLevel) {
        super(name, false, accessLevel);
        processCount.incrementAndGet();
    }

    public Routine(String name) {
        this(name, defaultAccessLevel());
    }

    public Routine(AttributeAccessLevel accessLevel) {
        this(defaultName(), accessLevel);
    }

    public Routine() {
        this(defaultName(), defaultAccessLevel());
    }

    @Override
    public abstract void run();
}
