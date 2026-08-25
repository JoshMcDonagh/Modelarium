package unit.modelarium.results;

import modelarium.entities.Entity;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.Environment;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import modelarium.results.Results;
import modelarium.results.ResultsForAgents;
import modelarium.results.ResultsForEnvironment;

import java.util.Arrays;
import java.util.List;

public class ResultsTestHelpers {
    private ResultsTestHelpers() {}

    static class AgentCounterProperty extends AgentProperty<Double> {
        private double value = 0.0;

        AgentCounterProperty(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC, Double.class);
        }

        @Override
        protected void run(AgentContext context) {
            value += 1.0;
        }

        @Override
        protected void set(AgentContext context, Double value) {
            this.value = value;
        }

        @Override
        protected Double get(AgentContext context) {
            return value;
        }
    }

    static class EnvironmentTickProperty extends EnvironmentProperty<Integer> {
        private int tick = 0;

        EnvironmentTickProperty(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC, Integer.class);
        }

        @Override
        protected void run(EnvironmentContext context) {
            tick++;
        }

        @Override
        protected void set(EnvironmentContext context, Integer value) {
            this.tick = value;
        }

        @Override
        protected Integer get(EnvironmentContext context) {
            return tick;
        }
    }

    public static AgentAttributeSet agentAttributeSet(String ownerName, String attributeSetName, String... propertyNames) {
        List<Attribute> properties = Arrays.stream(propertyNames)
                .map(propertyName -> (Attribute) new AgentCounterProperty(propertyName))
                .toList();
        return new AgentAttributeSet(attributeSetName, properties);
    }

    public static EnvironmentAttributeSet environmentAttributeSet(String ownerName, String attributeSetName, String... propertyNames) {
        List<Attribute> properties = Arrays.stream(propertyNames)
                .map(propertyName -> (Attribute) new EnvironmentTickProperty(propertyName))
                .toList();
        return new EnvironmentAttributeSet(attributeSetName, properties);
    }

    public static Agent agentWithMemoryLogs(String agentName, AgentAttributeSet... attributeSets) {
        Agent agent = new Agent(agentName, List.of(attributeSets));
        agent.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        return agent;
    }

    public static Environment environmentWithMemoryLogs(String environmentName, EnvironmentAttributeSet... attributeSets) {
        Environment environment = new Environment(environmentName, List.of(attributeSets));
        environment.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        return environment;
    }

    public static Agent agentWithLoggedProperty(String agentName, String attributeSetName, String propertyName) {
        return agentWithMemoryLogs(agentName, agentAttributeSet(agentName, attributeSetName, propertyName));
    }

    public static Environment environmentWithLoggedProperty(String environmentName, String attributeSetName, String propertyName) {
        return environmentWithMemoryLogs(environmentName, environmentAttributeSet(environmentName, attributeSetName, propertyName));
    }

    public static AgentSet agentSet(Agent... agents) {
        return new AgentSet(List.of(agents));
    }

    public static void record(Entity<?,?,?,?> entity, String attributeSetName, String attributeName, Object... values) {
        for (Object value : values)
            entity.getAttributeSet(attributeSetName).getLog().record(attributeName, value);
    }

    public static ResultsForAgents agentResults(Agent... agents) {
        return new ResultsForAgents(agentSet(agents));
    }

    public static ResultsForEnvironment environmentResults(Environment environment) {
        return new ResultsForEnvironment(environment);
    }

    public static Results mutableResults(ResultsForAgents agentsResults, ResultsForEnvironment environmentResults) {
        Results results = new Results();
        results.setAgentResults(agentsResults);
        results.setEnvironmentResults(environmentResults);
        return results;
    }
}
