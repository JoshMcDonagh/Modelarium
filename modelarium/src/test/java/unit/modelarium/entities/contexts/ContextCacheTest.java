package unit.modelarium.entities.contexts;

import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.immutable.ReadOnlyAgentSet;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.agents.mutable.AgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.Environment;
import modelarium.entities.environments.ReadOnlyEnvironment;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.contexts.ContextTestHelpers.*;

public class ContextCacheTest {
    private Map<String, Integer> getAgentIndexMap(ReadOnlyAgentSet individualAgentCache) throws NoSuchFieldException, IllegalAccessException {
        Field mutableVersionField = ReadOnlyAgentSet.class.getDeclaredField("mutableVersion");
        mutableVersionField.setAccessible(true);
        AgentSet mutableAgentSet = (AgentSet) mutableVersionField.get(individualAgentCache);

        Field agentIndexMapField = AgentSet.class.getDeclaredField("agentIndexMap");
        agentIndexMapField.setAccessible(true);
        return (Map<String, Integer>) agentIndexMapField.get(mutableAgentSet);
    }

    private ContextCache createContextCache(
            ReadOnlyAgentSet individualAgentCache,
            IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache,
            ReadOnlyEnvironment environment
    ) throws NoSuchFieldException, IllegalAccessException {
        ContextCache cache = new ContextCache();

        Field individualAgentCacheListField = ContextCache.class.getDeclaredField("individualAgentCacheList");
        individualAgentCacheListField.setAccessible(true);
        individualAgentCacheListField.set(cache, individualAgentCache.getAsList());

        Field individualAgentCacheMapField = ContextCache.class.getDeclaredField("individualAgentCacheMap");
        individualAgentCacheMapField.setAccessible(true);
        individualAgentCacheMapField.set(cache, getAgentIndexMap(individualAgentCache));

        Field filteredAgentsCacheField = ContextCache.class.getDeclaredField("filteredAgentsCache");
        filteredAgentsCacheField.setAccessible(true);
        filteredAgentsCacheField.set(cache, filteredAgentsCache);

        Field environmentField = ContextCache.class.getDeclaredField("environment");
        environmentField.setAccessible(true);
        environmentField.set(cache, environment);

        return cache;
    }

    private List<ReadOnlyAgent> getIndividualAgentCacheList(ContextCache cache) throws NoSuchFieldException, IllegalAccessException {
        Field individualAgentCacheListField = ContextCache.class.getDeclaredField("individualAgentCacheList");
        individualAgentCacheListField.setAccessible(true);
        return (List<ReadOnlyAgent>) individualAgentCacheListField.get(cache);
    }

    private Map<String, Integer> getIndividualAgentCacheMap(ContextCache cache) throws NoSuchFieldException, IllegalAccessException {
        Field individualAgentCacheMapField = ContextCache.class.getDeclaredField("individualAgentCacheMap");
        individualAgentCacheMapField.setAccessible(true);
        return (Map<String, Integer>) individualAgentCacheMapField.get(cache);
    }

    private IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> getFilteredAgentsCache(ContextCache contextCache) throws NoSuchFieldException, IllegalAccessException {
        Field filteredAgentsCacheField = ContextCache.class.getDeclaredField("filteredAgentsCache");
        filteredAgentsCacheField.setAccessible(true);
        return (IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet>) filteredAgentsCacheField.get(contextCache);
    }

    private ReadOnlyEnvironment getEnvironment(ContextCache contextCache) throws NoSuchFieldException, IllegalAccessException {
        Field environmentField = ContextCache.class.getDeclaredField("environment");
        environmentField.setAccessible(true);
        return (ReadOnlyEnvironment) environmentField.get(contextCache);
    }

    @Test
    public void testClear() throws NoSuchFieldException, IllegalAccessException {
        ReadOnlyAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        Environment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment.getAsImmutable()
        );

        cache.clear();

        assertTrue(getIndividualAgentCacheList(cache).isEmpty());
        assertTrue(getIndividualAgentCacheMap(cache).isEmpty());
        assertTrue(getFilteredAgentsCache(cache).isEmpty());
        assertNull(getEnvironment(cache));
    }

    @Test
    public void testDoesAgentFilterExistTrue() throws NoSuchFieldException, IllegalAccessException {
        Predicate<ReadOnlyAgent> filter = a -> a.attributeSetCount() > 4;

        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(filter, agentSetOfSize(5).getAsImmutable());
        filteredAgentsCache.put(a -> true, agentSetOfSize(20).getAsImmutable());

        ContextCache cache = createContextCache(
                agentSetOfSize(20).getAsImmutable(),
                filteredAgentsCache,
                emptyEnvironment().getAsImmutable()
        );

        assertTrue(cache.doesAgentFilterExist(filter));
    }

    @Test
    public void testDoesAgentFilterExistFalse() throws NoSuchFieldException, IllegalAccessException {
        Predicate<ReadOnlyAgent> filter = a -> a.attributeSetCount() > 4;

        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, agentSetOfSize(20).getAsImmutable());

        ContextCache cache = createContextCache(
                agentSetOfSize(20).getAsImmutable(),
                filteredAgentsCache,
                emptyEnvironment().getAsImmutable()
        );

        assertFalse(cache.doesAgentFilterExist(filter));
    }

    @Test
    public void testAddFilteredAgents() throws NoSuchFieldException, IllegalAccessException {
        Predicate<ReadOnlyAgent> filter = a -> a.attributeSetCount() > 4;
        ReadOnlyAgentSet results = agentSetOfSize(5).getAsImmutable();

        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, agentSetOfSize(20).getAsImmutable());

        ContextCache cache = createContextCache(
                agentSetOfSize(20).getAsImmutable(),
                filteredAgentsCache,
                emptyEnvironment().getAsImmutable()
        );

        cache.addFilteredAgents(filter, results);

        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgents = getFilteredAgentsCache(cache);

        assertTrue(filteredAgents.containsKey(filter));
        assertTrue(filteredAgents.containsValue(results));
        assertEquals(results, filteredAgents.get(filter));
    }

    @Test
    public void testGetFilteredAgents() throws NoSuchFieldException, IllegalAccessException {
        Predicate<ReadOnlyAgent> filter = a -> a.attributeSetCount() > 4;
        ReadOnlyAgentSet results = agentSetOfSize(5).getAsImmutable();

        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(filter, results);
        filteredAgentsCache.put(a -> true, agentSetOfSize(20).getAsImmutable());

        ContextCache cache = createContextCache(
                agentSetOfSize(20).getAsImmutable(),
                filteredAgentsCache,
                emptyEnvironment().getAsImmutable()
        );

        ReadOnlyAgentSet returnedAgents = cache.getFilteredAgents(filter);

        assertEquals(results, returnedAgents);
    }

    @Test
    public void testDoesAgentExistTrue() throws NoSuchFieldException, IllegalAccessException {
        String agentName = "Steve";

        AgentSet individualAgentCache = agentSetOfSize(20);
        individualAgentCache.add(emptyAgent(agentName));
        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache.getAsImmutable());
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        Environment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache.getAsImmutable(),
                filteredAgentsCache,
                environment.getAsImmutable()
        );

        assertTrue(cache.doesAgentExist(agentName));
    }

    @Test
    public void testDoesAgentExistFalse() throws NoSuchFieldException, IllegalAccessException {
        String agentName = "Steve";

        ReadOnlyAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        Environment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment.getAsImmutable()
        );

        assertFalse(cache.doesAgentExist(agentName));
    }

    @Test
    public void testGetAgent() throws NoSuchFieldException, IllegalAccessException {
        String agentName = "Steve";

        AgentSet individualAgentCache = agentSetOfSize(20);
        Agent agent = emptyAgent(agentName);
        individualAgentCache.add(agent);
        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache.getAsImmutable());
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        Environment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache.getAsImmutable(),
                filteredAgentsCache,
                environment.getAsImmutable()
        );

        assertSame(agent.getAsImmutable(), cache.getAgent(agentName));
    }

    @Test
    public void testAddAgent() throws NoSuchFieldException, IllegalAccessException {
        int populationSize = 20;
        ReadOnlyAgentSet individualAgentCache = agentSetOfSize(populationSize).getAsImmutable();
        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        Environment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment.getAsImmutable()
        );

        String agentName = "Mary";
        ReadOnlyAgent agent = emptyAgent(agentName).getAsImmutable();

        cache.addAgent(agent);

        List<ReadOnlyAgent> resultingIndividualAgentCacheList = getIndividualAgentCacheList(cache);
        Map<String, Integer> resultingIndividualAgentCacheMap = getIndividualAgentCacheMap(cache);

        assertEquals(populationSize + 1, resultingIndividualAgentCacheList.size());
        assertTrue(resultingIndividualAgentCacheMap.containsKey(agentName));
        assertSame(agent, resultingIndividualAgentCacheList.get(resultingIndividualAgentCacheMap.get(agentName)));
    }

    @Test
    public void testDoesEnvironmentExistTrue() throws NoSuchFieldException, IllegalAccessException {
        ReadOnlyAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        Environment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment.getAsImmutable()
        );

        assertTrue(cache.doesEnvironmentExist());
    }

    @Test
    public void testDoesEnvironmentExistFalse() throws NoSuchFieldException, IllegalAccessException {
        ReadOnlyAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                null
        );

        assertFalse(cache.doesEnvironmentExist());
    }

    @Test
    public void testGetEnvironment() throws NoSuchFieldException, IllegalAccessException {
        ReadOnlyAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        Environment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment.getAsImmutable()
        );

        assertSame(environment.getAsImmutable(), cache.getEnvironment());
    }

    @Test
    public void testAddEnvironment() throws NoSuchFieldException, IllegalAccessException {
        ReadOnlyAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<ReadOnlyAgent>, ReadOnlyAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                null
        );

        ReadOnlyEnvironment environment = emptyEnvironment().getAsImmutable();

        cache.addEnvironment(environment);

        assertSame(environment, getEnvironment(cache));
    }
}
