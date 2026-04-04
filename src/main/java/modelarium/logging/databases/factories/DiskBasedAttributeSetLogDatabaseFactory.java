package modelarium.logging.databases.factories;

import modelarium.logging.databases.AttributeSetLogDatabase;
import modelarium.logging.databases.DiskBasedAttributeSetLogDatabase;

public class DiskBasedAttributeSetLogDatabaseFactory extends AttributeSetLogDatabaseFactory {
    @Override
    public AttributeSetLogDatabase create() {
        return new DiskBasedAttributeSetLogDatabase();
    }
}
