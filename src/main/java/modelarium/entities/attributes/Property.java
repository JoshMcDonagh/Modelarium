package modelarium.entities.attributes;

import modelarium.entities.contexts.Context;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Property<T> extends Attribute {
    private static final AtomicInteger propertyCount = new AtomicInteger(0);

    private static String defaultName() {
        return "property_" + propertyCount.getAndIncrement();
    }

    private static boolean defaultIsLogged() {
        return true;
    }

    private static AttributeAccessLevel defaultAccessLevel() {
        return AttributeAccessLevel.PRIVATE;
    }

    private final Class<T> type;

    public Property(String name, boolean isLogged, AttributeAccessLevel accessLevel, Class<T> type) {
        super(name, isLogged, accessLevel);
        this.type = type;
    }

    public Property(String name, boolean isLogged, Class<T> type) {
        this(name, isLogged, defaultAccessLevel(), type);
    }

    public Property(String name, Class<T> type) {
        this(name, defaultIsLogged(), defaultAccessLevel(), type);
    }

    public Property(boolean isLogged, Class<T> type) {
        this(defaultName(), isLogged, defaultAccessLevel(), type);
    }

    public Property(Class<T> type) {
        this(defaultName(), defaultIsLogged(), defaultAccessLevel(), type);
    }

    public Property(String name, AttributeAccessLevel accessLevel, Class<T> type) {
        this(name, defaultIsLogged(), accessLevel, type);
    }

    public Property(boolean isLogged, AttributeAccessLevel accessLevel, Class<T> type) {
        this(defaultName(), isLogged, accessLevel, type);
    }

    public Property(AttributeAccessLevel accessLevel, Class<T> type) {
        this(defaultName(), defaultIsLogged(), accessLevel, type);
    }

    public Class<T> type() {
        return type;
    }

    public void set(T value) {
        set(context(), value);
    }

    public T get() {
        return get(context());
    }

    @Override
    public void run() {
        run(context());
    }

    protected abstract void run(Context context);

    protected abstract void set(Context context, T value);

    protected abstract T get(Context context);
}
