package integration.syncedCachedBasicModelUsageIntegrationTest.results;

import modelarium.results.mutable.MutableResults;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelMutableResults extends MutableResults {

    private static double asDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        // If something odd sneaks in, try parsing, else treat as 0
        try { return Double.parseDouble(o.toString()); } catch (Exception e) { return 0.0; }
    }

    @Override
    protected List<?> accumulateAgentPropertyResults(String attributeSetName, String propertyName,
                                                     List<?> accumulatedValues, List<?> valuesToBeProcessed) {
        if (Objects.equals(attributeSetName, "food") && Objects.equals(propertyName, "Hunger")) {
            // Element-wise sum - tolerate empty/null/mismatched lengths and non-Double numerics
            int nA = (accumulatedValues == null) ? 0 : accumulatedValues.size();
            int nB = (valuesToBeProcessed == null) ? 0 : valuesToBeProcessed.size();
            int n = Math.max(nA, nB);

            List<Double> out = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                double a = (i < nA) ? asDouble(accumulatedValues.get(i)) : 0.0;
                double b = (i < nB) ? asDouble(valuesToBeProcessed.get(i)) : 0.0;
                out.add(a + b);
            }
            return out;
        }

        // Not aggregating other properties/events in this test model
        return new ArrayList<>();
    }

    @Override
    protected List<?> accumulateAgentPreEventResults(String attributeSetName, String preEventName,
                                                     List<?> accumulatedValues, List<Boolean> valuesToBeProcessed) {
        return new ArrayList<>();
    }

    @Override
    protected List<?> accumulateAgentPostEventResults(String attributeSetName, String postEventName,
                                                      List<?> accumulatedValues, List<Boolean> valuesToBeProcessed) {
        return new ArrayList<>();
    }
}
