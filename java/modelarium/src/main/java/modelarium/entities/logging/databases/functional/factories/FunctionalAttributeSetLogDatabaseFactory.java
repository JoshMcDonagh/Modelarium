package modelarium.entities.logging.databases.functional.factories;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;

import java.util.function.Supplier;

public class FunctionalAttributeSetLogDatabaseFactory implements AttributeSetLogDatabaseFactory {
    private final Supplier<AttributeSetLogDatabase> createLogic;

    public FunctionalAttributeSetLogDatabaseFactory(Supplier<AttributeSetLogDatabase> createLogic) {
        this.createLogic = createLogic;
    }

    @Override
    public AttributeSetLogDatabase create() {
        return createLogic.get();
    }
}
