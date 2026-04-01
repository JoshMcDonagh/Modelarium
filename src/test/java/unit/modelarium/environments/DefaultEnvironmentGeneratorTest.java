package unit.modelarium.environments;

import modelarium.ModelSettings;
import modelarium.environments.Environment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DefaultEnvironmentGenerator} class.
 *
 * <p>Ensures that the environment is correctly generated using a deep copy
 * of the base environment attribute set provided in the model settings.
 */
public class DefaultEnvironmentGeneratorTest {

    @Test
    public void testGenerateEnvironmentReturnsExpectedEnvironment() {
        ModelSettings settings = new ModelSettings();
        AttributeSetCollection originalAttributes = new AttributeSetCollection();
        settings.setBaseEnvironmentAttributeSetCollection(originalAttributes);

        DefaultEnvironmentGenerator generator = new DefaultEnvironmentGenerator();
        Environment generatedEnvironment = generator.generateEnvironment(settings);

        assertNotNull(generatedEnvironment, "Environment should not be null.");
        assertEquals("Environment", generatedEnvironment.name(), "Environment name should be 'Environment'.");
        assertNotSame(originalAttributes, generatedEnvironment.getAttributeSetCollection(),
                "The attribute set should be a deep copy, not the same instance.");
    }
}
