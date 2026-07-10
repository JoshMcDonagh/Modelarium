package unit.modelarium.entities.contexts;

import helpers.TestFixtures;
import modelarium.Config;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;
import modelarium.entities.immutable.ImmutableEntity;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.Mockito.*;

public class ContextTestHelpers {
    public static Agent getMutableAgentFromImmutable(ImmutableAgent immutableAgent) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        return (Agent) getMutableEntityMethod.invoke(immutableAgent);
    }

    public static AgentSet getMutableAgentSetFromImmutable(ImmutableAgentSet immutableAgentSet) throws NoSuchFieldException, IllegalAccessException {
        Field agentSetField = ImmutableAgentSet.class.getDeclaredField("agentSet");
        agentSetField.setAccessible(true);
        return (AgentSet) agentSetField.get(immutableAgentSet);
    }

    public static <E extends Throwable, C extends Throwable> void assertCorrectExceptionThrown(
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

    public static <E extends Throwable> void assertCorrectExceptionThrown(
            Class<E> expectedException,
            Executable executableToCheck,
            String expectedMessage
    ) {
        assertCorrectExceptionThrown(expectedException, executableToCheck, expectedMessage, null);
    }

    public static void setContextsRequestResponseInterfaceField(
            SimulationContext context,
            RequestResponseInterface requestResponseInterface
    ) throws NoSuchFieldException, IllegalAccessException {
        Field requestResponseInterfaceField = SimulationContext.class.getDeclaredField(
                "requestResponseInterface"
        );
        requestResponseInterfaceField.setAccessible(true);
        requestResponseInterfaceField.set(context, requestResponseInterface);
    }

    private static Object anyFor(Class<?> parameterType) {
        if (!parameterType.isPrimitive())
            return any();
        if (parameterType == int.class)
            return anyInt();
        if (parameterType == long.class)
            return anyLong();
        if (parameterType == double.class)
            return anyDouble();
        if (parameterType == float.class)
            return anyFloat();
        if (parameterType == boolean.class)
            return anyBoolean();
        if (parameterType == byte.class)
            return anyByte();
        if (parameterType == short.class)
            return anyShort();
        if (parameterType == char.class)
            return anyChar();
        throw new IllegalArgumentException("Unhandled primitive parameter type: " + parameterType);
    }

    public static <C extends SimulationContext, T> C generateContextWhereRequestResponseInterfaceMethodReturns(
            Class<C> simulationContextClass,
            T returned,
            String methodName,
            Class<?>[] methodParameterTypes,
            Config config,
            Environment environment
    ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        Method method = RequestResponseInterface.class.getDeclaredMethod(methodName, methodParameterTypes);
        RequestResponseInterface mockRequestResponseInterface = mock(RequestResponseInterface.class);
        RequestResponseInterface stubbing = doReturn(returned).when(mockRequestResponseInterface);

        Object[] args = new Object[methodParameterTypes.length];
        for (int i = 0; i < args.length; i++)
            args[i] = anyFor(methodParameterTypes[i]);
        method.invoke(stubbing, args);

        C context = TestFixtures.simulationContextWithEnvironment(
                simulationContextClass,
                config,
                environment
        );

        setContextsRequestResponseInterfaceField(context, mockRequestResponseInterface);

        return context;
    }

    public static <C extends SimulationContext, E extends Throwable> C generateContextWhereRequestResponseInterfaceMethodThrows(
            Class<C> simulationContextClass,
            Class<E> exceptionClass,
            String methodName,
            Class<?>[] methodParameterTypes,
            Config config,
            Environment environment
    ) throws IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Method method = RequestResponseInterface.class.getDeclaredMethod(methodName, methodParameterTypes);
        RequestResponseInterface mockRequestResponseInterface = mock(RequestResponseInterface.class);
        RequestResponseInterface stubbing = doThrow(mock(exceptionClass)).when(mockRequestResponseInterface);

        Object[] args = new Object[methodParameterTypes.length];
        for (int i = 0; i < args.length; i++)
            args[i] = anyFor(methodParameterTypes[i]);
        method.invoke(stubbing, args);

        C context = TestFixtures.simulationContextWithEnvironment(
                simulationContextClass,
                config,
                environment
        );

        setContextsRequestResponseInterfaceField(context, mockRequestResponseInterface);

        return context;
    }


}
