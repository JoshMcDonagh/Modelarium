package dev.modelarium.examples.epstein_axtell_sugarscape.replication;

import dev.modelarium.examples.epstein_axtell_sugarscape.scheduler.SugarscapeScheduler;
import dev.modelarium.examples.epstein_axtell_sugarscape.scheduler.SugarscapeRunSpec;

import java.util.List;

/** Captured experiment-level output for one independent Sugarscape run. */
public record SugarscapeRunResult(
        String experiment,
        int runNumber,
        long seed,
        SugarscapeRunSpec spec,
        List<SugarscapeScheduler.Metrics> metrics,
        List<SugarscapeScheduler.SnapshotCell> snapshots,
        List<Integer> initialWealths,
        List<Integer> finalWealths
) {
    public SugarscapeScheduler.Metrics initialMetrics() { return metrics.getFirst(); }
    public SugarscapeScheduler.Metrics finalMetrics() { return metrics.getLast(); }
}
