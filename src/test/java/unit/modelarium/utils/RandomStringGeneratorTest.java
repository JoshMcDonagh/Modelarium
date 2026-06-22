package unit.modelarium.utils;

import modelarium.utils.RandomStringGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RandomStringGenerator}.
 */
public class RandomStringGeneratorTest {

    @AfterEach
    void cleanUp() {
        RandomStringGenerator.clearGeneratedForTests();
    }

    @Test
    void testGenerateRandomString_correctLength() {
        String s = RandomStringGenerator.generateRandomString(20);
        assertEquals(20, s.length());
    }

    @Test
    void testGenerateRandomString_zeroLength() {
        String s = RandomStringGenerator.generateRandomString(0);
        assertEquals("", s);
    }

    @Test
    void testGenerateRandomString_negativeLengthThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> RandomStringGenerator.generateRandomString(-1));
    }

    @Test
    void testGenerateRandomString_isAlphanumeric() {
        String s = RandomStringGenerator.generateRandomString(100);
        assertTrue(s.matches("[A-Za-z0-9]+"), "Should contain only alphanumeric characters.");
    }

    @Test
    void testGenerateUniqueRandomString_noDuplicates() {
        int n = 50;
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < n; i++) {
            String s = RandomStringGenerator.generateUniqueRandomString(10);
            assertTrue(seen.add(s), "Generated string should be unique.");
        }
    }

    @Test
    void testClearGeneratedForTests() {
        RandomStringGenerator.generateUniqueRandomString(10);
        RandomStringGenerator.clearGeneratedForTests();
        // Should not throw even if we try the same generator state
        assertDoesNotThrow(() -> RandomStringGenerator.generateUniqueRandomString(10));
    }
}
