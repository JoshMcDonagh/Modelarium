package helpers;

import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.attributes.events.AgentEvent;
import modelarium.entities.attributes.events.EnvironmentEvent;
import modelarium.entities.attributes.properties.AgentProperty;
import modelarium.entities.attributes.properties.EnvironmentProperty;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.attributes.routines.EnvironmentRoutine;
import modelarium.entities.contexts.AgentContext;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.EnvironmentContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;

import java.util.List;

/**
 * Concrete test implementations of the sealed attribute hierarchy.
 *
 * <p>Provides minimal Property, Event, and Routine implementations for use
 * across unit and integration tests without pulling in the functional variants.
 */
public final class TestAttributes {

    private TestAttributes() {}

    // ---- Agent property: stores a double and increments each tick ----

    public static class AgentCounterProperty extends AgentProperty<Double> {
        private double value;

        public AgentCounterProperty(String name, double initialValue) {
            super(name, true, AttributeAccessLevel.PUBLIC, Double.class);
            this.value = initialValue;
        }

        public AgentCounterProperty(String name) {
            this(name, 0.0);
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

        // Direct access for assertions (bypasses context requirement)
        public double rawGet() {
            return value;
        }
    }

    // ---- Agent property with PRIVATE access ----

    public static class PrivateCounterProperty extends AgentProperty<Double> {
        private double value;

        public PrivateCounterProperty(String name, double initialValue) {
            super(name, true, AttributeAccessLevel.PRIVATE, Double.class);
            this.value = initialValue;
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

        public double rawGet() {
            return value;
        }
    }

    // ---- Agent event: triggers when a counter exceeds a threshold ----

    public static class ThresholdEvent extends AgentEvent {
        private final double threshold;
        private boolean lastTriggered = false;
        private int runCount = 0;

        public ThresholdEvent(String name, double threshold) {
            super(name, true, AttributeAccessLevel.PUBLIC);
            this.threshold = threshold;
        }

        @Override
        protected boolean isTriggered(AgentContext context) {
            // Always trigger for simplicity in most tests
            lastTriggered = true;
            return true;
        }

        @Override
        protected void run(AgentContext context) {
            runCount++;
        }

        public boolean wasTriggered() {
            return lastTriggered;
        }

        public int getRunCount() {
            return runCount;
        }
    }

    // ---- Agent event that never triggers ----

    public static class NeverTriggeredEvent extends AgentEvent {
        public NeverTriggeredEvent(String name) {
            super(name, true, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected boolean isTriggered(AgentContext context) {
            return false;
        }

        @Override
        protected void run(AgentContext context) {
            throw new AssertionError("Should never run — event is not triggered");
        }
    }

    // ---- Agent routine: just counts invocations ----

    public static class InvocationCountingRoutine extends AgentRoutine {
        private int invocations = 0;

        public InvocationCountingRoutine(String name) {
            super(name, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected void run(AgentContext context) {
            invocations++;
        }

        public int getInvocations() {
            return invocations;
        }
    }

    // ---- Environment property: stores a tick counter ----

    public static class EnvironmentTickProperty extends EnvironmentProperty<Integer> {
        private int tick = 0;

        public EnvironmentTickProperty(String name) {
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

        public int rawGet() {
            return tick;
        }
    }

    // ---- Environment event: always triggered ----

    public static class AlwaysTriggeredEnvironmentEvent extends EnvironmentEvent {
        private int runCount = 0;

        public AlwaysTriggeredEnvironmentEvent(String name) {
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

        public int getRunCount() {
            return runCount;
        }
    }

    // ---- Environment routine ----

    public static class EnvironmentCountingRoutine extends EnvironmentRoutine {
        private int invocations = 0;

        public EnvironmentCountingRoutine(String name) {
            super(name, AttributeAccessLevel.PUBLIC);
        }

        @Override
        protected void run(EnvironmentContext context) {
            invocations++;
        }

        public int getInvocations() {
            return invocations;
        }
    }

    // ---- Factory methods for building attribute sets ----

    @SuppressWarnings("unchecked")
    public static AgentAttributeSet singlePropertyAgentSet(String ownerName, String setName, String propertyName) {
        AgentCounterProperty property = new AgentCounterProperty(propertyName);
        return new AgentAttributeSet(ownerName, setName,
                (List<Attribute<AgentSimulationContext>>) (List<?>) List.of(property));
    }

    @SuppressWarnings("unchecked")
    public static EnvironmentAttributeSet singlePropertyEnvironmentSet(String ownerName, String setName, String propertyName) {
        EnvironmentTickProperty property = new EnvironmentTickProperty(propertyName);
        return new EnvironmentAttributeSet(
                ownerName,
                setName,
                (List<Attribute<EnvironmentSimulationContext>>) (List<?>) List.of(property)
        );
    };

    @SuppressWarnings("unchecked")
    public static AgentAttributeSet agentAttributeSet(String ownerName, String setName, AgentProperty<?>... properties) {
        return new AgentAttributeSet(ownerName, setName,
                (List<Attribute<AgentSimulationContext>>) (List<?>) List.of(properties));
    }

    @SuppressWarnings("unchecked")
    public static AgentAttributeSet agentAttributeSetFromEvents(String ownerName, String setName, AgentEvent... events) {
        return new AgentAttributeSet(ownerName, setName,
                (List<Attribute<AgentSimulationContext>>) (List<?>) List.of(events));
    }

    @SuppressWarnings("unchecked")
    public static AgentAttributeSet agentAttributeSetFromRoutines(String ownerName, String setName, AgentRoutine... routines) {
        return new AgentAttributeSet(ownerName, setName,
                (List<Attribute<AgentSimulationContext>>) (List<?>) List.of(routines));
    }

    @SuppressWarnings("unchecked")
    public static EnvironmentAttributeSet environmentAttributeSet(String ownerName, String setName, EnvironmentProperty<?>... properties) {
        return new EnvironmentAttributeSet(ownerName, setName,
                (List<Attribute<EnvironmentSimulationContext>>) (List<?>) List.of(properties));
    }

    @SuppressWarnings("unchecked")
    public static EnvironmentAttributeSet emptyEnvironmentAttributeSet(String ownerName, String setName) {
        return new EnvironmentAttributeSet(ownerName, setName, List.of());
    }
}
