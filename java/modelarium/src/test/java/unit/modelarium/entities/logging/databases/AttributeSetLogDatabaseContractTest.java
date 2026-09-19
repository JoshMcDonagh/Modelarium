package unit.modelarium.entities.logging.databases;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Shared behavioural contract for every attribute-set log database backend. */
public abstract class AttributeSetLogDatabaseContractTest {
    private AttributeSetLogDatabase contractDatabase;

    protected abstract AttributeSetLogDatabase createDatabase();

    @BeforeEach
    public void connectContractDatabase() {
        contractDatabase = createDatabase();
        contractDatabase.connect();
    }

    @AfterEach
    public void disconnectContractDatabase() {
        contractDatabase.disconnect();
    }

    @Test
    public void contractMissingSeriesReturnsEmptyList() {
        List<Object> values = contractDatabase.getAttributeColumnAsList("missing");

        assertNotNull(values);
        assertTrue(values.isEmpty());
    }

    @Test
    public void contractNullValuesArePreservedInOrder() {
        contractDatabase.addAttributeValue("series", null);
        contractDatabase.addAttributeValue("series", 10);
        contractDatabase.addAttributeValue("series", null);
        contractDatabase.addAttributeValue("series", 20);
        contractDatabase.addAttributeValue("series", null);

        assertEquals(
                Arrays.asList(null, 10, null, 20, null),
                contractDatabase.getAttributeColumnAsList("series")
        );
    }

    @Test
    public void contractNullAndEmptyReplacementClearSeriesAndType() {
        contractDatabase.addAttributeValue("series", 1);
        contractDatabase.setAttributeColumn("series", null);

        assertEquals(List.of(), contractDatabase.getAttributeColumnAsList("series"));
        assertDoesNotThrow(() -> contractDatabase.addAttributeValue("series", "new type"));

        contractDatabase.setAttributeColumn("series", List.of());

        assertEquals(List.of(), contractDatabase.getAttributeColumnAsList("series"));
        assertDoesNotThrow(() -> contractDatabase.addAttributeValue("series", 2.5));
    }

    @Test
    public void contractAllNullReplacementDoesNotEstablishType() {
        contractDatabase.setAttributeColumn("series", Arrays.asList(null, null));

        assertEquals(Arrays.asList(null, null), contractDatabase.getAttributeColumnAsList("series"));
        assertDoesNotThrow(() -> contractDatabase.addAttributeValue("series", "first typed value"));
    }

    @Test
    public void contractIncompatibleAppendFailsWithoutChangingSeries() {
        contractDatabase.addAttributeValue("series", 1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contractDatabase.addAttributeValue("series", "wrong type")
        );

        assertTrue(exception.getMessage().contains(Integer.class.getName()));
        assertTrue(exception.getMessage().contains(String.class.getName()));
        assertEquals(List.of(1), contractDatabase.getAttributeColumnAsList("series"));
    }

    @Test
    public void contractMixedReplacementFailsWithoutChangingSeries() {
        contractDatabase.setAttributeColumn("series", List.of(7, 8));

        assertThrows(
                IllegalArgumentException.class,
                () -> contractDatabase.setAttributeColumn("series", List.of(1, "wrong type"))
        );

        assertEquals(List.of(7, 8), contractDatabase.getAttributeColumnAsList("series"));
    }

    @Test
    public void contractReplacementMayEstablishDifferentType() {
        contractDatabase.setAttributeColumn("series", List.of(1, 2));

        contractDatabase.setAttributeColumn("series", List.of("first", "second"));

        assertEquals(List.of("first", "second"), contractDatabase.getAttributeColumnAsList("series"));
        assertThrows(IllegalArgumentException.class, () -> contractDatabase.addAttributeValue("series", 3));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void contractCopiesValuesOnWriteAndRead() {
        ArrayList<String> original = new ArrayList<>(List.of("initial"));
        contractDatabase.addAttributeValue("series", original);
        original.add("changed after write");

        List<Object> firstRead = contractDatabase.getAttributeColumnAsList("series");
        ((List<String>) firstRead.getFirst()).add("changed after read");

        assertEquals(
                List.of("initial"),
                contractDatabase.getAttributeColumnAsList("series").getFirst()
        );
    }

    @Test
    public void contractAttributeNameMustNotBeNull() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> contractDatabase.addAttributeValue(null, 1)),
                () -> assertThrows(NullPointerException.class,
                        () -> contractDatabase.setAttributeColumn(null, List.of(1))),
                () -> assertThrows(NullPointerException.class,
                        () -> contractDatabase.getAttributeColumnAsList(null))
        );
    }

    @Test
    public void contractConnectIsIdempotentAndPreservesValues() {
        contractDatabase.addAttributeValue("series", 1);

        contractDatabase.connect();

        assertEquals(List.of(1), contractDatabase.getAttributeColumnAsList("series"));
    }

    @Test
    public void contractOperationsBeforeConnectFailFast() {
        AttributeSetLogDatabase disconnected = createDatabase();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> disconnected.addAttributeValue("series", 1)
        );

        assertEquals(
                "Database connection has not been established. Call connect() first.",
                exception.getMessage()
        );
        assertThrows(IllegalStateException.class,
                () -> disconnected.setAttributeColumn("series", List.of(1)));
        assertThrows(IllegalStateException.class,
                () -> disconnected.getAttributeColumnAsList("series"));
        assertDoesNotThrow(disconnected::disconnect);
    }

    @Test
    public void contractDisconnectIsIdempotentAndReconnectStartsEmpty() {
        contractDatabase.addAttributeValue("series", 1);
        contractDatabase.disconnect();

        assertDoesNotThrow(contractDatabase::disconnect);
        assertThrows(IllegalStateException.class,
                () -> contractDatabase.getAttributeColumnAsList("series"));

        contractDatabase.connect();

        assertEquals(List.of(), contractDatabase.getAttributeColumnAsList("series"));
        assertDoesNotThrow(() -> contractDatabase.addAttributeValue("series", "fresh type"));
    }
}
