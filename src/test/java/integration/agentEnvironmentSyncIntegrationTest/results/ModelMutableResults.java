package integration.agentEnvironmentSyncIntegrationTest.results;

import modelarium.results.mutable.MutableResults;

import java.util.ArrayList;
import java.util.List;

public class ModelMutableResults extends MutableResults {

    private static double asDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return 0.0; }
    }

    @Override
    protected List<?> accumulateAgentPropertyResults(String attributeSetName, String propertyName,
                                                     List<?> accumulatedValues, List<?> valuesToBeProcessed) {
        // Convert incoming values to a double list
        List<Double> next = new ArrayList<>(valuesToBeProcessed == null ? 0 : valuesToBeProcessed.size());
        if (valuesToBeProcessed != null) {
            for (Object v : valuesToBeProcessed) next.add(asDouble(v));
        }

        // First agent seen → just seed
        if (accumulatedValues == null) return next;

        // Sum element-wise with whatever is already accumulated
        List<Double> out = new ArrayList<>(Math.max(accumulatedValues.size(), next.size()));
        int n = Math.min(accumulatedValues.size(), next.size());
        for (int i = 0; i < n; i++) {
            out.add(asDouble(accumulatedValues.get(i)) + next.get(i));
        }
        // If lengths differ, carry over the remainder
        for (int i = n; i < accumulatedValues.size(); i++) out.add(asDouble(accumulatedValues.get(i)));
        for (int i = n; i < next.size(); i++) out.add(next.get(i));
        return out;
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

    // Environment processing: identity passthrough (matches base hook signature)
    @Override
    protected List<?> processEnvironmentPropertyResults(String attributeName, String propertyName, List<?> propertyValues) {
        return propertyValues;
    }
}
