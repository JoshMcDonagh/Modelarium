package unit.modelarium.agents;

import modelarium.Entity;
import modelarium.agents.Agent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link Agent}.
 */
public class AgentTest {

    private AttributeSetCollection mockAttributeSetCollection;
    private Agent agent;

    @BeforeEach
    public void setUp() {
        mockAttributeSetCollection = mock(AttributeSetCollection.class);
        when(mockAttributeSetCollection.deepCopy()).thenReturn(mockAttributeSetCollection);

        agent = new Agent("Agent_1", mockAttributeSetCollection);
    }

    @Test
    public void testGetName_returnsCorrectName() {
        assertEquals("Agent_1", agent.name(), "Agent should return the correct name.");
    }

    @Test
    public void testGetAttributeSetCollection_returnsCorrectInstance() {
        assertSame(mockAttributeSetCollection, agent.getAttributeSetCollection(), "Should return the assigned attribute set collection.");
    }

    @Test
    public void testRun_invokesAttributeSetRun() {
        agent.run();
        verify(mockAttributeSetCollection, times(1)).run();
    }

    @Test
    public void testCloneDuplicate_createsEquivalentAgent() {
        AttributeSetCollection copiedAttributeSetCollection = mock(AttributeSetCollection.class);
        when(mockAttributeSetCollection.deepCopy()).thenReturn(copiedAttributeSetCollection);
        doNothing().when(copiedAttributeSetCollection).setAssociatedModelElement(any(Entity.class));
        when(copiedAttributeSetCollection.deepCopy()).thenReturn((copiedAttributeSetCollection));

        Agent copy = agent.clone();

        assertNotNull(copy, "Deep copy should not be null.");
        assertEquals(agent.name(), copy.name(), "Copied agent should retain the same name.");
        assertNotSame(agent, copy, "Copy should be a different instance.");
        assertNotSame(agent.getAttributeSetCollection(), copy.getAttributeSetCollection(), "Attribute set collection should be a deep copy.");
    }
}
