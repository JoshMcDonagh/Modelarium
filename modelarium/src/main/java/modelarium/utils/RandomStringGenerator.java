package modelarium.utils;

import java.security.SecureRandom;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for generating random alphanumeric strings.
 *
 * <p>This class provides functionality for generating both general and unique random strings. Uniqueness is
 * maintained in memory for the life of the JVM.
 */
public final class RandomStringGenerator {

    private RandomStringGenerator() {}

    /** The set of characters random strings are built from */
    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /** The random generator used to select characters, which is thread-safe for multithreaded use */
    private static final SecureRandom RNG = new SecureRandom();

    /** Tracks previously generated unique strings */
    private static final Set<String> GENERATED = ConcurrentHashMap.newKeySet();

    /**
     * Generates a random alphanumeric string of the specified length.
     *
     * <p>The length must not be negative. This method has no global side effects.
     *
     * @param length the number of characters the generated string will contain
     * @return a new random alphanumeric string
     */
    public static String generateRandomString(int length) {
        if (length < 0)
            throw new IllegalArgumentException("Length must be non-negative.");

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = RNG.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(idx));
        }
        return sb.toString();
    }

    /**
     * Generates a unique random alphanumeric string of the specified length.
     *
     * <p>This method retries until a string not previously generated during the life of the JVM is produced.
     *
     * @param length the number of characters the generated string will contain
     * @return a new unique random alphanumeric string
     */
    public static String generateUniqueRandomString(int length) {
        String s;
        do {
            s = generateRandomString(length);
        } while (!GENERATED.add(s));
        return s;
    }

    /**
     * Clears the set of previously generated unique strings.
     *
     * <p>This method should only be used from tests, to avoid unbounded growth of the uniqueness set across suites.
     */
    public static void clearGeneratedForTests() {
        GENERATED.clear();
    }
}
