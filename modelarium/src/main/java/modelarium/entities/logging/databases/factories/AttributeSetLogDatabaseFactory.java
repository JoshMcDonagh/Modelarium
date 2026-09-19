package modelarium.entities.logging.databases.factories;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;

/**
 * Interface for creating the database an attribute set log uses to store its results.
 *
 * <p>The model uses a single factory to create one database per attribute set, allowing the storage mechanism
 * (in memory, on disk, or otherwise) to be chosen through the model's configuration settings.
 */
public interface AttributeSetLogDatabaseFactory {

    /**
     * Creates a new attribute set log database.
     *
     * @return a new {@link AttributeSetLogDatabase} instance
     */
    AttributeSetLogDatabase create();
}
