package modelarium.entities.logging.databases.factories;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;

public abstract class AttributeSetLogDatabaseFactory {
    private static AttributeSetLogDatabaseFactory factory = null;

    public static void set(AttributeSetLogDatabaseFactory factory) {
        if (AttributeSetLogDatabaseFactory.factory != null)
            return;

        AttributeSetLogDatabaseFactory.factory = factory;
    }

    public static AttributeSetLogDatabaseFactory get() {
        return factory;
    }

    public abstract AttributeSetLogDatabase create();
}
