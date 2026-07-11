package unit.modelarium.entities.contexts;

import helpers.TestFixtures;
import modelarium.Config;
import modelarium.entities.Entity;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.environments.Environment;
import modelarium.entities.immutable.ImmutableAgent;
import modelarium.entities.immutable.ImmutableAgentSet;
import modelarium.entities.immutable.ImmutableEntity;
import modelarium.entities.immutable.ImmutableEnvironment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.mockito.stubbing.Stubber;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;

class ContextTestHelpers {
    private ContextTestHelpers() {}

    static Agent getMutableFromImmutable(ImmutableAgent immutableAgent) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        return (Agent) getMutableEntityMethod.invoke(immutableAgent);
    }

    static Environment getMutableFromImmutable(ImmutableEnvironment immutableEnvironment) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getMutableEntityMethod = ImmutableEntity.class.getDeclaredMethod("getMutableEntity");
        getMutableEntityMethod.setAccessible(true);
        return (Environment) getMutableEntityMethod.invoke(immutableEnvironment);
    }

    static AgentSet getMutableFromImmutable(ImmutableAgentSet immutableAgentSet) throws NoSuchFieldException, IllegalAccessException {
        Field agentSetField = ImmutableAgentSet.class.getDeclaredField("agentSet");
        agentSetField.setAccessible(true);
        return (AgentSet) agentSetField.get(immutableAgentSet);
    }

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

    static <E extends Throwable> void assertCorrectExceptionThrown(
            Class<E> expectedException,
            Executable executableToCheck,
            String expectedMessage
    ) {
        assertCorrectExceptionThrown(expectedException, executableToCheck, expectedMessage, null);
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

    private static <C extends SimulationContext, T, E extends Entity<?,?,?,?>> C generateContextWithMockRequestResponseInterface(
            Class<C> contextClass,
            T returned,
            String methodName,
            Class<?>[] methodParameterTypes,
            Config config,
            E thisEntity,
            Function<T, Stubber> doMethod
    ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        Method method = RequestResponseInterface.class.getDeclaredMethod(methodName, methodParameterTypes);
        RequestResponseInterface mockRequestResponseInterface = mock(RequestResponseInterface.class);
        RequestResponseInterface stubbing = doMethod.apply(returned).when(mockRequestResponseInterface);

        Object[] args = new Object[methodParameterTypes.length];
        for (int i = 0; i < args.length; i++)
            args[i] = anyFor(methodParameterTypes[i]);
        method.invoke(stubbing, args);

        C context;

        if (contextClass.equals(AgentSimulationContext.class)) {
            Agent agent = (Agent) thisEntity;
            AgentSet agentSet = TestFixtures.agentSetOfSize(config.populationSize() - 1);
            agentSet.add(agent);
            context = (C) TestFixtures.agentSimulationContextWithAgent(
                    config,
                    agent,
                    agentSet
            );
        }
        else if (contextClass.equals(EnvironmentSimulationContext.class)) {
            context = TestFixtures.simulationContextWithEnvironment(
                    contextClass,
                    config,
                    (Environment) thisEntity
            );
        }
        else {
            throw new IllegalArgumentException("'" + contextClass.getName() + "' is not supported");
        }

        Field requestResponseInterfaceField = SimulationContext.class.getDeclaredField(
                "requestResponseInterface"
        );
        requestResponseInterfaceField.setAccessible(true);
        requestResponseInterfaceField.set(context, mockRequestResponseInterface);

        return context;
    }

    static <C extends SimulationContext, T, E extends Entity<?,?,?,?>> C generateContextWhereRequestResponseInterfaceMethodReturns(
            Class<C> contextClass,
            T returned,
            String methodName,
            Class<?>[] methodParameterTypes,
            Config config,
            E thisEntity
    ) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        return generateContextWithMockRequestResponseInterface(
                contextClass,
                returned,
                methodName,
                methodParameterTypes,
                config,
                thisEntity,
                Mockito::doReturn
        );
    }

    static <C extends SimulationContext, T extends Throwable, E extends Entity<?,?,?,?>> C generateContextWhereRequestResponseInterfaceMethodThrows(
            Class<C> contextClass,
            Class<T> exceptionClass,
            String methodName,
            Class<?>[] methodParameterTypes,
            Config config,
            E thisEntity
    ) throws IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        return generateContextWithMockRequestResponseInterface(
                contextClass,
                exceptionClass,
                methodName,
                methodParameterTypes,
                config,
                thisEntity,
                v -> Mockito.doThrow(mock((Class<Throwable>) v))
        );
    }

    static void assertSetsContainSameAgents(AgentSet expected, ImmutableAgentSet actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++)
            assertEquals(expected.get(i).name(), actual.get(i).name());
    }
}
