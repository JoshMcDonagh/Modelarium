package unit.modelarium.entities.attributes.sets.immutable;

import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.events.Event;
import modelarium.entities.attributes.events.functional.*;
import modelarium.entities.attributes.properties.Property;
import modelarium.entities.attributes.properties.functional.*;
import modelarium.entities.attributes.routines.Routine;
import modelarium.entities.attributes.routines.functional.AgentRoutineRunFunction;
import modelarium.entities.attributes.routines.functional.EnvironmentRoutineRunFunction;
import modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine;
import modelarium.entities.attributes.routines.functional.FunctionalEnvironmentRoutine;
import modelarium.entities.attributes.sets.immutable.ReadOnlyAgentAttributeSet;
import modelarium.entities.attributes.sets.immutable.ReadOnlyAttributeSet;
import modelarium.entities.attributes.sets.immutable.ReadOnlyEnvironmentAttributeSet;
import modelarium.entities.attributes.sets.mutable.AgentAttributeSet;
import modelarium.entities.attributes.sets.mutable.AttributeSet;
import modelarium.entities.attributes.sets.mutable.EnvironmentAttributeSet;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.SimulationContext;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReadOnlyAttributeSetTestHelpers {
    private ReadOnlyAttributeSetTestHelpers() {}

    static <T extends AttributeSet<?,?>> T makeAttributeSet(
            Class<T> attributeSetClass,
            String attributeSetName,
            List<Attribute> attributes
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return attributeSetClass.getConstructor(
                String.class,
                List.class
        ).newInstance(
                attributeSetName,
                attributes
        );
    }

    static <T extends ReadOnlyAttributeSet<?,?>> T makeImmutableAttributeSet(
            Class<T> attributeSetClass,
            String attributeSetName,
            List<Attribute> attributes
    ) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (attributeSetClass.equals(ReadOnlyAgentAttributeSet.class)) {
            return (T) new ReadOnlyAgentAttributeSet(makeAttributeSet(
                    AgentAttributeSet.class,
                    attributeSetName,
                    attributes
            ));
        }
        else if (attributeSetClass.equals(ReadOnlyEnvironmentAttributeSet.class)) {
            return (T) new ReadOnlyEnvironmentAttributeSet(makeAttributeSet(
                    EnvironmentAttributeSet.class,
                    attributeSetName,
                    attributes
            ));
        }
        else {
            throw new IllegalArgumentException("'" + attributeSetClass.getName() + "' is not supported");
        }
    }

    private static <SC extends SimulationContext, C extends Context> AttributeSet<SC, C> getMutableAttributeSetFromImmutableAttributeSet(
            ReadOnlyAttributeSet<SC, C> immutableAttributeSet
    ) throws NoSuchFieldException, IllegalAccessException {
        Field attributeSetField = ReadOnlyAttributeSet.class.getDeclaredField("attributeSet");
        attributeSetField.setAccessible(true);
        return (AttributeSet<SC, C>) attributeSetField.get(immutableAttributeSet);
    }

    static AgentAttributeSet getMutableFromImmutable(
            ReadOnlyAgentAttributeSet immutableAttributeSet
    ) throws NoSuchFieldException, IllegalAccessException {
        return (AgentAttributeSet) getMutableAttributeSetFromImmutableAttributeSet(immutableAttributeSet);
    }

    static EnvironmentAttributeSet getMutableFromImmutable(
            ReadOnlyEnvironmentAttributeSet immutableAttributeSet
    ) throws NoSuchFieldException, IllegalAccessException {
        return (EnvironmentAttributeSet) getMutableAttributeSetFromImmutableAttributeSet(immutableAttributeSet);
    }

    static <A,T,P> T runGetClonedAttribute(
            ReadOnlyAttributeSet<?,?> immutableAttributeSet,
            Class<A> attributeSetClass,
            Class<T> attributeReturnClass,
            String getterMethodName,
            Class<P> attributeIdClass,
            P attributeId
    ) throws Throwable {
        Method getClonedAttributeMethod = ReadOnlyAttributeSet.class.getDeclaredMethod(
                "getClonedAttribute",
                Class.class,
                Class.class,
                String.class,
                Class.class,
                Object.class
        );

        getClonedAttributeMethod.setAccessible(true);

        try {
            return (T) getClonedAttributeMethod.invoke(
                    immutableAttributeSet,
                    attributeSetClass,
                    attributeReturnClass,
                    getterMethodName,
                    attributeIdClass,
                    attributeId
            );
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw e.getCause();
        }
    };

    static <E extends Throwable, C extends Throwable> void assertCorrectExceptionThrown(
            Class<E> expectedException,
            Executable executableToCheck,
            String expectedMessage,
            Class<C> causingException
    ) {
        E exception = assertThrows(expectedException, executableToCheck);
        assertEquals(expectedMessage, exception.getMessage());

        if (causingException != null)
            assertInstanceOf(causingException, exception.getCause());
    }

    private static AgentEventRunFunction makeEmptyAgentEventRunFunction() {
        return (context) -> {};
    }

    private static AgentEventIsTriggeredFunction makeEmptyAgentEventIsTriggeredFunction() {
        return (context) -> true;
    }

    private static EnvironmentEventRunFunction makeEmptyEnvironmentEventRunFunction() {
        return (context) -> {};
    }

    private static EnvironmentEventIsTriggeredFunction makeEmptyEnvironmentEventIsTriggeredFunction() {
        return (context) -> true;
    }

    static <T extends Event<?>> T makeEmptyFunctionalEvent(
            Class<T> eventClass,
            String eventName
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> eventRunFunctionClass;
        Class<?> eventIsTriggeredFunctionClass;

        Object eventRunFunction;
        Object eventIsTriggeredFunction;

        if (eventClass.equals(FunctionalAgentEvent.class)) {
            eventRunFunctionClass = AgentEventRunFunction.class;
            eventRunFunction = makeEmptyAgentEventRunFunction();
            eventIsTriggeredFunctionClass = AgentEventIsTriggeredFunction.class;
            eventIsTriggeredFunction = makeEmptyAgentEventIsTriggeredFunction();
        }
        else if (eventClass.equals(FunctionalEnvironmentEvent.class)) {
            eventRunFunctionClass = EnvironmentEventRunFunction.class;
            eventRunFunction = makeEmptyEnvironmentEventRunFunction();
            eventIsTriggeredFunctionClass = EnvironmentEventIsTriggeredFunction.class;
            eventIsTriggeredFunction = makeEmptyEnvironmentEventIsTriggeredFunction();
        }
        else {
            throw new IllegalArgumentException("'" + eventClass.getName() + "' is not supported");
        }

        return eventClass.getConstructor(
                String.class,
                boolean.class,
                AttributeAccessLevel.class,
                eventRunFunctionClass,
                eventIsTriggeredFunctionClass
        ).newInstance(
                eventName,
                true,
                AttributeAccessLevel.PUBLIC,
                eventRunFunction,
                eventIsTriggeredFunction
        );
    }

    private static AgentPropertyRunFunction<Integer> makeEmptyAgentPropertyRunFunction() {
        return (context, val) -> val;
    }

    private static AgentPropertySetterFunction<Integer> makeEmptyAgentPropertySetterFunction() {
        return (context, currentVal, newVal) -> currentVal + newVal;
    }

    private static AgentPropertyGetterFunction<Integer> makeEmptyAgentPropertyGetterFunction() {
        return (context, val) -> val;
    }

    private static EnvironmentPropertyRunFunction<Integer> makeEmptyEnvironmentPropertyRunFunction() {
        return (context, val) -> val;
    }

    private static EnvironmentPropertySetterFunction<Integer> makeEmptyEnvironmentPropertySetterFunction() {
        return (context, currentVal, newVal) -> currentVal + newVal;
    }

    private static EnvironmentPropertyGetterFunction<Integer> makeEmptyEnvironmentPropertyGetterFunction() {
        return (context, val) -> val;
    }

    static <T extends Property<?, ?>> T makeEmptyFunctionalProperty(
            Class<T> propertyClass,
            String propertyName
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> propertyRunFunctionClass;
        Class<?> propertySetterFunctionClass;
        Class<?> propertyGetterFunctionClass;

        Object propertyRunFunction;
        Object propertySetterFunction;
        Object propertyGetterFunction;

        if (propertyClass.equals(FunctionalAgentProperty.class)) {
            propertyRunFunctionClass = AgentPropertyRunFunction.class;
            propertyRunFunction = makeEmptyAgentPropertyRunFunction();
            propertySetterFunctionClass = AgentPropertySetterFunction.class;
            propertySetterFunction = makeEmptyAgentPropertySetterFunction();
            propertyGetterFunctionClass = AgentPropertyGetterFunction.class;
            propertyGetterFunction = makeEmptyAgentPropertyGetterFunction();
        }
        else if (propertyClass.equals(FunctionalEnvironmentProperty.class)) {
            propertyRunFunctionClass = EnvironmentPropertyRunFunction.class;
            propertyRunFunction = makeEmptyEnvironmentPropertyRunFunction();
            propertySetterFunctionClass = EnvironmentPropertySetterFunction.class;
            propertySetterFunction = makeEmptyEnvironmentPropertySetterFunction();
            propertyGetterFunctionClass = EnvironmentPropertyGetterFunction.class;
            propertyGetterFunction = makeEmptyEnvironmentPropertyGetterFunction();
        }
        else {
            throw new IllegalArgumentException("'" + propertyClass.getName() + "' is not supported");
        }

        return propertyClass.getConstructor(
                String.class,
                boolean.class,
                AttributeAccessLevel.class,
                Class.class,
                propertyGetterFunctionClass,
                propertySetterFunctionClass,
                propertyRunFunctionClass
        ).newInstance(
                propertyName,
                true,
                AttributeAccessLevel.PUBLIC,
                int.class,
                propertyGetterFunction,
                propertySetterFunction,
                propertyRunFunction
        );
    }

    private static AgentRoutineRunFunction makeEmptyAgentRoutineRunFunction() {
        return (context) -> {};
    }

    private static EnvironmentRoutineRunFunction makeEmptyEnvironmentRoutineRunFunction() {
        return (context) -> {};
    }

    static <T extends Routine<?>> T makeEmptyFunctionalRoutine(
            Class<T> routineClass,
            String routineName
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> routineRunFunctionClass;
        Object routineRunFunction;

        if (routineClass.equals(FunctionalAgentRoutine.class)) {
            routineRunFunctionClass = AgentRoutineRunFunction.class;
            routineRunFunction = makeEmptyAgentRoutineRunFunction();
        }
        else if (routineClass.equals(FunctionalEnvironmentRoutine.class)) {
            routineRunFunctionClass = EnvironmentRoutineRunFunction.class;
            routineRunFunction = makeEmptyEnvironmentRoutineRunFunction();
        }
        else {
            throw new IllegalArgumentException("'" + routineClass.getName() + "' is not supported");
        }

        return routineClass.getConstructor(
                String.class,
                AttributeAccessLevel.class,
                routineRunFunctionClass
        ).newInstance(
                routineName,
                AttributeAccessLevel.PUBLIC,
                routineRunFunction
        );
    }
}
