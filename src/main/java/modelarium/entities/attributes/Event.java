package modelarium.entities.attributes;

public abstract class Event extends Attribute {
    private static int eventCount = 0;

    private static String defaultName() {
        return "event_" + eventCount;
    }

    private static boolean defaultIsLogged() {
        return true;
    }

    private static AttributeAccessLevel defaultAccessLevel() {
        return AttributeAccessLevel.PRIVATE;
    }

    public Event(String name, boolean isLogged, AttributeAccessLevel accessLevel) {
        super(name, isLogged, accessLevel);
        eventCount++;
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

    public abstract boolean isTriggered();

    @Override
    public abstract void run();
}
