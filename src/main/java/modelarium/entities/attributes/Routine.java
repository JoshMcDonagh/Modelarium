package modelarium.entities.attributes;

public abstract class Routine extends Attribute {
    private static int processCount = 0;

    private static String defaultName() {
        return "process_" + processCount;
    }

    public static AttributeAccessLevel defaultAccessLevel() {
        return AttributeAccessLevel.PRIVATE;
    }

    public Routine(String name, AttributeAccessLevel accessLevel) {
        super(name, false, accessLevel);
        processCount++;
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
