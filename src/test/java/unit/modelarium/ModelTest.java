package unit.modelarium;

import modelarium.Model;
import modelarium.ModelSettings;
import modelarium.results.Results;
import modelarium.scheduler.InOrderScheduler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Model} class.
 * <p>
 * These tests check that the model can run with a minimal configuration
 * and returns a results object without throwing errors.
 */
public class ModelTest {

    /**
     * A minimal mock subclass of Results for testing purposes.
     */
    public static class MockResults extends Results {
        @Override
        protected List<?> accumulateAgentPropertyResults(String attributeSetName, String propertyName, List<?> accumulatedValues, List<?> valuesToBeProcessed) {
            return valuesToBeProcessed;
        }

        @Override
        protected List<?> accumulateAgentPreEventResults(String attributeSetName, String preEventName, List<?> accumulatedValues, List<Boolean> valuesToBeProcessed) {
            return valuesToBeProcessed;
        }

        @Override
        protected List<?> accumulateAgentPostEventResults(String attributeSetName, String postEventName, List<?> accumulatedValues, List<Boolean> valuesToBeProcessed) {
            return valuesToBeProcessed;
        }
    }

    /**
     * Ensures that the model runs successfully with default settings and produces non-null results.
     */
    @Test
    public void testModelRunsSuccessfullyWithDefaultSettings() {
        ModelSettings settings = new ModelSettings();
        settings.setNumOfAgents(1);
        settings.setNumOfCores(1);
        settings.setNumOfTicksToRun(2);
        settings.setNumOfWarmUpTicks(0);
        settings.setAgentGenerator(new DefaultAgentGenerator());
        settings.setEnvironmentGenerator(new DefaultEnvironmentGenerator());
        settings.setModelScheduler(new InOrderScheduler());
        settings.setResultsClass(MockResults.class);

        AttributeSetCollection agentAttributes = new AttributeSetCollection();
        agentAttributes.add(new AttributeSet("agentSet"));

        AttributeSetCollection envAttributes = new AttributeSetCollection();
        envAttributes.add(new AttributeSet("envSet"));

        settings.setBaseAgentAttributeSetCollection(agentAttributes);
        settings.setBaseEnvironmentAttributeSetCollection(envAttributes);

        Model model = new Model(settings);

        try {
            Results results = model.run();
            assertNotNull(results, "Results should not be null after running the model.");
            assertTrue(results.getAgentNames().size() > 0, "At least one agent should be recorded in the results.");
        } catch (Exception e) {
            fail("Model run threw an exception: " + e.getMessage());
        }
    }
}
