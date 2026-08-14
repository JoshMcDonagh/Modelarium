package unit.modelarium.entities.contexts;

import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.MutableEnvironment;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.contexts.ContextTestHelpers.*;

public class ContextCacheTest {
    private ContextCache createContextCache(
            MutableAgentSet individualAgentCache,
            IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache,
            MutableEnvironment environment
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

    private MutableAgentSet getIndividualAgentCache(ContextCache contextCache) throws NoSuchFieldException, IllegalAccessException {
        Field individualAgentCacheField = ContextCache.class.getDeclaredField("individualAgentCache");
        individualAgentCacheField.setAccessible(true);
        return (MutableAgentSet) individualAgentCacheField.get(contextCache);
    }

    private IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> getFilteredAgentsCache(ContextCache contextCache) throws NoSuchFieldException, IllegalAccessException {
        Field filteredAgentsCacheField = ContextCache.class.getDeclaredField("filteredAgentsCache");
        filteredAgentsCacheField.setAccessible(true);
        return (IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet>) filteredAgentsCacheField.get(contextCache);
    }

    private MutableEnvironment getEnvironment(ContextCache contextCache) throws NoSuchFieldException, IllegalAccessException {
        Field environmentField = ContextCache.class.getDeclaredField("environment");
        environmentField.setAccessible(true);
        return (MutableEnvironment) environmentField.get(contextCache);
    }

    @Test
    public void testClear() throws NoSuchFieldException, IllegalAccessException {
        MutableAgentSet individualAgentCache = agentSetOfSize(20);
        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5));
        MutableEnvironment environment = emptyEnvironment();

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
        Predicate<MutableAgent> filter = a -> a.attributeSetCount() > 4;

        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(filter, agentSetOfSize(5));
        filteredAgentsCache.put(a -> true, agentSetOfSize(20));

        ContextCache cache = createContextCache(
                agentSetOfSize(20),
                filteredAgentsCache,
                emptyEnvironment()
        );

        assertTrue(cache.doesAgentFilterExist(filter));
    }

    @Test
    public void testDoesAgentFilterExistFalse() throws NoSuchFieldException, IllegalAccessException {
        Predicate<MutableAgent> filter = a -> a.attributeSetCount() > 4;

        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, agentSetOfSize(20));

        ContextCache cache = createContextCache(
                agentSetOfSize(20),
                filteredAgentsCache,
                emptyEnvironment()
        );

        assertFalse(cache.doesAgentFilterExist(filter));
    }

    @Test
    public void testAddFilteredAgents() throws NoSuchFieldException, IllegalAccessException {
        Predicate<MutableAgent> filter = a -> a.attributeSetCount() > 4;
        MutableAgentSet results = agentSetOfSize(5);

        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, agentSetOfSize(20));

        ContextCache cache = createContextCache(
                agentSetOfSize(20),
                filteredAgentsCache,
                emptyEnvironment()
        );

        cache.addFilteredAgents(filter, results);

        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgents = getFilteredAgentsCache(cache);

        assertTrue(filteredAgents.containsKey(filter));
        assertTrue(filteredAgents.containsValue(results));
        assertEquals(results, filteredAgents.get(filter));
    }

    @Test
    public void testGetFilteredAgents() throws NoSuchFieldException, IllegalAccessException {
        Predicate<MutableAgent> filter = a -> a.attributeSetCount() > 4;
        MutableAgentSet results = agentSetOfSize(5);

        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(filter, results);
        filteredAgentsCache.put(a -> true, agentSetOfSize(20));

        ContextCache cache = createContextCache(
                agentSetOfSize(20),
                filteredAgentsCache,
                emptyEnvironment()
        );

        MutableAgentSet returnedAgents = cache.getFilteredAgents(filter);

        assertEquals(results, returnedAgents);
    }

    @Test
    public void testDoesAgentExistTrue() throws NoSuchFieldException, IllegalAccessException {
        String agentName = "Steve";

        MutableAgentSet individualAgentCache = agentSetOfSize(20);
        individualAgentCache.add(emptyAgent(agentName));
        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5));
        MutableEnvironment environment = emptyEnvironment();

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

        MutableAgentSet individualAgentCache = agentSetOfSize(20);
        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5));
        MutableEnvironment environment = emptyEnvironment();

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

        MutableAgentSet individualAgentCache = agentSetOfSize(20);
        MutableAgent agent = emptyAgent(agentName);
        individualAgentCache.add(agent);
        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5));
        MutableEnvironment environment = emptyEnvironment();

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
        MutableAgentSet individualAgentCache = agentSetOfSize(populationSize);
        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5));
        MutableEnvironment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        String agentName = "Mary";
        MutableAgent agent = emptyAgent(agentName);

        cache.addAgent(agent);

        MutableAgentSet resultingIndividualAgentCache = getIndividualAgentCache(cache);

        assertEquals(populationSize + 1, resultingIndividualAgentCache.size());
        assertTrue(resultingIndividualAgentCache.doesAgentExist(agentName));
        assertSame(agent, resultingIndividualAgentCache.get(agentName));
    }

    @Test
    public void testAddAgents() throws NoSuchFieldException, IllegalAccessException {
        int originalPopulationSize = 20;
        MutableAgentSet individualAgentCache = agentSetOfSize(originalPopulationSize);
        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5));
        MutableEnvironment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        int additionalPopulationSize = 12;
        MutableAgentSet additionalIndividualAgentCache = agentSetOfSize(additionalPopulationSize);

        cache.addAgents(additionalIndividualAgentCache);

        MutableAgentSet resultingIndividualAgentCache = getIndividualAgentCache(cache);

        assertEquals(originalPopulationSize + additionalPopulationSize, resultingIndividualAgentCache.size());

        for (MutableAgent agent : additionalIndividualAgentCache) {
            assertTrue(resultingIndividualAgentCache.doesAgentExist(agent.name()));
            assertSame(agent, resultingIndividualAgentCache.get(agent.name()));
        }
    }

    @Test
    public void testDoesEnvironmentExistTrue() throws NoSuchFieldException, IllegalAccessException {
        MutableAgentSet individualAgentCache = agentSetOfSize(20);
        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5));
        MutableEnvironment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        assertTrue(cache.doesEnvironmentExist());
    }

    @Test
    public void testDoesEnvironmentExistFalse() throws NoSuchFieldException, IllegalAccessException {
        MutableAgentSet individualAgentCache = agentSetOfSize(20);
        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5));

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                null
        );

        assertFalse(cache.doesEnvironmentExist());
    }

    @Test
    public void testGetEnvironment() throws NoSuchFieldException, IllegalAccessException {
        MutableAgentSet individualAgentCache = agentSetOfSize(20);
        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5));
        MutableEnvironment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment
        );

        assertSame(environment, cache.getEnvironment());
    }

    @Test
    public void testAddEnvironment() throws NoSuchFieldException, IllegalAccessException {
        MutableAgentSet individualAgentCache = agentSetOfSize(20);
        IdentityHashMap<Predicate<MutableAgent>, MutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5));

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                null
        );

        MutableEnvironment environment = emptyEnvironment();

        cache.addEnvironment(environment);

        assertSame(environment, getEnvironment(cache));
    }
}
