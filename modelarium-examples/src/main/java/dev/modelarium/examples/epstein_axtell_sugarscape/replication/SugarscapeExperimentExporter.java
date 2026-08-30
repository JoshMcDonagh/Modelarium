package dev.modelarium.examples.epstein_axtell_sugarscape.replication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.modelarium.examples.epstein_axtell_sugarscape.config.SugarscapeSettings;
import dev.modelarium.examples.epstein_axtell_sugarscape.scheduler.SugarscapeScheduler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.ToDoubleFunction;

/** Writes the paper/book-facing Chapter II replication tables alongside Modelarium's ordinary export. */
public final class SugarscapeExperimentExporter {
    private SugarscapeExperimentExporter() {}

    public static void export(Path modelariumOutput, SugarscapeSettings settings, List<SugarscapeRunResult> runs) {
        Path root = modelariumOutput.resolve("epstein_axtell_1996_chapter_ii_replication");
        try {
            Files.createDirectories(root);
            writeExperimentConfig(root, settings);
            writeManifest(root, runs);
            writeReadme(root);
            writeCapacityMap(root.resolve("landscape_capacity_map.csv"), single(runs, "00_immediate_growback").snapshots());

            for (SugarscapeRunResult run : runs) {
                if (!run.experiment().equals("02_figure_ii_5_carrying_capacity")) {
                    Path directory = root.resolve(run.experiment());
                    Files.createDirectories(directory);
                    writeTimeSeries(directory.resolve("time_series.csv"), run.metrics());
                    if (!run.snapshots().isEmpty())
                        writeSnapshots(directory.resolve("landscape_snapshots.csv"), run.snapshots());
                }
            }

            List<SugarscapeRunResult> carrying = filter(runs, "02_figure_ii_5_carrying_capacity");
            Path carryingDirectory = root.resolve("02_figure_ii_5_carrying_capacity");
            Files.createDirectories(carryingDirectory);
            writeCarryingCapacityRuns(carryingDirectory.resolve("run_results.csv"), carrying);
            writeCarryingCapacityAggregate(carryingDirectory.resolve("figure_ii_5_aggregate.csv"), carrying);

            SugarscapeRunResult wealth = single(runs, "03_animations_ii_3_ii_4_wealth_distribution");
            Path wealthDirectory = root.resolve(wealth.experiment());
            writeWealthDistribution(wealthDirectory.resolve("initial_wealth_distribution.csv"), wealth.initialWealths());
            writeWealthDistribution(wealthDirectory.resolve("final_wealth_distribution.csv"), wealth.finalWealths());
            writeLorenzCurve(wealthDirectory.resolve("initial_lorenz_curve.csv"), wealth.initialWealths());
            writeLorenzCurve(wealthDirectory.resolve("final_lorenz_curve.csv"), wealth.finalWealths());

            writePaperComparison(root.resolve("paper_findings_comparison.csv"), runs);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export Sugarscape replication results", e);
        }
    }

    private static void writeExperimentConfig(Path root, SugarscapeSettings settings) throws IOException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("source", "Epstein & Axtell (1996), Growing Artificial Societies, Chapter II: Life and Death on the Sugarscape");
        data.put("scope", "Complete Chapter II experimental sequence; later-book sex/culture/combat, trade, and disease extensions are outside this example");
        data.put("landscape", "50x50 toroidal twin-peaked sugar-capacity reconstruction: peaks at (15,35) and (35,15), capacities 4/3/2/1 within radii 6/11/16/19; chosen because the book specifies the terrain visually rather than as a machine-readable map");
        data.put("baseSeed", settings.baseSeed());
        data.put("immediateGrowbackTicks", settings.immediateGrowbackTicks());
        data.put("selectionTicks", settings.selectionTicks());
        data.put("carryingCapacityTicks", settings.carryingCapacityTicks());
        data.put("carryingCapacityReplications", settings.carryingCapacityReplications());
        data.put("wealthTicks", settings.wealthTicks());
        data.put("neighbourNetworkTicks", settings.neighbourNetworkTicks());
        data.put("waveTicks", settings.waveTicks());
        data.put("seasonalTicks", settings.seasonalTicks());
        data.put("pollutionTicks", settings.pollutionTicks());
        new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                .writeValue(root.resolve("experiment_config.json").toFile(), data);
    }

    private static void writeManifest(Path root, List<SugarscapeRunResult> runs) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(root.resolve("experiment_manifest.csv"), StandardCharsets.UTF_8)) {
            writer.write("experiment,description,number_of_runs\n");
            Map<String, String> descriptions = new LinkedHashMap<>();
            descriptions.put("00_immediate_growback", "Immediate-growback hiving and terrace sticking");
            descriptions.put("01_animation_ii_2_selection", "Animation II-2 / Figure II-4: G1 + M carrying capacity and selection");
            descriptions.put("02_figure_ii_5_carrying_capacity", "Figure II-5: carrying-capacity parameter sweep over mean vision and metabolism");
            descriptions.put("03_animations_ii_3_ii_4_wealth_distribution", "Animations II-3/II-4: replacement, wealth distribution, Lorenz curve and Gini");
            descriptions.put("04_animation_ii_5_neighbour_networks", "Animation II-5: emergent spatial neighbour networks");
            descriptions.put("05_animation_ii_6_collective_wave", "Animation II-6: collective wave from cardinal-only movement");
            descriptions.put("06_animation_ii_7_seasonal_migration", "Animation II-7: seasonal migration under S[1,8,50]");
            descriptions.put("07_animation_ii_8_pollution", "Animation II-8: pollution formation P11 and diffusion D1");
            for (Map.Entry<String, String> entry : descriptions.entrySet()) {
                long count = runs.stream().filter(r -> r.experiment().equals(entry.getKey())).count();
                writer.write(csv(entry.getKey()) + "," + csv(entry.getValue()) + "," + count + "\n");
            }
        }
    }

    private static void writeReadme(Path root) throws IOException {
        String text = """
                Epstein & Axtell (1996) Sugarscape — Chapter II replication
                ============================================================

                This directory contains experiment-level results for the full experimental sequence in Chapter II,
                \"Life and Death on the Sugarscape\", of Growing Artificial Societies: Social Science from the Bottom Up.

                The book, rather than a standalone paper, is the primary source. Chapter II introduces the canonical
                one-resource Sugarscape. Chapters III-V subsequently add sex/culture/combat, sugar-and-spice trade,
                and disease; those are separate extensions and are not silently folded into this example.

                Rules reproduced here:
                  G∞     immediate growback
                  G1     one unit of growback per period up to site capacity
                  M      cardinal vision, maximum sugar, nearest-site tie break, random residual tie break
                  R60,100 finite lifetimes and replacement
                  S1,8,50 seasonal summer/winter growback with 50-period season changes
                  P11    pollution from harvested sugar and metabolism
                  D1     pollution diffusion every period once enabled

                The original book does not publish machine-readable coordinates for every plotted/animated outcome.
                Accordingly paper_findings_comparison.csv distinguishes numerical docking targets from qualitative
                animation targets. The book specifies the canonical twin-peaked geography visually rather than as a machine-readable algorithm.
                This implementation therefore uses an independently generated 50x50 toroidal radial-contour reconstruction
                with peaks at (15,35) and (35,15), and capacity bands 4/3/2/1 at radii 6/11/16/19. This avoids silently
                treating any later implementation's terrain file as the original data.

                Primary source:
                  Joshua M. Epstein and Robert Axtell (1996), Growing Artificial Societies: Social Science from the Bottom Up,
                  Chapter II, MIT Press / Brookings Institution Press.
                  DOI: 10.7551/mitpress/3374.001.0001

                Cross-platform replication reference:
                  Anthony Bigbee, Claudio Cioffi-Revilla and Sean Luke, Replication of Sugarscape Using MASON (2007).
                """;
        Files.writeString(root.resolve("README.txt"), text, StandardCharsets.UTF_8);
    }


    private static void writeCapacityMap(Path path, List<SugarscapeScheduler.SnapshotCell> snapshots) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("x,y,capacity\n");
            snapshots.stream().filter(cell -> cell.step() == 0).forEach(cell -> {
                try {
                    writer.write(cell.x() + "," + cell.y() + "," + cell.capacity() + "\n");
                } catch (IOException e) {
                    throw new java.io.UncheckedIOException(e);
                }
            });
        } catch (java.io.UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private static void writeTimeSeries(Path path, List<SugarscapeScheduler.Metrics> metrics) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("step,population,mean_vision,mean_metabolism,mean_wealth,maximum_wealth,gini,north_population,south_population,high_capacity_occupancy_fraction,total_pollution,mean_high_capacity_pollution,neighbour_edges,neighbour_components,largest_neighbour_component,neighbour_graph_has_cycle\n");
            for (SugarscapeScheduler.Metrics m : metrics) {
                writer.write(String.format(Locale.ROOT,
                        "%d,%d,%.8f,%.8f,%.8f,%d,%.8f,%d,%d,%.8f,%.8f,%.8f,%d,%d,%d,%s%n",
                        m.step(), m.population(), m.meanVision(), m.meanMetabolism(), m.meanWealth(), m.maximumWealth(),
                        m.gini(), m.northPopulation(), m.southPopulation(), m.highCapacityOccupancyFraction(),
                        m.totalPollution(), m.meanHighCapacityPollution(), m.neighbourEdges(), m.neighbourComponents(),
                        m.largestNeighbourComponent(), m.neighbourGraphHasCycle()));
            }
        }
    }

    private static void writeSnapshots(Path path, List<SugarscapeScheduler.SnapshotCell> snapshots) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("step,x,y,capacity,sugar,pollution,occupied\n");
            for (SugarscapeScheduler.SnapshotCell cell : snapshots) {
                writer.write(String.format(Locale.ROOT, "%d,%d,%d,%d,%d,%.8f,%s%n",
                        cell.step(), cell.x(), cell.y(), cell.capacity(), cell.sugar(), cell.pollution(), cell.occupied()));
            }
        }
    }

    private static void writeCarryingCapacityRuns(Path path, List<SugarscapeRunResult> runs) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("mean_initial_vision,mean_initial_metabolism,replication,seed,final_population,final_mean_vision,final_mean_metabolism\n");
            for (SugarscapeRunResult run : runs) {
                double targetVision = (run.spec().visionMinimum() + run.spec().visionMaximum()) / 2.0;
                double targetMetabolism = (run.spec().metabolismMinimum() + run.spec().metabolismMaximum()) / 2.0;
                SugarscapeScheduler.Metrics m = run.finalMetrics();
                writer.write(String.format(Locale.ROOT, "%.1f,%.1f,%d,%d,%d,%.8f,%.8f%n",
                        targetVision, targetMetabolism, run.runNumber(), run.seed(), m.population(), m.meanVision(), m.meanMetabolism()));
            }
        }
    }

    private static void writeCarryingCapacityAggregate(Path path, List<SugarscapeRunResult> runs) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("mean_initial_vision,mean_initial_metabolism,runs,mean_final_population,sample_sd_final_population\n");
            for (int vision = 1; vision <= 10; vision++) {
                for (int metabolism = 1; metabolism <= 3; metabolism++) {
                    final int v = vision;
                    final int m = metabolism;
                    List<Double> populations = runs.stream()
                            .filter(r -> (r.spec().visionMinimum() + r.spec().visionMaximum()) / 2 == v)
                            .filter(r -> (r.spec().metabolismMinimum() + r.spec().metabolismMaximum()) / 2 == m)
                            .map(r -> (double) r.finalMetrics().population())
                            .toList();
                    writer.write(String.format(Locale.ROOT, "%d,%d,%d,%.8f,%.8f%n",
                            vision, metabolism, populations.size(), mean(populations), sampleSd(populations)));
                }
            }
        }
    }

    private static void writeWealthDistribution(Path path, List<Integer> wealth) throws IOException {
        if (wealth.isEmpty()) return;
        int max = wealth.stream().mapToInt(Integer::intValue).max().orElse(0);
        int width = Math.max(1, (int) Math.ceil((max + 1) / 20.0));
        int[] counts = new int[20];
        for (int value : wealth) counts[Math.min(19, Math.max(0, value) / width)]++;
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("bin_lower_inclusive,bin_upper_inclusive,count,percentage\n");
            for (int i = 0; i < counts.length; i++) {
                int lower = i * width;
                int upper = (i + 1) * width - 1;
                writer.write(String.format(Locale.ROOT, "%d,%d,%d,%.8f%n", lower, upper, counts[i], 100.0 * counts[i] / wealth.size()));
            }
        }
    }

    private static void writeLorenzCurve(Path path, List<Integer> wealth) throws IOException {
        int[] sorted = wealth.stream().mapToInt(Integer::intValue).map(v -> Math.max(0, v)).sorted().toArray();
        long total = Arrays.stream(sorted).asLongStream().sum();
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("population_share,wealth_share\n0.0,0.0\n");
            long cumulative = 0;
            for (int i = 0; i < sorted.length; i++) {
                cumulative += sorted[i];
                double populationShare = (i + 1.0) / sorted.length;
                double wealthShare = total == 0 ? 0 : cumulative / (double) total;
                writer.write(String.format(Locale.ROOT, "%.8f,%.8f%n", populationShare, wealthShare));
            }
        }
    }

    private static void writePaperComparison(Path path, List<SugarscapeRunResult> runs) throws IOException {
        SugarscapeRunResult selection = single(runs, "01_animation_ii_2_selection");
        SugarscapeRunResult wealth = single(runs, "03_animations_ii_3_ii_4_wealth_distribution");
        SugarscapeRunResult network = single(runs, "04_animation_ii_5_neighbour_networks");
        SugarscapeRunResult seasonal = single(runs, "06_animation_ii_7_seasonal_migration");
        SugarscapeRunResult pollution = single(runs, "07_animation_ii_8_pollution");
        List<SugarscapeRunResult> carrying = filter(runs, "02_figure_ii_5_carrying_capacity");

        boolean allPositiveSlopes = true;
        double previousAtVision1 = Double.POSITIVE_INFINITY;
        boolean orderedMetabolismLines = true;
        for (int metabolism = 1; metabolism <= 3; metabolism++) {
            double first = carryingMean(carrying, 1, metabolism);
            double last = carryingMean(carrying, 10, metabolism);
            allPositiveSlopes &= last > first;
            orderedMetabolismLines &= first < previousAtVision1;
            previousAtVision1 = first;
        }

        double initialGini = wealth.initialMetrics().gini();
        double finalGini = wealth.finalMetrics().gini();
        boolean networkCycleObserved = network.metrics().stream().anyMatch(SugarscapeScheduler.Metrics::neighbourGraphHasCycle);
        SugarscapeScheduler.Metrics seasonal50 = nearest(seasonal.metrics(), 50);
        SugarscapeScheduler.Metrics seasonal100 = nearest(seasonal.metrics(), 100);
        double northShare50 = share(seasonal50.northPopulation(), seasonal50.population());
        double northShare100 = share(seasonal100.northPopulation(), seasonal100.population());
        SugarscapeScheduler.Metrics pollution50 = nearest(pollution.metrics(), 50);
        SugarscapeScheduler.Metrics pollution100 = nearest(pollution.metrics(), 100);
        SugarscapeScheduler.Metrics pollutionFinal = pollution.finalMetrics();

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("source_item,paper_finding_or_target,modelarium_finding,comparison_type,status\n");
            row(writer, "Animation II-2", "Hiving / peak clustering / terrace sticking under G1 + M",
                    String.format(Locale.ROOT, "final population=%d; high-capacity occupancy=%.3f", selection.finalMetrics().population(), selection.finalMetrics().highCapacityOccupancyFraction()),
                    "qualitative + diagnostics", "inspect exported snapshots/time series");
            row(writer, "Figure II-4 / carrying-capacity example", "Population approaches about 224; selection raises mean vision and lowers mean metabolism (reported around 4.1 and 1.8 after 500 periods)",
                    String.format(Locale.ROOT, "final population=%d; mean vision=%.3f; mean metabolism=%.3f", selection.finalMetrics().population(), selection.finalMetrics().meanVision(), selection.finalMetrics().meanMetabolism()),
                    "numerical landmarks", "reported side-by-side");
            row(writer, "Figure II-5", "Three roughly equally spaced carrying-capacity curves with small positive slopes as mean vision rises; lower metabolism supports more agents",
                    "all three slopes positive=" + allPositiveSlopes + "; metabolism ordering at vision=1=" + orderedMetabolismLines,
                    "shape/relationship", allPositiveSlopes && orderedMetabolismLines ? "PASS" : "CHECK");
            row(writer, "Animation II-3", "Long-run wealth distribution becomes strongly right-skewed / Pareto-like",
                    String.format(Locale.ROOT, "initial max wealth=%d; final max wealth=%d; mean wealth=%.3f", wealth.initialMetrics().maximumWealth(), wealth.finalMetrics().maximumWealth(), wealth.finalMetrics().meanWealth()),
                    "distributional", "inspect wealth histogram and Lorenz CSVs");
            row(writer, "Animation II-4", "Gini rises from about 0.230 initially to about 0.503 in the long run",
                    String.format(Locale.ROOT, "initial Gini=%.3f; final Gini=%.3f", initialGini, finalGini),
                    "numerical landmark", "reported side-by-side");
            row(writer, "Animation II-5", "Local neighbor relations form simple and elaborate connected networks, including cycles",
                    String.format(Locale.ROOT, "cycle observed=%s; final edges=%d; final largest component=%d", networkCycleObserved, network.finalMetrics().neighbourEdges(), network.finalMetrics().largestNeighbourComponent()),
                    "qualitative", networkCycleObserved ? "PASS" : "CHECK");
            row(writer, "Animation II-6", "A compact population can generate a collective diagonal wave although individual M moves are cardinal only",
                    "landscape snapshots exported at multiple times", "visual/qualitative", "manual visual comparison required");
            row(writer, "Animation II-7", "With S[1,8,50], population clusters and migrates between seasonal hemispheres",
                    String.format(Locale.ROOT, "north population share at t=50: %.3f; at t=100: %.3f", northShare50, northShare100),
                    "qualitative + phase diagnostic", "inspect seasonal time series/snapshots");
            row(writer, "Animation II-8", "P11 beginning at t=50 drives agents away from polluted productive sites; D1 from t=100 spreads pollution and changes migration",
                    String.format(Locale.ROOT, "high-capacity occupancy t50=%.3f, t100=%.3f, final=%.3f; total pollution final=%.1f", pollution50.highCapacityOccupancyFraction(), pollution100.highCapacityOccupancyFraction(), pollutionFinal.highCapacityOccupancyFraction(), pollutionFinal.totalPollution()),
                    "qualitative + phase diagnostic", "inspect pollution time series/snapshots");
        }
    }

    private static void row(BufferedWriter writer, String item, String paper, String modelarium, String type, String status) throws IOException {
        writer.write(csv(item) + "," + csv(paper) + "," + csv(modelarium) + "," + csv(type) + "," + csv(status) + "\n");
    }

    private static double carryingMean(List<SugarscapeRunResult> runs, int vision, int metabolism) {
        return runs.stream()
                .filter(r -> (r.spec().visionMinimum() + r.spec().visionMaximum()) / 2 == vision)
                .filter(r -> (r.spec().metabolismMinimum() + r.spec().metabolismMaximum()) / 2 == metabolism)
                .mapToInt(r -> r.finalMetrics().population()).average().orElse(Double.NaN);
    }

    private static SugarscapeScheduler.Metrics nearest(List<SugarscapeScheduler.Metrics> metrics, int step) {
        return metrics.stream().min(Comparator.comparingInt(m -> Math.abs(m.step() - step))).orElseThrow();
    }

    private static double share(int numerator, int denominator) { return denominator == 0 ? 0 : numerator / (double) denominator; }

    private static List<SugarscapeRunResult> filter(List<SugarscapeRunResult> runs, String experiment) {
        return runs.stream().filter(r -> r.experiment().equals(experiment)).toList();
    }

    private static SugarscapeRunResult single(List<SugarscapeRunResult> runs, String experiment) {
        return runs.stream().filter(r -> r.experiment().equals(experiment)).findFirst().orElseThrow();
    }

    private static double mean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
    }

    private static double sampleSd(List<Double> values) {
        if (values.size() < 2) return 0;
        double mean = mean(values);
        double ss = values.stream().mapToDouble(v -> (v - mean) * (v - mean)).sum();
        return Math.sqrt(ss / (values.size() - 1));
    }

    private static String csv(String value) {
        if (value == null) return "";
        if (!value.contains(",") && !value.contains("\"") && !value.contains("\n") && !value.contains("\r")) return value;
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
