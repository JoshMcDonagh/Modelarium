package unit.modelarium.utils;

import modelarium.utils.RandomStringGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RandomStringGeneratorTest {
    @AfterEach
    public void tearDown() {
        RandomStringGenerator.clearGeneratedForTests();
    }

    @Test
    public void testGenerateRandomString() {
        String generatedString = RandomStringGenerator.generateRandomString(20);

        assertEquals(20, generatedString.length());
    }

    @Test
    public void testGenerateRandomString_ZeroLength() {
        String generatedString = RandomStringGenerator.generateRandomString(0);

        assertEquals("", generatedString);
    }

    @Test
    public void testGenerateRandomString_NegativeLength_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> RandomStringGenerator.generateRandomString(-1));
    }

    @Test
    public void testGenerateRandomString_Alphanumeric() {
        String generatedString = RandomStringGenerator.generateRandomString(100);

        assertTrue(generatedString.matches("[A-Za-z0-9]+"));
    }

    @Test
    public void testGenerateUniqueRandomString() {
        Set<String> generatedStrings = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            String generatedString = RandomStringGenerator.generateUniqueRandomString(10);
            assertTrue(generatedStrings.add(generatedString));
        }
    }

    @Test
    public void testClearGeneratedForTests() {
        RandomStringGenerator.generateUniqueRandomString(10);

        RandomStringGenerator.clearGeneratedForTests();

        assertDoesNotThrow(() -> RandomStringGenerator.generateUniqueRandomString(10));
    }
}
