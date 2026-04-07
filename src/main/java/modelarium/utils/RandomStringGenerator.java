package modelarium.utils;

import java.security.SecureRandom;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for generating random alphanumeric strings.
 * Includes functionality to generate both general and unique strings.
 *
 * Note: Uniqueness is maintained in-memory for the life of the JVM.
 */
public final class RandomStringGenerator {

    private RandomStringGenerator() {}

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // SecureRandom is thread-safe; fine for multi-threaded use.
    private static final SecureRandom RNG = new SecureRandom();

    // Tracks previously generated unique strings
    private static final Set<String> GENERATED = ConcurrentHashMap.newKeySet();

    /**
     * Generates a random alphanumeric string of the specified length.
     * Pure function: no global side effects.
     *
     * @param length non-negative length
     * @return random string
     * @throws IllegalArgumentException if length is negative
     */
    public static String generateRandomString(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be non-negative.");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = RNG.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(idx));
        }
        return sb.toString();
    }

    /**
     * Generates a unique random alphanumeric string of the specified length.
     * Retries until a value is inserted into the concurrent set.
     */
    public static String generateUniqueRandomString(int length) {
        String s;
        do {
            s = generateRandomString(length);
        } while (!GENERATED.add(s));
        return s;
    }

    /**
     * Test helper: clears the uniqueness set.
     * Only use from tests to avoid unbounded growth across suites.
     */
    public static void clearGeneratedForTests() {
        GENERATED.clear();
    }
}
