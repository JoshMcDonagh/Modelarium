package dev.modelarium.examples.sirbasic.config;

public record SIRSettings(
    SIRModelSettings modelSettings,
    InitialStates initialStates,
    Environment environment,
    Movement movement,
    Disease disease
) {
    public record SIRModelSettings(int numOfCores, int numOfTicks, int numOfWarmUpTicks) {}
    public record InitialStates(int S, int I, int R) {}
    public record Environment(Area area) { public record Area(int width, int height) {} }
    public record Movement(double probabilityPerTick, double contactDistance) {}
    public record Disease(double infectionProbabilityPerContact, double recoveryProbabilityPerTick) {}
}
