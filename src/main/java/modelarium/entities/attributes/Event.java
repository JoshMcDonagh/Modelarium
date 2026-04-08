package modelarium.entities.attributes;

import modelarium.entities.contexts.Context;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Event extends Attribute {
    private static final AtomicInteger eventCount = new AtomicInteger(0);

    private static String defaultName() {
        return "event_" + eventCount.getAndIncrement();
    }

    private static boolean defaultIsLogged() {
        return true;
    }

    private static AttributeAccessLevel defaultAccessLevel() {
        return AttributeAccessLevel.PRIVATE;
    }

    public Event(String name, boolean isLogged, AttributeAccessLevel accessLevel) {
        super(name, isLogged, accessLevel);
    }

    public Event(String name, boolean isLogged) {
        this(name, isLogged, defaultAccessLevel());
    }

    public Event(String name) {
        this(name, defaultIsLogged(), defaultAccessLevel());
    }

    public Event(boolean isLogged) {
        this(defaultName(), isLogged, defaultAccessLevel());
    }

    public Event() {
        this(defaultName(), defaultIsLogged(), defaultAccessLevel());
    }

    public Event(String name, AttributeAccessLevel accessLevel) {
        this(name, defaultIsLogged(), accessLevel);
    }

    public Event(boolean isLogged, AttributeAccessLevel accessLevel) {
        this(defaultName(), isLogged, accessLevel);
    }

    public Event(AttributeAccessLevel accessLevel) {
        this(defaultName(), defaultIsLogged(), accessLevel);
    }

    public boolean isTriggered() {
        return isTriggered(context());
    }

    @Override
    public void run() {
        run(context());
    }

    protected abstract boolean isTriggered(Context context);

    protected abstract void run(Context context);
}
