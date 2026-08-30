package dev.modelarium.examples.epstein_axtell_sugarscape.scheduler;

/** Complete parameterisation for one Chapter II Sugarscape run. */
public final class SugarscapeRunSpec {
    public enum GrowthMode { IMMEDIATE, CONSTANT, SEASONAL }
    public enum PlacementMode { RANDOM, LOWER_RIGHT_BLOCK }

    private final String experiment;
    private final int populationSize;
    private final int ticks;
    private final GrowthMode growthMode;
    private final PlacementMode placementMode;
    private final boolean replacement;
    private final boolean pollution;
    private final boolean recordNeighbourNetwork;
    private final int visionMinimum;
    private final int visionMaximum;
    private final int metabolismMinimum;
    private final int metabolismMaximum;
    private final int initialWealthMinimum;
    private final int initialWealthMaximum;
    private final int maximumAgeMinimum;
    private final int maximumAgeMaximum;

    public SugarscapeRunSpec(
            String experiment,
            int populationSize,
            int ticks,
            GrowthMode growthMode,
            PlacementMode placementMode,
            boolean replacement,
            boolean pollution,
            boolean recordNeighbourNetwork,
            int visionMinimum,
            int visionMaximum,
            int metabolismMinimum,
            int metabolismMaximum,
            int initialWealthMinimum,
            int initialWealthMaximum,
            int maximumAgeMinimum,
            int maximumAgeMaximum
    ) {
        this.experiment = experiment;
        this.populationSize = populationSize;
        this.ticks = ticks;
        this.growthMode = growthMode;
        this.placementMode = placementMode;
        this.replacement = replacement;
        this.pollution = pollution;
        this.recordNeighbourNetwork = recordNeighbourNetwork;
        this.visionMinimum = visionMinimum;
        this.visionMaximum = visionMaximum;
        this.metabolismMinimum = metabolismMinimum;
        this.metabolismMaximum = metabolismMaximum;
        this.initialWealthMinimum = initialWealthMinimum;
        this.initialWealthMaximum = initialWealthMaximum;
        this.maximumAgeMinimum = maximumAgeMinimum;
        this.maximumAgeMaximum = maximumAgeMaximum;
    }

    public static SugarscapeRunSpec standard(String experiment, int populationSize, int ticks, GrowthMode growthMode) {
        return new SugarscapeRunSpec(
                experiment, populationSize, ticks, growthMode, PlacementMode.RANDOM,
                false, false, false,
                1, 6, 1, 4, 5, 25, Integer.MAX_VALUE, Integer.MAX_VALUE
        );
    }

    public String experiment() { return experiment; }
    public int populationSize() { return populationSize; }
    public int ticks() { return ticks; }
    public GrowthMode growthMode() { return growthMode; }
    public PlacementMode placementMode() { return placementMode; }
    public boolean replacement() { return replacement; }
    public boolean pollution() { return pollution; }
    public boolean recordNeighbourNetwork() { return recordNeighbourNetwork; }
    public int visionMinimum() { return visionMinimum; }
    public int visionMaximum() { return visionMaximum; }
    public int metabolismMinimum() { return metabolismMinimum; }
    public int metabolismMaximum() { return metabolismMaximum; }
    public int initialWealthMinimum() { return initialWealthMinimum; }
    public int initialWealthMaximum() { return initialWealthMaximum; }
    public int maximumAgeMinimum() { return maximumAgeMinimum; }
    public int maximumAgeMaximum() { return maximumAgeMaximum; }
}
