package modelarium.entities;

import com.rits.cloning.Cloner;
import modelarium.Config;
import modelarium.clock.SimulationClock;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.logging.AttributeSetLog;
import modelarium.entities.logging.EntityLog;
import modelarium.entities.logging.databases.factories.AttributeSetLogDatabaseFactory;
import modelarium.internal.Internal;
import modelarium.multithreading.requestresponse.RequestResponseInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed abstract class Entity<SC extends SimulationContext, C extends Context, AS extends AttributeSet<SC,C>, ASL extends AttributeSetLog<SC>>
        permits Agent, Environment {
    private static final Cloner cloner = new Cloner();

    protected static Cloner getCloner() {
        return cloner;
    }

    private final String name;
    private final List<AS> attributeSetList;
    private final Map<String, Integer> attributeSetIndexMap = new HashMap<>();

    private SC context = null;

    protected Entity(String name, List<AS> attributeSetList) {
        this.name = name;
        this.attributeSetList = attributeSetList;
        for (int i = 0; i < this.attributeSetList.size(); i++) {
            AS attributeSet = this.attributeSetList.get(i);
            this.attributeSetIndexMap.put(attributeSet.name(), i);
        }
    }

    @Internal
    public void setLogDatabaseFactory(AttributeSetLogDatabaseFactory databaseFactory) {
        for (AS attributeSet : attributeSetList)
            attributeSet.setLogDatabaseFactory(databaseFactory);
    }

    protected abstract SC makeContextInstance(
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            SimulationClock clock,
            RequestResponseInterface requestResponseInterface,
            Environment localEnvironment
    );

    @Internal
    public void createContext(
            AgentSet agentSet,
            Config config,
            ContextCache contextCache,
            SimulationClock clock,
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

        for (AS attributeSet : attributeSetList)
            attributeSet.setContext(context);
    }

    public SC context() {
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

        for (AS attributeSet : attributeSetList)
            count += attributeSet.size();

        return count;
    }

    public AS getAttributeSet(int attributeSetIndex) {
        return attributeSetList.get(attributeSetIndex);
    }

    public AS getAttributeSet(String attributeSetName) {
        return getAttributeSet(attributeSetIndexMap.get(attributeSetName));
    }

    public EntityLog<SC, AS, ASL> getLog() {
        return new EntityLog<SC, AS, ASL>(name, attributeSetList);
    }

    public void run() {
        for (AS attributeSet : attributeSetList)
            attributeSet.run();
    }

    public abstract Entity<SC, C, AS, ASL> clone();
}
