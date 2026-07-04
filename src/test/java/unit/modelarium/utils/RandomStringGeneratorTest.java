package unit.modelarium.utils;

import modelarium.utils.random.RndStringGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RndStringGenerator}.
 */
public class RandomStringGeneratorTest {

    @AfterEach
    void cleanUp() {
        RndStringGenerator.clearGeneratedForTests();
    }

    @Test
    void testGenerateRandomString_correctLength() {
        String s = RndStringGenerator.generateRandomString(20);
        assertEquals(20, s.length());
    }

    @Test
    void testGenerateRandomString_zeroLength() {
        String s = RndStringGenerator.generateRandomString(0);
        assertEquals("", s);
    }

    @Test
    void testGenerateRandomString_negativeLengthThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> RndStringGenerator.generateRandomString(-1));
    }

    @Test
    void testGenerateRandomString_isAlphanumeric() {
        String s = RndStringGenerator.generateRandomString(100);
        assertTrue(s.matches("[A-Za-z0-9]+"), "Should contain only alphanumeric characters.");
    }

    @Test
    void testGenerateUniqueRandomString_noDuplicates() {
        int n = 50;
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < n; i++) {
            String s = RndStringGenerator.generateUniqueRandomString(10);
            assertTrue(seen.add(s), "Generated string should be unique.");
        }
    }

    @Test
    void testClearGeneratedForTests() {
        RndStringGenerator.generateUniqueRandomString(10);
        RndStringGenerator.clearGeneratedForTests();
        // Should not throw even if we try the same generator state
        assertDoesNotThrow(() -> RndStringGenerator.generateUniqueRandomString(10));
    }
}
