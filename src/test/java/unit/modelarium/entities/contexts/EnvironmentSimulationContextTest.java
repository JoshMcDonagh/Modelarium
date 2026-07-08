package unit.modelarium.entities.contexts;

import helpers.TestAttributes;
import helpers.TestFixtures;
import modelarium.Config;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.environments.Environment;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

public class EnvironmentSimulationContextTest {
    @Test
    public void testGetThisEntity() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Environment environment = TestFixtures.emptyEnvironment();
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithEnvironment(
                EnvironmentSimulationContext.class,
                config,
                environment
        );

        assertSame(environment, context.getThisEntity());
    }

    @Test
    public void testGetThisAttributeSet() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        EnvironmentAttributeSet set = TestAttributes.singlePropertyEnvironmentSet("owner", "time", "ticks");
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithAttributeSet(
                EnvironmentSimulationContext.class,
                config,
                set
        );

        assertSame(set, context.getThisAttributeSet());
    }

    @Test
    public void testGetThisAttribute() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        Attribute<?> attribute = new TestAttributes.EnvironmentTickProperty("ticker");
        EnvironmentSimulationContext context = TestFixtures.simulationContextWithAttribute(
                EnvironmentSimulationContext.class,
                config,
                attribute
        );

        assertSame(attribute, context.getThisAttribute());
    }

    @Test
    public void testGetEnvironment() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Config config = TestFixtures.syncedConfig(2, 10, 1);
        EnvironmentSimulationContext context = TestFixtures.emptySimulationContext(
                EnvironmentSimulationContext.class,
                config
        );

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                context::getEnvironment
        );

        assertEquals(
                "Context requester is already an Environment - use 'getThisEntity()' instead",
                exception.getMessage()
        );
    }

    
}
