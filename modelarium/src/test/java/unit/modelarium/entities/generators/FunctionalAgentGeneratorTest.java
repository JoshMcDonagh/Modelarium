package unit.modelarium.entities.generators;

import modelarium.Config;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.generators.FunctionalAgentGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.generators.AgentGeneratorTestHelpers.syncedConfig;

public class FunctionalAgentGeneratorTest {
    @Test
    public void testGenerateAgentsForEachThread_DelegatesToFunction() {
        List<AgentSet> expected = List.of(new AgentSet());
        FunctionalAgentGenerator generator = new FunctionalAgentGenerator((config, random) -> expected);
        Config config = syncedConfig(1, 1, 1);

        assertSame(expected, generator.generateAgentsForEachThread(config, new SplittableRandom()));
    }

    @Test
    public void testInternalReset_ResetFunctionProvided_DelegatesToFunction() {
        AtomicInteger resetCount = new AtomicInteger();
        FunctionalAgentGenerator generator = new FunctionalAgentGenerator(
                (config, random) -> List.of(new AgentSet()),
                resetCount::incrementAndGet
        );

        generator.internalReset();

        assertEquals(1, resetCount.get());
    }

    @Test
    public void testInternalReset_NoResetFunction_DoesNothing() {
        FunctionalAgentGenerator generator = new FunctionalAgentGenerator(
                (config, random) -> List.of(new AgentSet())
        );

        assertDoesNotThrow(generator::internalReset);
    }
}
