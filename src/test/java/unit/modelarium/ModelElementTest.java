package unit.modelarium;

import modelarium.ModelElement;
import modelarium.ModelElementAccessor;
import modelarium.agents.Agent;
import modelarium.agents.AgentSet;
import modelarium.attributes.AttributeSetCollection;
import modelarium.environments.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link ModelElement} abstract class via a dummy subclass.
 */
public class ModelElementTest {

    private static class DummyModelElement extends ModelElement {
        private boolean wasRunCalled = false;

        public DummyModelElement(String name, AttributeSetCollection attributeSetCollection) {
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
        public ModelElement deepCopy() {
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
    private DummyModelElement dummyElement;

    @BeforeEach
    public void setup() {
        attributeSetCollection = mock(AttributeSetCollection.class);
        when(attributeSetCollection.deepCopy()).thenReturn(attributeSetCollection);
        dummyElement = new DummyModelElement("TestElement", attributeSetCollection);
    }

    @Test
    public void testNameIsStoredCorrectly() {
        assertEquals("TestElement", dummyElement.getName(), "Model element name should match the constructor input.");
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

        DummyModelElement dummy = new DummyModelElement("TestElement", mockAttributeSetCollection);
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
        ModelElementAccessor mockAccessor = mock(ModelElementAccessor.class);
        dummyElement.setModelElementAccessor(mockAccessor);
        assertSame(mockAccessor, dummyElement.getModelElementAccessor());
    }

    @Test
    public void testAccessExternalAgentByNameDelegatesToAccessor() {
        ModelElementAccessor accessor = mock(ModelElementAccessor.class);
        Agent agent = mock(Agent.class);

        dummyElement.setModelElementAccessor(accessor);
        when(accessor.getAgentByName("A")).thenReturn(agent);

        Agent result = dummyElement.callAccessExternalAgentByName("A");

        assertSame(agent, result);
        verify(accessor).getAgentByName("A");
    }

    @Test
    public void testAccessExternalAgentsByFilterDelegatesToAccessor() {
        ModelElementAccessor accessor = mock(ModelElementAccessor.class);
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
        ModelElementAccessor accessor = mock(ModelElementAccessor.class);
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
