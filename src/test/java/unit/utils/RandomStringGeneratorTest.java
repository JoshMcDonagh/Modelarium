package unit.utils;

import modelarium.utils.RandomStringGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RandomStringGenerator} utility class.
 * <p>
 * These tests verify that both general and unique random string generation
 * functions as expected, and that invalid parameters are handled appropriately.
 */
public class RandomStringGeneratorTest {

    /**
     * Ensures a generated random string has the correct length.
     */
    @Test
    public void testGenerateRandomString_LengthCorrect() {
        int length = 10;
        String result = RandomStringGenerator.generateRandomString(length);
        assertEquals(length, result.length(), "Generated string should be of specified length.");
    }

    /**
     * Ensures that all characters in the generated string are from the expected character pool.
     */
    @Test
    public void testGenerateRandomString_ContainsOnlyValidCharacters() {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String result = RandomStringGenerator.generateRandomString(50);

        for (char c : result.toCharArray()) {
            assertTrue(allowedChars.indexOf(c) >= 0,
                    "Generated character '" + c + "' should be within allowed character set.");
        }
    }

    /**
     * Verifies that requesting a negative-length string throws an exception.
     */
    @Test
    public void testGenerateRandomString_NegativeLengthThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                        RandomStringGenerator.generateRandomString(-1),
                "Generating a string with negative length should throw an IllegalArgumentException.");
    }

    /**
     * Confirms that multiple calls to generateUniqueRandomString yield distinct strings.
     */
    @Test
    public void testGenerateUniqueRandomString_YieldsUniqueValues() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String unique = RandomStringGenerator.generateUniqueRandomString(12);
            assertFalse(seen.contains(unique), "Generated string should be unique.");
            seen.add(unique);
        }
    }
}
