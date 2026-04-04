package unit.modelarium;

import modelarium.entities.Entity;
import modelarium.entities.contexts.Context;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.sets.AgentSet;
import modelarium.entities.environments.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link Entity} abstract class via a dummy subclass.
 */
public class EntityTest {

    private static class DummyEntity extends Entity {
        private boolean wasRunCalled = false;

        public DummyEntity(String name, AttributeSetCollection attributeSetCollection) {
            super(name, attributeSetCollection);
        }

        @Override
        public void run() {
            wasRunCalled = true;
        }

        public boolean wasRunCalled() {
            return wasRunCalled;
        }

        @Override
        public Entity clone() {
            return null;
        }

        public Agent callAccessExternalAgentByName(String targetAgentName) {
            return accessExternalAgentByName(targetAgentName);
        }

        public AgentSet callAccessExternalAgentsByFilter(Predicate<Agent> filter) {
            return accessExternalAgentsByFilter(filter);
        }

        public Environment callAccessEnvironment() {
            return accessEnvironment();
        }
    }

    private AttributeSetCollection attributeSetCollection;
    private DummyEntity dummyElement;

    @BeforeEach
    public void setup() {
        attributeSetCollection = mock(AttributeSetCollection.class);
        when(attributeSetCollection.deepCopy()).thenReturn(attributeSetCollection);
        dummyElement = new DummyEntity("TestElement", attributeSetCollection);
    }

    @Test
    public void testNameIsStoredCorrectly() {
        assertEquals("TestElement", dummyElement.name(), "Model element name should match the constructor input.");
    }

    @Test
    public void testAttributeSetCollectionIsReturned() {
        assertSame(attributeSetCollection, dummyElement.getAttributeSetCollection(),
                "Attribute set collection should be the same as passed in.");
    }

    @Test
    public void testSetupInitialisesAttributeCollection() {
        AttributeSetCollection mockAttributeSetCollection = mock(AttributeSetCollection.class);
        when(mockAttributeSetCollection.deepCopy()).thenReturn(mockAttributeSetCollection);

        DummyEntity dummy = new DummyEntity("TestElement", mockAttributeSetCollection);
        dummy.setup();

        verify(mockAttributeSetCollection).setup("TestElement");
    }

    @Test
    public void testRunMethodCanBeInvoked() {
        dummyElement.run();
        assertTrue(dummyElement.wasRunCalled(), "Run method should change internal flag when invoked.");
    }

    @Test
    public void testModelElementAccessorCanBeSetAndRetrieved() {
        Context mockAccessor = mock(Context.class);
        dummyElement.setModelElementAccessor(mockAccessor);
        assertSame(mockAccessor, dummyElement.getModelElementAccessor());
    }

    @Test
    public void testAccessExternalAgentByNameDelegatesToAccessor() {
        Context accessor = mock(Context.class);
        Agent agent = mock(Agent.class);

        dummyElement.setModelElementAccessor(accessor);
        when(accessor.getAgent("A")).thenReturn(agent);

        Agent result = dummyElement.callAccessExternalAgentByName("A");

        assertSame(agent, result);
        verify(accessor).getAgent("A");
    }

    @Test
    public void testAccessExternalAgentsByFilterDelegatesToAccessor() {
        Context accessor = mock(Context.class);
        AgentSet agentSet = mock(AgentSet.class);

        dummyElement.setModelElementAccessor(accessor);

        Predicate<Agent> filter = a -> true; // any predicate object is fine
        when(accessor.getFilteredAgents(filter)).thenReturn(agentSet);

        AgentSet result = dummyElement.callAccessExternalAgentsByFilter(filter);

        assertSame(agentSet, result);
        verify(accessor).getFilteredAgents(filter);
    }

    @Test
    public void testAccessEnvironmentDelegatesToAccessor() {
        Context accessor = mock(Context.class);
        Environment env = mock(Environment.class);

        dummyElement.setModelElementAccessor(accessor);
        when(accessor.getEnvironment()).thenReturn(env);

        Environment result = dummyElement.callAccessEnvironment();

        assertSame(env, result);
        verify(accessor).getEnvironment();
    }

    @Test
    public void testAccessExternalAgentByNameThrowsIfAccessorNotSet() {
        assertThrows(NullPointerException.class,
                () -> dummyElement.callAccessExternalAgentByName("A"));
    }

    @Test
    public void testAccessExternalAgentsByFilterThrowsIfAccessorNotSet() {
        assertThrows(NullPointerException.class,
                () -> dummyElement.callAccessExternalAgentsByFilter(a -> true));
    }

    @Test
    public void testAccessEnvironmentThrowsIfAccessorNotSet() {
        assertThrows(NullPointerException.class,
                () -> dummyElement.callAccessEnvironment());
    }

}
