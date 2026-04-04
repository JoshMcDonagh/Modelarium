package modelarium.logging.databases.factories;

import modelarium.logging.databases.AttributeSetLogDatabase;
import modelarium.logging.databases.MemoryBasedAttributeSetLogDatabase;

public class MemoryBasedAttributeSetLogDatabaseFactory extends AttributeSetLogDatabaseFactory {
    @Override
    public AttributeSetLogDatabase create() {
        return new MemoryBasedAttributeSetLogDatabase();
    }
}
