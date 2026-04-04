package modelarium.entities.logging.databases.factories;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import modelarium.entities.logging.databases.DiskBasedAttributeSetLogDatabase;

public class DiskBasedAttributeSetLogDatabaseFactory extends AttributeSetLogDatabaseFactory {
    @Override
    public AttributeSetLogDatabase create() {
        return new DiskBasedAttributeSetLogDatabase();
    }
}
