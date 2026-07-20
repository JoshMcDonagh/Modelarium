package unit.modelarium.entities.logging.databases.factories;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import modelarium.entities.logging.databases.DiskBasedAttributeSetLogDatabase;
import modelarium.entities.logging.databases.factories.DiskBasedAttributeSetLogDatabaseFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiskBasedLogDatabaseFactoryTest {
    @Test
    public void testCreate() {
        DiskBasedAttributeSetLogDatabaseFactory factory = new DiskBasedAttributeSetLogDatabaseFactory();

        AttributeSetLogDatabase database = factory.create();

        assertInstanceOf(DiskBasedAttributeSetLogDatabase.class, database);
    }

    @Test
    public void testCreate_ReturnsNewInstanceOnEachCall() {
        DiskBasedAttributeSetLogDatabaseFactory factory = new DiskBasedAttributeSetLogDatabaseFactory();

        AttributeSetLogDatabase firstDatabase = factory.create();
        AttributeSetLogDatabase secondDatabase = factory.create();

        assertNotSame(firstDatabase, secondDatabase);
    }
}
