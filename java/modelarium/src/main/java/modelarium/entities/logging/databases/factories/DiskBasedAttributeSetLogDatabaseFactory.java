package modelarium.entities.logging.databases.factories;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import modelarium.entities.logging.databases.DiskBasedAttributeSetLogDatabase;
import modelarium.python.PythonValueCodec;

/**
 * Class for creating disk-based attribute set log databases.
 *
 * <p>This factory produces a new {@link DiskBasedAttributeSetLogDatabase} for each attribute set, storing results
 * on disk rather than in memory.
 */
public class DiskBasedAttributeSetLogDatabaseFactory implements AttributeSetLogDatabaseFactory {
    private final PythonValueCodec pythonValueCodec;

    /**
     * Constructs a factory for disk-backed attribute-set log databases.
     */
    public DiskBasedAttributeSetLogDatabaseFactory() {
        this(null);
    }

    public DiskBasedAttributeSetLogDatabaseFactory(PythonValueCodec pythonValueCodec) {
        this.pythonValueCodec = pythonValueCodec;
    }

    /**
     * Creates a new disk-based attribute set log database.
     *
     * @return a new {@link DiskBasedAttributeSetLogDatabase} instance
     */
    @Override
    public AttributeSetLogDatabase create() {
        return new DiskBasedAttributeSetLogDatabase(pythonValueCodec);
    }
}
