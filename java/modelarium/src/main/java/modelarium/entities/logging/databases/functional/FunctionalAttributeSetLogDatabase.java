package modelarium.entities.logging.databases.functional;

import modelarium.entities.logging.databases.AttributeSetLogDatabase;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FunctionalAttributeSetLogDatabase extends AttributeSetLogDatabase {
    private final Runnable connectLogic;
    private final Runnable disconnectLogic;
    private final BiConsumer<String, Object> addAttributeValueLogic;
    private final BiConsumer<String, List<Object>> setAttributeColumnLogic;
    private final Function<String, List<Object>> getAttributeColumnAsListLogic;

    public FunctionalAttributeSetLogDatabase(
            Runnable connectLogic,
            Runnable disconnectLogic,
            BiConsumer<String, Object> addAttributeValueLogic,
            BiConsumer<String, List<Object>> setAttributeColumnLogic,
            Function<String, List<Object>> getAttributeColumnAsListLogic
    ) {
        super();
        this.connectLogic = connectLogic;
        this.disconnectLogic = disconnectLogic;
        this.addAttributeValueLogic = addAttributeValueLogic;
        this.setAttributeColumnLogic = setAttributeColumnLogic;
        this.getAttributeColumnAsListLogic = getAttributeColumnAsListLogic;
    }

    public FunctionalAttributeSetLogDatabase(
            String databasePath,
            Runnable connectLogic,
            Runnable disconnectLogic,
            BiConsumer<String, Object> addAttributeValueLogic,
            BiConsumer<String, List<Object>> setAttributeColumnLogic,
            Function<String, List<Object>> getAttributeColumnAsListLogic
    ) {
        super(databasePath);
        this.connectLogic = connectLogic;
        this.disconnectLogic = disconnectLogic;
        this.addAttributeValueLogic = addAttributeValueLogic;
        this.setAttributeColumnLogic = setAttributeColumnLogic;
        this.getAttributeColumnAsListLogic = getAttributeColumnAsListLogic;
    }

    @Override
    public void connect() {
        connectLogic.run();
    }

    @Override
    public void disconnect() {
        disconnectLogic.run();
    }

    @Override
    public <T> void addAttributeValue(String attributeName, T attributeValue) {
        addAttributeValueLogic.accept(attributeName, attributeValue);
    }

    @Override
    public void setAttributeColumn(String attributeName, List<Object> attributeValues) {
        setAttributeColumnLogic.accept(attributeName, propertyValues);
    }

    @Override
    public List<Object> getAttributeColumnAsList(String attributeName) {
        return getAttributeColumnAsListLogic.apply(attributeName);
    }
}
