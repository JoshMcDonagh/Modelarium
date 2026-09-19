package dev.modelarium.examples.sir.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SIRSettingsTest {
    @Test
    void validSettingsCalculatePopulationSize() {
        assertEquals(12, settings(new SIRSettings.InitialStates(9, 2, 1)).populationSize());
    }

    @Test
    void modelSettingsRejectInvalidCoreAndTickCounts() {
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.SIRModelSettings(0, 1, 1L));
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.SIRModelSettings(1, 0, 1L));
    }

    @Test
    void initialStatesRejectNegativeAndEmptyPopulations() {
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.InitialStates(-1, 1, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> settings(new SIRSettings.InitialStates(0, 0, 0))
        );
    }

    @Test
    void initialStatesRejectPopulationOverflow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> settings(new SIRSettings.InitialStates(Integer.MAX_VALUE, 1, 0))
        );
    }

    @Test
    void areaRejectsNonPositiveDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.Environment.Area(0, 10));
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.Environment.Area(10, -1));
    }

    @Test
    void movementRejectsInvalidProbabilityAndDistance() {
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.Movement(-0.01, 1));
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.Movement(1.01, 1));
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.Movement(Double.NaN, 1));
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.Movement(0.5, -0.01));
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.Movement(0.5, Double.POSITIVE_INFINITY));
    }

    @Test
    void diseaseRejectsInvalidProbabilities() {
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.Disease(-0.01, 0.5));
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.Disease(0.5, 1.01));
        assertThrows(IllegalArgumentException.class, () -> new SIRSettings.Disease(Double.NaN, 0.5));
    }

    private static SIRSettings settings(SIRSettings.InitialStates initialStates) {
        return new SIRSettings(
                new SIRSettings.SIRModelSettings(1, 10, 1L),
                initialStates,
                new SIRSettings.Environment(new SIRSettings.Environment.Area(10, 10)),
                new SIRSettings.Movement(0.5, 2),
                new SIRSettings.Disease(0.5, 0.02)
        );
    }
}
