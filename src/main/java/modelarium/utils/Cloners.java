package modelarium.utils;

import com.rits.cloning.Cloner;
import com.rits.cloning.IFastCloner;
import modelarium.entities.attributes.events.functional.*;
import modelarium.entities.attributes.properties.functional.*;
import modelarium.entities.attributes.routines.functional.*;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.logging.AttributeSetLog;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class Cloners {
    private static final Cloner INSTANCE = configure();

    private Cloners() {}

    public static Cloner standard() {
        return INSTANCE;
    }

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

    private static void registerIfPresent(Cloner cloner, String className, IFastCloner fastCloner) {
        try {
            cloner.registerFastCloner(Class.forName(className), fastCloner);
        } catch (ClassNotFoundException ignored) {
            // JDK internal names shifted — fall through to reflective cloning
        }
    }
}