package modelarium.utils.random;

import java.util.Random;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

public final class RndGenerator {
    private RndGenerator() {}

    private static SplittableRandom mainRandom = null;
    private static boolean isSeedSet = false;
    private static Long seed;

    public static void setSeed(Long seed) {
        if (isSeedSet)
            throw new IllegalStateException("Seed is already set.");

        isSeedSet = true;
        RndGenerator.seed = seed;
    }

    public static RandomGenerator get() {
        if (mainRandom == null) {
            if (!isSeedSet)
                throw new IllegalStateException("Seed is not set.");
            else if (seed == null)
                mainRandom = new SplittableRandom();
            else
                mainRandom = new SplittableRandom(seed);
        }

        return mainRandom;
    }
}
