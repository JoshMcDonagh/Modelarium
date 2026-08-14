package unit.modelarium.entities.environments;

import modelarium.Config;
import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.sets.mutable.MutableEnvironmentAttributeSet;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.environments.MutableEnvironment;
import modelarium.entities.environments.generators.EnvironmentGenerator;

import java.util.List;
import modelarium.clock.MutableClock;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.ContextCache;
import modelarium.multithreading.requestresponse.RequestResponseController;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

class EnvironmentTestHelpers {
    private EnvironmentTestHelpers() {}

    static MutableEnvironment emptyEnvironment() {
        return new MutableEnvironment("env", List.of());
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
    static MutableEnvironmentAttributeSet environmentAttributeSet(String ownerName, String attributeSetName, EnvironmentProperty<?>... properties) {
        return new MutableEnvironmentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(properties)
        );
    }

    static MutableEnvironmentAttributeSet emptyEnvironmentAttributeSet(String ownerName, String attributeSetName) {
        return new MutableEnvironmentAttributeSet(attributeSetName, List.of());
    }

    static DefaultAgentGenerator agentGenerator() {
        return new DefaultAgentGenerator() {
            private int index = 0;

            @Override
            protected MutableAgent generateAgent(Config config, RandomGenerator random) {
                return new MutableAgent("agent_" + index++, List.of());
            }
        };
    }

    static EnvironmentGenerator environmentGenerator() {
        return new EnvironmentGenerator() {
            @Override
            public MutableEnvironment generateEnvironment(Config config, RandomGenerator random) {
                return emptyEnvironment();
            }
        };
    }

    static Config syncedConfig(int populationSize, int tickCount, int threadCount) {
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
    static MutableEnvironmentAttributeSet environmentAttributeSetFromAttributes(String ownerName, String attributeSetName, Attribute... attributes) {
        return new MutableEnvironmentAttributeSet(
                attributeSetName,
                (List<Attribute>) (List<?>) List.of(attributes)
        );
    }

    static void createContextFor(MutableEnvironment environment) {
        Config config = syncedConfig(1, 1, 1);
        environment.createContext(
                new MutableAgentSet(),
                config,
                new ContextCache(),
                new MutableClock(config.tickCount()),
                new RequestResponseController(config),
                environment,
                new SplittableRandom()
        );
    }
}
