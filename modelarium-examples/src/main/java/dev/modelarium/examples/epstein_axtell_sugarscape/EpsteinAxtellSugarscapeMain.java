package dev.modelarium.examples.epstein_axtell_sugarscape;

import dev.modelarium.examples.epstein_axtell_sugarscape.config.SettingsLoader;
import dev.modelarium.examples.epstein_axtell_sugarscape.config.SugarscapeSettings;
import dev.modelarium.examples.epstein_axtell_sugarscape.entities.agents.SugarscapeAgentGenerator;
import dev.modelarium.examples.epstein_axtell_sugarscape.entities.environment.SugarscapeEnvironmentGenerator;
import dev.modelarium.examples.epstein_axtell_sugarscape.replication.SugarscapeExperimentExporter;
import dev.modelarium.examples.epstein_axtell_sugarscape.replication.SugarscapeRunResult;
import dev.modelarium.examples.epstein_axtell_sugarscape.scheduler.SugarscapeRunSpec;
import dev.modelarium.examples.epstein_axtell_sugarscape.scheduler.SugarscapeScheduler;
import modelarium.Config;
import modelarium.Model;
import modelarium.results.readonly.ReadOnlyResults;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

/**
 * Replicates the complete experiment sequence in Chapter II, "Life and Death on the Sugarscape", of Epstein and
 * Axtell's 1996 book Growing Artificial Societies.
 *
 * <p>Later chapters add sex/culture/combat, two-commodity trade and disease; they are deliberately outside this
 * example because they are distinct extensions of the Chapter II Sugarscape model rather than additional runs of the
 * same experiment.
 */
public final class EpsteinAxtellSugarscapeMain {
    private static final String CONFIG_RESOURCE =
            "dev/modelarium/examples/epstein_axtell_sugarscape/sugarscape-config.json";
    private static final String OUTPUT_DIRECTORY = "modelarium-examples/output/epstein_axtell_sugarscape";

    private EpsteinAxtellSugarscapeMain() {}

    public static void main(String[] args) {
        SugarscapeSettings settings = SettingsLoader.load(CONFIG_RESOURCE);
        SplittableRandom seeds = new SplittableRandom(settings.baseSeed());
        List<SugarscapeRunResult> results = new ArrayList<>();
        ReadOnlyResults rawResults = null;

        System.out.println("Replicating Epstein & Axtell (1996), Chapter II: Life and Death on the Sugarscape");

        // Immediate growback: the terrace-sticking / hiving demonstration preceding the G1 experiments.
        SugarscapeRunSpec immediate = SugarscapeRunSpec.standard(
                "00_immediate_growback", 400, settings.immediateGrowbackTicks(), SugarscapeRunSpec.GrowthMode.IMMEDIATE
        );
        results.add(run(immediate, 1, seeds.nextLong()).result());

        // Animation II-2 and Figure II-4: G1 + M, selection without reproduction.
        SugarscapeRunSpec selection = SugarscapeRunSpec.standard(
                "01_animation_ii_2_selection", 400, settings.selectionTicks(), SugarscapeRunSpec.GrowthMode.CONSTANT
        );
        RunExecution selectionExecution = run(selection, 1, seeds.nextLong());
        results.add(selectionExecution.result());

        // Figure II-5: 500 initial agents, 10 independent runs for every mean-vision 1..10 x mean-metabolism 1..3 cell.
        int totalCarryingRuns = 10 * 3 * settings.carryingCapacityReplications();
        int carryingRun = 0;
        for (int meanVision = 1; meanVision <= 10; meanVision++) {
            for (int meanMetabolism = 1; meanMetabolism <= 3; meanMetabolism++) {
                for (int replication = 1; replication <= settings.carryingCapacityReplications(); replication++) {
                    carryingRun++;
                    SugarscapeRunSpec spec = new SugarscapeRunSpec(
                            "02_figure_ii_5_carrying_capacity",
                            500,
                            settings.carryingCapacityTicks(),
                            SugarscapeRunSpec.GrowthMode.CONSTANT,
                            SugarscapeRunSpec.PlacementMode.RANDOM,
                            false, false, false,
                            1, 2 * meanVision - 1,
                            1, 2 * meanMetabolism - 1,
                            5, 25,
                            Integer.MAX_VALUE, Integer.MAX_VALUE
                    );
                    results.add(run(spec, replication, seeds.nextLong()).result());
                    if (carryingRun % 25 == 0 || carryingRun == totalCarryingRuns)
                        System.out.printf("  Figure II-5: %d/%d runs complete%n", carryingRun, totalCarryingRuns);
                }
            }
        }

        // Animations II-3 and II-4: finite lifetimes + replacement yield the long-run wealth distribution and Gini.
        SugarscapeRunSpec wealth = new SugarscapeRunSpec(
                "03_animations_ii_3_ii_4_wealth_distribution",
                250,
                settings.wealthTicks(),
                SugarscapeRunSpec.GrowthMode.CONSTANT,
                SugarscapeRunSpec.PlacementMode.RANDOM,
                true, false, false,
                1, 6, 1, 4, 5, 25, 60, 100
        );
        RunExecution wealthExecution = run(wealth, 1, seeds.nextLong());
        results.add(wealthExecution.result());
        // Preserve one ordinary Modelarium result tree; the paper-level exporter below captures every experiment.
        rawResults = wealthExecution.rawResults();

        // Animation II-5: neighbor-network formation under the same G1 + M ecology.
        SugarscapeRunSpec network = new SugarscapeRunSpec(
                "04_animation_ii_5_neighbour_networks",
                400, settings.neighbourNetworkTicks(), SugarscapeRunSpec.GrowthMode.CONSTANT,
                SugarscapeRunSpec.PlacementMode.RANDOM,
                false, false, true,
                1, 6, 1, 4, 5, 25, Integer.MAX_VALUE, Integer.MAX_VALUE
        );
        results.add(run(network, 1, seeds.nextLong()).result());

        // Animation II-6: a compact block exhibits collective diagonal-wave behaviour despite cardinal-only motion.
        SugarscapeRunSpec wave = new SugarscapeRunSpec(
                "05_animation_ii_6_collective_wave",
                400, settings.waveTicks(), SugarscapeRunSpec.GrowthMode.CONSTANT,
                SugarscapeRunSpec.PlacementMode.LOWER_RIGHT_BLOCK,
                false, false, false,
                1, 6, 1, 4, 5, 25, Integer.MAX_VALUE, Integer.MAX_VALUE
        );
        results.add(run(wave, 1, seeds.nextLong()).result());

        // Animation II-7: S[1,8,50], alternating northern/southern summer every 50 periods.
        SugarscapeRunSpec seasonal = SugarscapeRunSpec.standard(
                "06_animation_ii_7_seasonal_migration",
                400, settings.seasonalTicks(), SugarscapeRunSpec.GrowthMode.SEASONAL
        );
        results.add(run(seasonal, 1, seeds.nextLong()).result());

        // Animation II-8: G1 + M + P11 + D1; pollution starts at t=50 and diffusion at t=100.
        SugarscapeRunSpec pollution = new SugarscapeRunSpec(
                "07_animation_ii_8_pollution",
                400, settings.pollutionTicks(), SugarscapeRunSpec.GrowthMode.CONSTANT,
                SugarscapeRunSpec.PlacementMode.RANDOM,
                false, true, false,
                1, 6, 1, 4, 5, 25, Integer.MAX_VALUE, Integer.MAX_VALUE
        );
        results.add(run(pollution, 1, seeds.nextLong()).result());

        if (rawResults == null)
            throw new IllegalStateException("No Sugarscape result was available for Modelarium export");

        Path output = rawResults.export(OUTPUT_DIRECTORY);
        SugarscapeExperimentExporter.export(output, settings, results);
        System.out.println("Sugarscape Chapter II replication results exported to: " + output.toAbsolutePath());
    }

    private static RunExecution run(SugarscapeRunSpec spec, int runNumber, long seed) {
        System.out.printf("  %-48s run %d (seed=%d)%n", spec.experiment(), runNumber, seed);
        SugarscapeScheduler scheduler = new SugarscapeScheduler(spec);
        Config config = Config.builder()
                .populationSize(spec.populationSize())
                .tickCount(spec.ticks())
                .threadCount(1)
                .areThreadsSynced(true)
                .agentGenerator(new SugarscapeAgentGenerator(spec))
                .environmentGenerator(new SugarscapeEnvironmentGenerator())
                .scheduler(scheduler)
                .seed(seed)
                .build();
        Model model = new Model(config);
        model.run();
        ReadOnlyResults raw = model.getResults();
        SugarscapeRunResult result = new SugarscapeRunResult(
                spec.experiment(), runNumber, seed, spec,
                scheduler.metrics(), scheduler.snapshots(), scheduler.initialWealths(), scheduler.finalWealths()
        );
        return new RunExecution(result, raw);
    }

    private record RunExecution(SugarscapeRunResult result, ReadOnlyResults rawResults) {}
}
