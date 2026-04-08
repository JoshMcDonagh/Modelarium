package modelarium.entities.attributes.properties;

import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.contexts.Context;

abstract class Property<T,C extends Context> extends Attribute<C> {
    private final Class<T> type;

    public Property(String name, boolean isLogged, AttributeAccessLevel accessLevel, Class<T> type) {
        super(name, isLogged, accessLevel);
        this.type = type;
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

    protected abstract void run(C context);

    protected abstract void set(C context, T value);

    protected abstract T get(C context);
}
