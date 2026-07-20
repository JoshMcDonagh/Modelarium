package modelarium.entities.logging.databases.factories;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import modelarium.entities.logging.databases.DiskBasedAttributeSetLogDatabase;

/**
 * Class for creating disk-based attribute set log databases.
 *
 * <p>This factory produces a new {@link DiskBasedAttributeSetLogDatabase} for each attribute set, storing results
 * on disk rather than in memory.
 */
public class DiskBasedAttributeSetLogDatabaseFactory implements AttributeSetLogDatabaseFactory {

    /**
     * Creates a new disk-based attribute set log database.
     *
     * @return a new {@link DiskBasedAttributeSetLogDatabase} instance
     */
    @Override
    public AttributeSetLogDatabase create() {
        return new DiskBasedAttributeSetLogDatabase();
    }
}
