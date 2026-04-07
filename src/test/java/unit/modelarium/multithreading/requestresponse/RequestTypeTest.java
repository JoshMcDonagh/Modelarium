package unit.modelarium.multithreading.requestresponse;

import modelarium.multithreading.requestresponse.RequestType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the {@link RequestType} enum.
 *
 * <p>Validates presence, name mapping, and ordinal consistency.
 */
public class RequestTypeTest {

    @Test
    public void testEnumContainsExpectedValues() {
        assertNotNull(RequestType.valueOf("ALL_WORKERS_FINISH_TICK"));
        assertNotNull(RequestType.valueOf("ALL_WORKERS_UPDATE_COORDINATOR"));
        assertNotNull(RequestType.valueOf("AGENT_ACCESS"));
        assertNotNull(RequestType.valueOf("FILTERED_AGENTS_ACCESS"));
        assertNotNull(RequestType.valueOf("ENVIRONMENT_ATTRIBUTES_ACCESS"));
        assertNotNull(RequestType.valueOf("UPDATE_COORDINATOR_AGENTS"));
    }

    @Test
    public void testEnumOrdinalConsistency() {
        assertEquals(0, RequestType.ALL_WORKERS_FINISH_TICK.ordinal());
        assertEquals(1, RequestType.ALL_WORKERS_UPDATE_COORDINATOR.ordinal());
        assertEquals(2, RequestType.AGENT_ACCESS.ordinal());
        assertEquals(3, RequestType.FILTERED_AGENTS_ACCESS.ordinal());
        assertEquals(4, RequestType.ENVIRONMENT_ATTRIBUTES_ACCESS.ordinal());
        assertEquals(5, RequestType.UPDATE_COORDINATOR_AGENTS.ordinal());
    }

    @Test
    public void testEnumToStringMatchesName() {
        for (RequestType type : RequestType.values()) {
            assertEquals(type.name(), type.toString());
        }
    }
}
