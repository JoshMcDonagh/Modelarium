package modelarium.attributes;

public abstract class Process extends Attribute {
    private static int processCount = 0;

    private static String defaultName() {
        return "process_" + processCount;
    }

    public static AttributeAccessLevel defaultAccessLevel() {
        return AttributeAccessLevel.PRIVATE;
    }

    public Process(String name, AttributeAccessLevel accessLevel) {
        super(name, false, accessLevel);
        processCount++;
    }

    public Process(String name) {
        this(name, defaultAccessLevel());
    }

    public Process(AttributeAccessLevel accessLevel) {
        this(defaultName(), accessLevel);
    }

    public Process() {
        this(defaultName(), defaultAccessLevel());
    }

    @Override
    public abstract void run();
}
