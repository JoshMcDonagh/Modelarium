package modelarium.entities.logging.databases.factories;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import modelarium.entities.logging.databases.MemoryBasedAttributeSetLogDatabase;

public class MemoryBasedAttributeSetLogDatabaseFactory extends AttributeSetLogDatabaseFactory {
    @Override
    public AttributeSetLogDatabase create() {
        return new MemoryBasedAttributeSetLogDatabase();
    }
}
