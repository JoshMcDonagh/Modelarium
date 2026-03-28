package integration.syncedCachedBasicModelUsageIntegrationTest.attributes.postevents;

import modelarium.attributes.Event;
import modelarium.attributes.Property;

public class EatFood extends Event {

    public EatFood() {
        super("Eat Food", false);
    }

    public EatFood(EatFood other) {
        super(other);
    }

    @Override
    public boolean isTriggered() {
        Property<Double> hunger = (Property<Double>) getAssociatedModelElement().getAttributeSetCollection().get("food").getProperties().get("Hunger");
        return hunger.get() > 0.7;
    }

    @Override
    public void run() {
        Property<Double> hunger = (Property<Double>) getAssociatedModelElement().getAttributeSetCollection().get("food").getProperties().get("Hunger");
        hunger.set(hunger.get() - 0.5);
    }
}
