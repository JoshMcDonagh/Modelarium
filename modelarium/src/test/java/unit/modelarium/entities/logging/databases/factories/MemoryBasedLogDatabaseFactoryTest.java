package unit.modelarium.entities.logging.databases.factories;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import modelarium.entities.logging.databases.MemoryBasedAttributeSetLogDatabase;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class MemoryBasedLogDatabaseFactoryTest {
    @Test
    public void testCreate() {
        MemoryBasedAttributeSetLogDatabaseFactory factory = new MemoryBasedAttributeSetLogDatabaseFactory();

        AttributeSetLogDatabase database = factory.create();

        assertInstanceOf(MemoryBasedAttributeSetLogDatabase.class, database);
    }

    @Test
    public void testCreate_ReturnsNewInstanceOnEachCall() {
        MemoryBasedAttributeSetLogDatabaseFactory factory = new MemoryBasedAttributeSetLogDatabaseFactory();

        AttributeSetLogDatabase firstDatabase = factory.create();
        AttributeSetLogDatabase secondDatabase = factory.create();

        assertNotSame(firstDatabase, secondDatabase);
    }
}
