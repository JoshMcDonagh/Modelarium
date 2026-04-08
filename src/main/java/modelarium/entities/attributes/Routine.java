package modelarium.entities.attributes;

import modelarium.entities.contexts.Context;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Routine extends Attribute {
    private static final AtomicInteger routineCount = new AtomicInteger(0);

    private static String defaultName() {
        return "routine_" + routineCount.getAndIncrement();
    }

    public static AttributeAccessLevel defaultAccessLevel() {
        return AttributeAccessLevel.PRIVATE;
    }

    public Routine(String name, AttributeAccessLevel accessLevel) {
        super(name, false, accessLevel);
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
    public void run() {
        run(context());
    }

    protected abstract void run(Context context);
}
