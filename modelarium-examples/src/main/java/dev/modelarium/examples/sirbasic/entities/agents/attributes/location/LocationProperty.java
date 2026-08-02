package dev.modelarium.examples.sirbasic.entities.agents.attributes.location;

import dev.modelarium.examples.sirbasic.config.SettingsLoader;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.immutable.attributes.ImmutableEnvironmentAttributeSet;

import java.util.random.RandomGenerator;

public class LocationProperty extends AgentProperty<Coordinates> {
    private final double movementProbabilityPerTick;

    private Coordinates coordinates;

    public LocationProperty() {
        super("location", true, AttributeAccessLevel.PUBLIC, Coordinates.class);
        movementProbabilityPerTick = SettingsLoader
                .loadSIRConfig("dev/modelarium/examples/sirbasic/sir-config.json")
                .movement()
                .probabilityPerTick();
    }

    @Override
    protected void run(AgentContext context) {
        RandomGenerator random = context.getRandom();

        if (random.nextDouble(0.0, 1.0) < movementProbabilityPerTick) {
            ImmutableEnvironmentAttributeSet geography = context
                    .getEnvironment()
                    .getAttributeSet("geography");

            int geographicalAreaWidth = (int) geography.getProperty("width").get();
            int geographicalAreaHeight = (int) geography.getProperty("height").get();

            coordinates.moveRandomlyBy(random, 1, geographicalAreaWidth, geographicalAreaHeight);
        }
    }

    @Override
    protected void set(AgentContext context, Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    protected Coordinates get(AgentContext context) {
        return coordinates;
    }
}
