package unit.modelarium.entities.contexts;

import helpers.TestFixtures;
import modelarium.entities.agents.Agent;
import modelarium.entities.agents.AgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class ContextCacheTest {
    private ContextCache createContextCache(
            AgentSet individualAgentCache,
            IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache,
            Environment environment
    ) throws NoSuchFieldException, IllegalAccessException {
        ContextCache cache = new ContextCache();

        Field individualAgentCacheField = ContextCache.class.getDeclaredField("individualAgentCache");
        individualAgentCacheField.setAccessible(true);
        individualAgentCacheField.set(cache, individualAgentCache);

        Field filteredAgentsCacheField = ContextCache.class.getDeclaredField("filteredAgentsCache");
        filteredAgentsCacheField.setAccessible(true);
        filteredAgentsCacheField.set(cache, filteredAgentsCache);

        Field environmentField = ContextCache.class.getDeclaredField("environment");
        environmentField.setAccessible(true);
        environmentField.set(cache, environment);

        return cache;
    }

    private AgentSet getIndividualAgentCache(ContextCache contextCache) throws NoSuchFieldException, IllegalAccessException {
        Field individualAgentCacheField = ContextCache.class.getDeclaredField("individualAgentCache");
        individualAgentCacheField.setAccessible(true);
        return (AgentSet) individualAgentCacheField.get(contextCache);
    }

    private IdentityHashMap<Predicate<Agent>, AgentSet> getFilteredAgentsCache(ContextCache contextCache) throws NoSuchFieldException, IllegalAccessException {
        Field filteredAgentsCacheField = ContextCache.class.getDeclaredField("filteredAgentsCache");
        filteredAgentsCacheField.setAccessible(true);
        return (IdentityHashMap<Predicate<Agent>, AgentSet>) filteredAgentsCacheField.get(contextCache);
    }

    private Environment getEnvironment(ContextCache contextCache) throws NoSuchFieldException, IllegalAccessException {
        Field environmentField = ContextCache.class.getDeclaredField("environment");
        environmentField.setAccessible(true);
        return (Environment) environmentField.get(contextCache);
    }

    @Test
    public void testClear() throws NoSuchFieldException, IllegalAccessException {
        AgentSet individualAgentCache = TestFixtures.agentSetOfSize(20);
        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, TestFixtures.agentSetOfSize(5));
        Environment environment = TestFixtures.emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        cache.clear();

        assertTrue(getIndividualAgentCache(cache).isEmpty());
        assertTrue(getFilteredAgentsCache(cache).isEmpty());
        assertNull(getEnvironment(cache));
    }

    @Test
    public void testDoesAgentFilterExistTrue() throws NoSuchFieldException, IllegalAccessException {
        Predicate<Agent> filter = a -> a.attributeSetCount() > 4;

        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(filter, TestFixtures.agentSetOfSize(5));
        filteredAgentsCache.put(a -> true, TestFixtures.agentSetOfSize(20));

        ContextCache cache = createContextCache(
                TestFixtures.agentSetOfSize(20),
                filteredAgentsCache,
                TestFixtures.emptyEnvironment()
        );

        assertTrue(cache.doesAgentFilterExist(filter));
    }

    @Test
    public void testDoesAgentFilterExistFalse() throws NoSuchFieldException, IllegalAccessException {
        Predicate<Agent> filter = a -> a.attributeSetCount() > 4;

        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, TestFixtures.agentSetOfSize(20));

        ContextCache cache = createContextCache(
                TestFixtures.agentSetOfSize(20),
                filteredAgentsCache,
                TestFixtures.emptyEnvironment()
        );

        assertFalse(cache.doesAgentFilterExist(filter));
    }

    @Test
    public void testAddFilteredAgents() throws NoSuchFieldException, IllegalAccessException {
        Predicate<Agent> filter = a -> a.attributeSetCount() > 4;
        AgentSet results = TestFixtures.agentSetOfSize(5);

        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, TestFixtures.agentSetOfSize(20));

        ContextCache cache = createContextCache(
                TestFixtures.agentSetOfSize(20),
                filteredAgentsCache,
                TestFixtures.emptyEnvironment()
        );

        cache.addFilteredAgents(filter, results);

        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgents = getFilteredAgentsCache(cache);

        assertTrue(filteredAgents.containsKey(filter));
        assertTrue(filteredAgents.containsValue(results));
        assertEquals(results, filteredAgents.get(filter));
    }

    @Test
    public void testGetFilteredAgents() throws NoSuchFieldException, IllegalAccessException {
        Predicate<Agent> filter = a -> a.attributeSetCount() > 4;
        AgentSet results = TestFixtures.agentSetOfSize(5);

        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(filter, results);
        filteredAgentsCache.put(a -> true, TestFixtures.agentSetOfSize(20));

        ContextCache cache = createContextCache(
                TestFixtures.agentSetOfSize(20),
                filteredAgentsCache,
                TestFixtures.emptyEnvironment()
        );

        AgentSet returnedAgents = cache.getFilteredAgents(filter);

        assertEquals(results, returnedAgents);
    }

    @Test
    public void testDoesAgentExistTrue() throws NoSuchFieldException, IllegalAccessException {
        String agentName = "Steve";

        AgentSet individualAgentCache = TestFixtures.agentSetOfSize(20);
        individualAgentCache.add(TestFixtures.emptyAgent(agentName));
        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, TestFixtures.agentSetOfSize(5));
        Environment environment = TestFixtures.emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        assertTrue(cache.doesAgentExist(agentName));
    }

    @Test
    public void testDoesAgentExistFalse() throws NoSuchFieldException, IllegalAccessException {
        String agentName = "Steve";

        AgentSet individualAgentCache = TestFixtures.agentSetOfSize(20);
        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, TestFixtures.agentSetOfSize(5));
        Environment environment = TestFixtures.emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        assertFalse(cache.doesAgentExist(agentName));
    }

    @Test
    public void testGetAgent() throws NoSuchFieldException, IllegalAccessException {
        String agentName = "Steve";

        AgentSet individualAgentCache = TestFixtures.agentSetOfSize(20);
        Agent agent = TestFixtures.emptyAgent(agentName);
        individualAgentCache.add(agent);
        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, TestFixtures.agentSetOfSize(5));
        Environment environment = TestFixtures.emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        assertSame(agent, cache.getAgent(agentName));
    }

    @Test
    public void testAddAgent() throws NoSuchFieldException, IllegalAccessException {
        int populationSize = 20;
        AgentSet individualAgentCache = TestFixtures.agentSetOfSize(populationSize);
        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, TestFixtures.agentSetOfSize(5));
        Environment environment = TestFixtures.emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        String agentName = "Mary";
        Agent agent = TestFixtures.emptyAgent(agentName);

        cache.addAgent(agent);

        AgentSet resultingIndividualAgentCache = getIndividualAgentCache(cache);

        assertEquals(populationSize + 1, resultingIndividualAgentCache.size());
        assertTrue(resultingIndividualAgentCache.doesAgentExist(agentName));
        assertSame(agent, resultingIndividualAgentCache.get(agentName));
    }

    @Test
    public void testAddAgents() throws NoSuchFieldException, IllegalAccessException {
        int originalPopulationSize = 20;
        AgentSet individualAgentCache = TestFixtures.agentSetOfSize(originalPopulationSize);
        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, TestFixtures.agentSetOfSize(5));
        Environment environment = TestFixtures.emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        int additionalPopulationSize = 12;
        AgentSet additionalIndividualAgentCache = TestFixtures.agentSetOfSize(additionalPopulationSize);

        cache.addAgents(additionalIndividualAgentCache);

        AgentSet resultingIndividualAgentCache = getIndividualAgentCache(cache);

        assertEquals(originalPopulationSize + additionalPopulationSize, resultingIndividualAgentCache.size());

        for (Agent agent : additionalIndividualAgentCache) {
            assertTrue(resultingIndividualAgentCache.doesAgentExist(agent.name()));
            assertSame(agent, resultingIndividualAgentCache.get(agent.name()));
        }
    }

    @Test
    public void testDoesEnvironmentExistTrue() throws NoSuchFieldException, IllegalAccessException {
        AgentSet individualAgentCache = TestFixtures.agentSetOfSize(20);
        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, TestFixtures.agentSetOfSize(5));
        Environment environment = TestFixtures.emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        assertTrue(cache.doesEnvironmentExist());
    }

    @Test
    public void testDoesEnvironmentExistFalse() throws NoSuchFieldException, IllegalAccessException {
        AgentSet individualAgentCache = TestFixtures.agentSetOfSize(20);
        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, TestFixtures.agentSetOfSize(5));

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                null
        );

        assertFalse(cache.doesEnvironmentExist());
    }

    @Test
    public void testGetEnvironment() throws NoSuchFieldException, IllegalAccessException {
        AgentSet individualAgentCache = TestFixtures.agentSetOfSize(20);
        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, TestFixtures.agentSetOfSize(5));
        Environment environment = TestFixtures.emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        assertSame(environment, cache.getEnvironment());
    }

    @Test
    public void testAddEnvironment() throws NoSuchFieldException, IllegalAccessException {
        AgentSet individualAgentCache = TestFixtures.agentSetOfSize(20);
        IdentityHashMap<Predicate<Agent>, AgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, TestFixtures.agentSetOfSize(5));

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                null
        );

        Environment environment = TestFixtures.emptyEnvironment();

        cache.addEnvironment(environment);

        assertSame(environment, getEnvironment(cache));
    }
}
