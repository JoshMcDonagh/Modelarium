package unit.modelarium.multithreading.requestresponse;

import modelarium.multithreading.requestresponse.ResponseType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the {@link ResponseType} enum.
 *
 * <p>Ensures the enum defines the expected values, maintains ordinal consistency,
 * and provides correct string representations.
 */
public class ResponseTypeTest {

    @Test
    public void testEnumContainsExpectedValues() {
        assertNotNull(ResponseType.valueOf("ALL_WORKERS_FINISH_TICK"));
        assertNotNull(ResponseType.valueOf("ALL_WORKERS_UPDATE_COORDINATOR"));
        assertNotNull(ResponseType.valueOf("AGENT_ACCESS"));
        assertNotNull(ResponseType.valueOf("FILTERED_AGENTS_ACCESS"));
        assertNotNull(ResponseType.valueOf("ENVIRONMENT_ATTRIBUTES_ACCESS"));
    }

    @Test
    public void testEnumOrdinalConsistency() {
        assertEquals(0, ResponseType.ALL_WORKERS_FINISH_TICK.ordinal());
        assertEquals(1, ResponseType.ALL_WORKERS_UPDATE_COORDINATOR.ordinal());
        assertEquals(2, ResponseType.AGENT_ACCESS.ordinal());
        assertEquals(3, ResponseType.FILTERED_AGENTS_ACCESS.ordinal());
        assertEquals(4, ResponseType.ENVIRONMENT_ATTRIBUTES_ACCESS.ordinal());
    }

    @Test
    public void testEnumToStringMatchesName() {
        for (ResponseType type : ResponseType.values()) {
            assertEquals(type.name(), type.toString());
        }
    }
}
