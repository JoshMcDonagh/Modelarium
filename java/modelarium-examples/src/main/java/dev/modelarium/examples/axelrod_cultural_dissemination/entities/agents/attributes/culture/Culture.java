package dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.culture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An Axelrod culture vector: one integer trait for each cultural feature.
 *
 * <p>The object is value-like to model code: changing a trait returns a new {@code Culture}. Its backing field is
 * nevertheless non-final so it remains compatible with Modelarium's reflective deep-cloning mechanism.
 */
public final class Culture {
    private int[] traits;

    public Culture(int[] traits) {
        if (traits == null || traits.length == 0)
            throw new IllegalArgumentException("A culture must contain at least one feature");
        this.traits = Arrays.copyOf(traits, traits.length);
    }

    public int featureCount() {
        return traits.length;
    }

    public int trait(int featureIndex) {
        return traits[featureIndex];
    }

    /** Returns the fraction of features on which this culture and another culture have identical traits. */
    public double similarity(Culture other) {
        requireSameFeatureCount(other);

        int matchingFeatures = 0;
        for (int feature = 0; feature < traits.length; feature++) {
            if (traits[feature] == other.traits[feature])
                matchingFeatures++;
        }
        return matchingFeatures / (double) traits.length;
    }

    /** Returns the feature indices on which this culture differs from another culture. */
    public List<Integer> differingFeatures(Culture other) {
        requireSameFeatureCount(other);

        List<Integer> differingFeatures = new ArrayList<>();
        for (int feature = 0; feature < traits.length; feature++) {
            if (traits[feature] != other.traits[feature])
                differingFeatures.add(feature);
        }
        return differingFeatures;
    }

    /** Returns a copy of this culture with one feature changed to the specified trait. */
    public Culture withTrait(int featureIndex, int trait) {
        int[] updatedTraits = Arrays.copyOf(traits, traits.length);
        updatedTraits[featureIndex] = trait;
        return new Culture(updatedTraits);
    }

    private void requireSameFeatureCount(Culture other) {
        if (other == null || other.featureCount() != featureCount())
            throw new IllegalArgumentException("Cultures must contain the same number of features");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Culture culture))
            return false;
        return Arrays.equals(traits, culture.traits);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(traits);
    }

    @Override
    public String toString() {
        return Arrays.toString(traits);
    }
}
