package unit.modelarium;

import modelarium.Entity;
import modelarium.contexts.Context;
import modelarium.Config;
import modelarium.agents.Agent;
import modelarium.agents.sets.AgentSet;
import modelarium.environments.Environment;
import modelarium.multithreading.requestresponse.RequestResponseInterface;
import modelarium.contexts.ContextCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link Context}.
 */
public class ContextTest {

    private Agent mockAgent;
    private Environment mockEnvironment;
    private AgentSet localAgentSet;
    private Config settings;
    private ContextCache cache;
    private RequestResponseInterface requestInterface;
    private Context accessor;

    @BeforeEach
    public void setup() {
        mockAgent = mock(Agent.class);
        mockEnvironment = mock(Environment.class);
        when(mockAgent.name()).thenReturn("Agent_X");

        localAgentSet = new AgentSet();
        settings = new Config();
        settings.setIsCacheUsed(true);
        cache = new ContextCache(true);
        requestInterface = mock(RequestResponseInterface.class);

        Entity entity = mock(Entity.class);
        AttributeSetCollection mockAttrSetCollection = mock(AttributeSetCollection.class);
        AttributeSetCollectionResults mockResults = mock(AttributeSetCollectionResults.class);

        when(entity.name()).thenReturn("Agent_X");
        when(entity.getAttributeSetCollection()).thenReturn(mockAttrSetCollection);
        when(mockAttrSetCollection.getResults()).thenReturn(mockResults);
        when(mockResults.getModelElementName()).thenReturn("Agent_X");

        accessor = new Context(
                entity,
                localAgentSet,
                settings,
                cache,
                requestInterface,
                mockEnvironment
        );
    }


    @Test
    public void testDoesAgentExistInThisCore_returnsTrue() {
        localAgentSet.add(mockAgent);
        assertTrue(accessor.doesAgentExistInThisCore("Agent_X"), "Should detect the agent exists in the local thread.");
    }

    @Test
    public void testGetAgent_returnsFromLocal() {
        localAgentSet.add(mockAgent);
        Agent agent = accessor.getAgent("Agent_X");
        assertNotNull(agent, "Should return the local agent.");
        assertEquals("Agent_X", agent.name());
    }

    @Test
    public void testGetAgent_returnsFromCache() {
        settings.setIsCacheUsed(true);
        settings.setAreProcessesSynced(false); // optional; cache access doesn't need this
        Agent agent = new Agent("Agent_X", new AttributeSetCollection());
        cache.addAgent(agent);
        Agent result = accessor.getAgent("Agent_X");
        assertNotNull(result, "Should return the agent from cache.");
        assertEquals("Agent_X", result.name());
    }

    @Test
    public void testGetAgent_returnsFromCoordinator() throws Exception {
        settings.setAreProcessesSynced(true);
        settings.setIsCacheUsed(true);
        when(requestInterface.getAgentFromCoordinator("Agent_X", "Agent_Remote")).thenReturn(mockAgent);

        Agent agent = accessor.getAgent("Agent_Remote");
        assertNotNull(agent, "Should return the agent from the coordinator.");
        assertEquals("Agent_X", agent.name());
        assertTrue(cache.doesAgentExist("Agent_X"), "Agent should now be cached.");
    }

    @Test
    public void testGetFilteredAgents_localOnly() {
        Predicate<Agent> filter = a -> true;
        Agent mockA = mock(Agent.class);
        localAgentSet.add(mockA);

        settings.setIsCacheUsed(false);
        settings.setAreProcessesSynced(false);

        AgentSet result = accessor.getFilteredAgents(filter);
        assertNotNull(result);
        assertNotEquals(0, result.size(), "Should return filtered agents from the local set.");
    }

    @Test
    public void testGetFilteredAgents_fromCoordinator() throws Exception {
        Predicate<Agent> filter = a -> true;
        AgentSet filtered = new AgentSet();
        filtered.add(mockAgent);

        settings.setIsCacheUsed(true);
        settings.setAreProcessesSynced(true);

        when(requestInterface.getFilteredAgentsFromCoordinator("Agent_X", filter)).thenReturn(filtered);

        AgentSet result = accessor.getFilteredAgents(filter);
        assertNotNull(result);
        assertTrue(cache.doesAgentFilterExist(filter), "Filter should be cached.");
        assertNotEquals(0, result.size(), "Should return the filtered agent set.");
    }

    @Test
    public void testGetEnvironment_returnsLocal() {
        settings.setAreProcessesSynced(false);
        Environment env = accessor.getEnvironment();
        assertSame(mockEnvironment, env, "Should return the local environment.");
    }

    @Test
    public void testGetEnvironment_returnsFromCoordinator() throws Exception {
        settings.setAreProcessesSynced(true);
        settings.setIsCacheUsed(true);

        when(requestInterface.getEnvironmentFromCoordinator("Agent_X")).thenReturn(mockEnvironment);

        Environment env = accessor.getEnvironment();
        assertNotNull(env, "Should retrieve the environment from the coordinator.");
        assertSame(mockEnvironment, cache.getEnvironment(), "Environment should now be cached.");
    }
}
