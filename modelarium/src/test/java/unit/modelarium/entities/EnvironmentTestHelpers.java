package unit.modelarium.entities;

import modelarium.Config;
import modelarium.clock.Clock;
import modelarium.entities.generators.DefaultAgentGenerator;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.attributes.sets.EnvironmentAttributeSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.Environment;
import modelarium.entities.generators.EnvironmentGenerator;
import modelarium.multithreading.requestresponse.RequestResponseController;

import java.util.List;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

public class EnvironmentTestHelpers {
    private EnvironmentTestHelpers() {}

    public static Environment emptyEnvironment() {
        return new Environment("env", List.of());
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

    @SuppressWarnings("unchecked")
    static EnvironmentAttributeSet environmentAttributeSet(String ownerName, String attributeSetName, EnvironmentProperty<?>... properties) {
        return new EnvironmentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(properties)
        );
    }

    static EnvironmentAttributeSet emptyEnvironmentAttributeSet(String ownerName, String attributeSetName) {
        return new EnvironmentAttributeSet(attributeSetName, List.of());
    }

    public static DefaultAgentGenerator agentGenerator() {
        return new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected Agent generateAgent(Config config, RandomGenerator random) {
                return new Agent("agent_" + index++, List.of());
            }
        };
    }

    public static EnvironmentGenerator environmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public Environment generateEnvironment(Config config, RandomGenerator random) {
                return emptyEnvironment();
            }
        };
    }

    public static Config syncedConfig(int populationSize, int tickCount, int threadCount) {
        return Config.builder()
                .populationSize(populationSize)
                .tickCount(tickCount)
                .threadCount(threadCount)
                .areThreadsSynced(true)
                .agentGenerator(agentGenerator())
                .environmentGenerator(environmentGenerator())
                .build();
    }


    static class AlwaysTriggeredEnvironmentEvent extends EnvironmentEvent {
        private int runCount = 0;

        AlwaysTriggeredEnvironmentEvent(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected boolean isTriggered(EnvironmentContext context) {
            return true;
        }

        @Override
        protected void run(EnvironmentContext context) {
            runCount++;
        }

        int runCount() {
            return runCount;
        }
    }

    static class EmptyEnvironmentRoutine extends EnvironmentRoutine {
        EmptyEnvironmentRoutine(String name) {
            super(name, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected void run(EnvironmentContext context) {}
    }

    @SuppressWarnings("unchecked")
    static EnvironmentAttributeSet environmentAttributeSetFromAttributes(String ownerName, String attributeSetName, Attribute... attributes) {
        return new EnvironmentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(attributes)
        );
    }

    static void createContextFor(Environment environment) {
        Config config = syncedConfig(1, 1, 1);
        environment.createContext(
                new AgentSet(),
                config,
                new ContextCache(),
                new Clock(config.tickCount()),
                new RequestResponseController(config),
                environment,
                new SplittableRandom()
        );
    }
}
