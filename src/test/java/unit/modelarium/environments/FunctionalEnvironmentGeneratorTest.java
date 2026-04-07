package unit.modelarium.environments;

import modelarium.Config;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.FunctionalEnvironmentGenerator;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Unit test for {@link FunctionalEnvironmentGenerator}.
 *
 * <p>Verifies that the generator delegates to the user-defined function correctly.</p>
 */
public class FunctionalEnvironmentGeneratorTest {

    @Test
    void testGenerateEnvironmentReturnsExpectedObject() {
        Environment mockEnvironment = mock(Environment.class);
        Config mockSettings = mock(Config.class);

        Function<Config, Environment> generatorFunction = settings -> mockEnvironment;

        FunctionalEnvironmentGenerator generator = new FunctionalEnvironmentGenerator(generatorFunction);

        Environment result = generator.generateEnvironment(mockSettings);

        assertSame(mockEnvironment, result, "The environment returned should match the one provided by the function");
    }
}
