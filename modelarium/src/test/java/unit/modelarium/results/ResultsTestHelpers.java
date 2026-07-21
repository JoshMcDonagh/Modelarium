package unit.modelarium.results;

import modelarium.entities.Entity;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.logging.databases.factories.MemoryBasedAttributeSetLogDatabaseFactory;
import modelarium.results.mutable.MutableResults;
import modelarium.results.mutable.MutableResultsForAgents;
import modelarium.results.mutable.MutableResultsForEnvironment;

import java.util.Arrays;
import java.util.List;

class ResultsTestHelpers {
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

    static AgentAttributeSet agentAttributeSet(String ownerName, String attributeSetName, String... propertyNames) {
        List<Attribute> properties = Arrays.stream(propertyNames)
                .map(propertyName -> (Attribute) new AgentCounterProperty(propertyName))
                .toList();
        return new AgentAttributeSet(attributeSetName, properties);
    }

    static EnvironmentAttributeSet environmentAttributeSet(String ownerName, String attributeSetName, String... propertyNames) {
        List<Attribute> properties = Arrays.stream(propertyNames)
                .map(propertyName -> (Attribute) new EnvironmentTickProperty(propertyName))
                .toList();
        return new EnvironmentAttributeSet(attributeSetName, properties);
    }

    static Agent agentWithMemoryLogs(String agentName, AgentAttributeSet... attributeSets) {
        Agent agent = new Agent(agentName, List.of(attributeSets));
        agent.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        return agent;
    }

    static Environment environmentWithMemoryLogs(String environmentName, EnvironmentAttributeSet... attributeSets) {
        Environment environment = new Environment(environmentName, List.of(attributeSets));
        environment.setLogDatabaseFactory(new MemoryBasedAttributeSetLogDatabaseFactory());
        return environment;
    }

    static Agent agentWithLoggedProperty(String agentName, String attributeSetName, String propertyName) {
        return agentWithMemoryLogs(agentName, agentAttributeSet(agentName, attributeSetName, propertyName));
    }

    static Environment environmentWithLoggedProperty(String environmentName, String attributeSetName, String propertyName) {
        return environmentWithMemoryLogs(environmentName, environmentAttributeSet(environmentName, attributeSetName, propertyName));
    }

    static AgentSet agentSet(Agent... agents) {
        return new AgentSet(List.of(agents));
    }

    static void record(Entity<?,?,?,?> entity, String attributeSetName, String attributeName, Object... values) {
        for (Object value : values)
            entity.getAttributeSet(attributeSetName).getLog().record(attributeName, value);
    }

    static MutableResultsForAgents agentResults(Agent... agents) {
        return new MutableResultsForAgents(agentSet(agents));
    }

    static MutableResultsForEnvironment environmentResults(Environment environment) {
        return new MutableResultsForEnvironment(environment);
    }

    static MutableResults mutableResults(MutableResultsForAgents agentsResults, MutableResultsForEnvironment environmentResults) {
        MutableResults results = new MutableResults();
        results.setAgentResults(agentsResults);
        results.setEnvironmentResults(environmentResults);
        return results;
    }
}
