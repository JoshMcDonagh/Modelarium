package modelarium.entities.logging.databases.factories;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import modelarium.entities.logging.databases.MemoryBasedAttributeSetLogDatabase;

/**
 * Class for creating memory-based attribute set log databases.
 *
 * <p>This factory produces a new {@link MemoryBasedAttributeSetLogDatabase} for each attribute set, storing results
 * in memory for the duration of the run.
 */
public class MemoryBasedAttributeSetLogDatabaseFactory implements AttributeSetLogDatabaseFactory {

    /**
     * Creates a new memory-based attribute set log database.
     *
     * @return a new {@link MemoryBasedAttributeSetLogDatabase} instance
     */
    @Override
    public AttributeSetLogDatabase create() {
        return new MemoryBasedAttributeSetLogDatabase();
    }
}
