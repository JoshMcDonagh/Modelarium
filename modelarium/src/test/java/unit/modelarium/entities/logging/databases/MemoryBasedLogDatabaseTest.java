package unit.modelarium.entities.logging.databases;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import modelarium.entities.logging.databases.MemoryBasedAttributeSetLogDatabase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class MemoryBasedLogDatabaseTest extends AttributeSetLogDatabaseContractTest {
    @Override
    protected AttributeSetLogDatabase createDatabase() {
        return new MemoryBasedAttributeSetLogDatabase();
    }

    @Test
    public void testGetDatabasePath_NoBackingFile_ReturnsNull() {
        assertNull(createDatabase().getDatabasePath());
    }
}
