package modelarium.utils;

import com.rits.cloning.Cloner;
import com.rits.cloning.IFastCloner;
import modelarium.entities.attributes.events.functional.AgentEventIsTriggeredFunction;
import modelarium.entities.attributes.events.functional.AgentEventRunFunction;
import modelarium.entities.attributes.events.functional.EnvironmentEventIsTriggeredFunction;
import modelarium.entities.attributes.events.functional.EnvironmentEventRunFunction;
import modelarium.entities.attributes.properties.functional.*;
import modelarium.entities.attributes.routines.functional.AgentRoutineRunFunction;
import modelarium.entities.attributes.routines.functional.EnvironmentRoutineRunFunction;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for providing a shared, pre-configured {@link Cloner} instance for deep cloning model elements.
 *
 * <p>This class configures the cloner so that entity contexts and attribute logs are nulled rather than cloned,
 * functional attribute lambdas are shared rather than cloned, and the JDK's immutable collections are cloned via
 * registered fast cloners.
 */
public final class Cloners {

    /** The shared cloner instance used throughout the framework */
    private static final Cloner INSTANCE = configure();

    private Cloners() {}

    /**
     * Returns the shared, pre-configured cloner instance.
     *
     * @return the framework's standard {@link Cloner} instance
     */
    public static Cloner standard() {
        return INSTANCE;
    }

    /**
     * Creates and configures the cloner instance the framework will share.
     *
     * @return a new {@link Cloner} instance configured for cloning model elements
     */
    private static Cloner configure() {
        Cloner cloner = new Cloner();

        cloner.nullInsteadOfClone(
                AgentSimulationContext.class,
                EnvironmentSimulationContext.class,
                AttributeSetLog.class
        );

        cloner.dontCloneInstanceOf(
                AgentPropertyGetterFunction.class, AgentPropertySetterFunction.class,
                AgentPropertyRunFunction.class,
                EnvironmentPropertyGetterFunction.class, EnvironmentPropertySetterFunction.class,
                EnvironmentPropertyRunFunction.class,
                AgentEventIsTriggeredFunction.class, AgentEventRunFunction.class,
                EnvironmentEventIsTriggeredFunction.class, EnvironmentEventRunFunction.class,
                AgentRoutineRunFunction.class, EnvironmentRoutineRunFunction.class
        );

        IFastCloner listCloner = (o, dc, clones) ->
                ((List<?>) o).stream().map(e -> dc.deepClone(e, clones)).toList();
        IFastCloner setCloner = (o, dc, clones) ->
                ((Set<?>) o).stream().map(e -> dc.deepClone(e, clones))
                        .collect(Collectors.toUnmodifiableSet());
        IFastCloner mapCloner = (o, dc, clones) ->
                ((Map<?, ?>) o).entrySet().stream().collect(Collectors.toUnmodifiableMap(
                        e -> dc.deepClone(e.getKey(), clones),
                        e -> dc.deepClone(e.getValue(), clones)));

        registerIfPresent(cloner, "java.util.ImmutableCollections$List12", listCloner);
        registerIfPresent(cloner, "java.util.ImmutableCollections$ListN", listCloner);
        registerIfPresent(cloner, "java.util.ImmutableCollections$Set12", setCloner);
        registerIfPresent(cloner, "java.util.ImmutableCollections$SetN", setCloner);
        registerIfPresent(cloner, "java.util.ImmutableCollections$Map1", mapCloner);
        registerIfPresent(cloner, "java.util.ImmutableCollections$MapN", mapCloner);

        return cloner;
    }

    /**
     * Registers a fast cloner for the named class if that class is present in the running JDK.
     *
     * @param cloner the cloner to register the fast cloner with
     * @param className the fully qualified name of the class the fast cloner handles
     * @param fastCloner the fast cloner to register
     */
    private static void registerIfPresent(Cloner cloner, String className, IFastCloner fastCloner) {
        try {
            cloner.registerFastCloner(Class.forName(className), fastCloner);
        } catch (ClassNotFoundException ignored) {
            // JDK internal names shifted — fall through to reflective cloning
        }
    }
}
