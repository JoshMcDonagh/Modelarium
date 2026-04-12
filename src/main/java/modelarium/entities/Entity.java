package modelarium.entities;

import com.rits.cloning.Cloner;
import modelarium.Clock;
import modelarium.Config;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.attributes.*;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Entity<C extends Context, A extends AttributeSet<C>, L extends AttributeSetLog<C>> {
    private static final Cloner cloner = new Cloner();

    protected static Cloner getCloner() {
        return cloner;
    }

    private final String name;
    private final List<A> attributeSetList;
    private final Map<String, Integer> attributeSetIndexMap = new HashMap<>();

    private C context = null;

    protected Entity(String name, List<A> attributeSetList) {
        this.name = name;
        this.attributeSetList = attributeSetList;
        for (int i = 0; i < this.attributeSetList.size(); i++) {
            A attributeSet = this.attributeSetList.get(i);
            this.attributeSetIndexMap.put(attributeSet.name(), i);
        }
    }

    public void setLogDatabaseFactory(AttributeSetLogDatabaseFactory databaseFactory) {
        for (A attributeSet : attributeSetList)
            attributeSet.setLogDatabaseFactory(databaseFactory);
    }

    protected abstract C makeContextInstance(
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            Clock clock,
            RequestResponseInterface requestResponseInterface,
            Environment localEnvironment
    );

    public void createContext(
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            Clock clock,
            RequestResponseInterface requestResponseInterface,
            Environment localEnvironment
    ) {
        if (context != null)
            return;

        context = makeContextInstance(
                agentSet,
                config,
                contextCache,
                clock,
                requestResponseInterface,
                localEnvironment
        );
    }

    public C context() {
        return context;
    }

    public String name() {
        return name;
    }

    public int attributeSetCount() {
        return attributeSetList.size();
    }

    public int attributeCount() {
        int count = 0;

        for (A attributeSet : attributeSetList)
            count += attributeSet.size();

        return count;
    }

    public A getAttributeSet(int attributeSetIndex) {
        return attributeSetList.get(attributeSetIndex);
    }

    public A getAttributeSet(String attributeSetName) {
        return getAttributeSet(attributeSetIndexMap.get(attributeSetName));
    }

    public EntityLog<C,A,L> getLog() {
        return new EntityLog<C,A,L>(name, (List<A>) attributeSetList);
    }

    public void run() {
        for (A attributeSet : attributeSetList)
            attributeSet.run();
    }

    public abstract Entity<C,A,L> clone();
}
