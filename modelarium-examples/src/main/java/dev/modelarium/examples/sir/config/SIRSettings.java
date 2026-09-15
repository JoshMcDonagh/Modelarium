package dev.modelarium.examples.sir.config;

import java.util.Objects;

public record SIRSettings(
    SIRModelSettings modelSettings,
    InitialStates initialStates,
    Environment environment,
    Movement movement,
    Disease disease
) {
    public SIRSettings {
        Objects.requireNonNull(modelSettings, "modelSettings must be set");
        Objects.requireNonNull(initialStates, "initialStates must be set");
        Objects.requireNonNull(environment, "environment must be set");
        Objects.requireNonNull(movement, "movement must be set");
        Objects.requireNonNull(disease, "disease must be set");

        long populationSize = (long) initialStates.S() + initialStates.I() + initialStates.R();
        if (populationSize == 0)
            throw new IllegalArgumentException("The initial population must contain at least one agent");
        if (populationSize > Integer.MAX_VALUE)
            throw new IllegalArgumentException("The initial population must not exceed Integer.MAX_VALUE");
    }

    public int populationSize() {
        return initialStates.S() + initialStates.I() + initialStates.R();
    }

    public record SIRModelSettings(int numOfCores, int numOfTicks, long seed) {
        public SIRModelSettings {
            if (numOfCores <= 0)
                throw new IllegalArgumentException("numOfCores must be greater than 0");
            if (numOfTicks <= 0)
                throw new IllegalArgumentException("numOfTicks must be greater than 0");
        }
    }

    public record InitialStates(int S, int I, int R) {
        public InitialStates {
            if (S < 0 || I < 0 || R < 0)
                throw new IllegalArgumentException("Initial S, I and R counts must not be negative");
        }
    }

    public record Environment(Area area) {
        public Environment {
            Objects.requireNonNull(area, "area must be set");
        }

        public record Area(int width, int height) {
            public Area {
                if (width <= 0 || height <= 0)
                    throw new IllegalArgumentException("Area width and height must be greater than 0");
            }
        }
    }

    public record Movement(double probabilityPerTick, double contactDistance) {
        public Movement {
            requireProbability(probabilityPerTick, "probabilityPerTick");
            if (!Double.isFinite(contactDistance) || contactDistance < 0)
                throw new IllegalArgumentException("contactDistance must be finite and at least 0");
        }
    }

    public record Disease(double infectionProbabilityPerContact, double recoveryProbabilityPerTick) {
        public Disease {
            requireProbability(infectionProbabilityPerContact, "infectionProbabilityPerContact");
            requireProbability(recoveryProbabilityPerTick, "recoveryProbabilityPerTick");
        }
    }

    private static void requireProbability(double probability, String name) {
        if (!Double.isFinite(probability) || probability < 0 || probability > 1)
            throw new IllegalArgumentException(name + " must be between 0 and 1 inclusive");
    }
}
