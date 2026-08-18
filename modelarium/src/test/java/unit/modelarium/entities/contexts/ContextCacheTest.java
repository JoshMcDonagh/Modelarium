package unit.modelarium.entities.contexts;

import modelarium.entities.agents.Agent;
import modelarium.entities.agents.immutable.ImmutableAgent;
import modelarium.entities.agents.immutable.ImmutableAgentSet;
import modelarium.entities.agents.mutable.MutableAgent;
import modelarium.entities.agents.mutable.MutableAgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.ImmutableEnvironment;
import modelarium.entities.environments.MutableEnvironment;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.contexts.ContextTestHelpers.*;

public class ContextCacheTest {
    private Map<String, Integer> getAgentIndexMap(ImmutableAgentSet individualAgentCache) throws NoSuchFieldException, IllegalAccessException {
        Field mutableVersionField = ImmutableAgentSet.class.getDeclaredField("mutableVersion");
        mutableVersionField.setAccessible(true);
        MutableAgentSet mutableAgentSet = (MutableAgentSet) mutableVersionField.get(individualAgentCache);

        Field agentIndexMapField = MutableAgentSet.class.getDeclaredField("agentIndexMap");
        agentIndexMapField.setAccessible(true);
        return (Map<String, Integer>) agentIndexMapField.get(mutableAgentSet);
    }

    private ContextCache createContextCache(
            ImmutableAgentSet individualAgentCache,
            IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache,
            ImmutableEnvironment environment
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

    private List<ImmutableAgent> getIndividualAgentCacheList(ContextCache cache) throws NoSuchFieldException, IllegalAccessException {
        Field individualAgentCacheListField = ContextCache.class.getDeclaredField("individualAgentCacheList");
        individualAgentCacheListField.setAccessible(true);
        return (List<ImmutableAgent>) individualAgentCacheListField.get(cache);
    }

    private Map<String, Integer> getIndividualAgentCacheMap(ContextCache cache) throws NoSuchFieldException, IllegalAccessException {
        Field individualAgentCacheMapField = ContextCache.class.getDeclaredField("individualAgentCacheMap");
        individualAgentCacheMapField.setAccessible(true);
        return (Map<String, Integer>) individualAgentCacheMapField.get(cache);
    }

    private IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> getFilteredAgentsCache(ContextCache contextCache) throws NoSuchFieldException, IllegalAccessException {
        Field filteredAgentsCacheField = ContextCache.class.getDeclaredField("filteredAgentsCache");
        filteredAgentsCacheField.setAccessible(true);
        return (IdentityHashMap<Predicate<Agent>, ImmutableAgentSet>) filteredAgentsCacheField.get(contextCache);
    }

    private ImmutableEnvironment getEnvironment(ContextCache contextCache) throws NoSuchFieldException, IllegalAccessException {
        Field environmentField = ContextCache.class.getDeclaredField("environment");
        environmentField.setAccessible(true);
        return (ImmutableEnvironment) environmentField.get(contextCache);
    }

    @Test
    public void testClear() throws NoSuchFieldException, IllegalAccessException {
        ImmutableAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        MutableEnvironment environment = emptyEnvironment();

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
        Predicate<Agent> filter = a -> a.attributeSetCount() > 4;

        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
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
        Predicate<Agent> filter = a -> a.attributeSetCount() > 4;

        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
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
        Predicate<Agent> filter = a -> a.attributeSetCount() > 4;
        ImmutableAgentSet results = agentSetOfSize(5).getAsImmutable();

        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, agentSetOfSize(20).getAsImmutable());

        ContextCache cache = createContextCache(
                agentSetOfSize(20).getAsImmutable(),
                filteredAgentsCache,
                emptyEnvironment().getAsImmutable()
        );

        cache.addFilteredAgents(filter, results);

        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgents = getFilteredAgentsCache(cache);

        assertTrue(filteredAgents.containsKey(filter));
        assertTrue(filteredAgents.containsValue(results));
        assertEquals(results, filteredAgents.get(filter));
    }

    @Test
    public void testGetFilteredAgents() throws NoSuchFieldException, IllegalAccessException {
        Predicate<Agent> filter = a -> a.attributeSetCount() > 4;
        ImmutableAgentSet results = agentSetOfSize(5).getAsImmutable();

        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(filter, results);
        filteredAgentsCache.put(a -> true, agentSetOfSize(20).getAsImmutable());

        ContextCache cache = createContextCache(
                agentSetOfSize(20).getAsImmutable(),
                filteredAgentsCache,
                emptyEnvironment().getAsImmutable()
        );

        ImmutableAgentSet returnedAgents = cache.getFilteredAgents(filter);

        assertEquals(results, returnedAgents);
    }

    @Test
    public void testDoesAgentExistTrue() throws NoSuchFieldException, IllegalAccessException {
        String agentName = "Steve";

        MutableAgentSet individualAgentCache = agentSetOfSize(20);
        individualAgentCache.add(emptyAgent(agentName));
        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache.getAsImmutable());
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        MutableEnvironment environment = emptyEnvironment();

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

        ImmutableAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        MutableEnvironment environment = emptyEnvironment();

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

        MutableAgentSet individualAgentCache = agentSetOfSize(20);
        MutableAgent agent = emptyAgent(agentName);
        individualAgentCache.add(agent);
        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache.getAsImmutable());
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        MutableEnvironment environment = emptyEnvironment();

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
        ImmutableAgentSet individualAgentCache = agentSetOfSize(populationSize).getAsImmutable();
        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        MutableEnvironment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment.getAsImmutable()
        );

        String agentName = "Mary";
        ImmutableAgent agent = emptyAgent(agentName).getAsImmutable();

        cache.addAgent(agent);

        List<ImmutableAgent> resultingIndividualAgentCacheList = getIndividualAgentCacheList(cache);
        Map<String, Integer> resultingIndividualAgentCacheMap = getIndividualAgentCacheMap(cache);

        assertEquals(populationSize + 1, resultingIndividualAgentCacheList.size());
        assertTrue(resultingIndividualAgentCacheMap.containsKey(agentName));
        assertSame(agent, resultingIndividualAgentCacheList.get(resultingIndividualAgentCacheMap.get(agentName)));
    }

    @Test
    public void testDoesEnvironmentExistTrue() throws NoSuchFieldException, IllegalAccessException {
        ImmutableAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        MutableEnvironment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment.getAsImmutable()
        );

        assertTrue(cache.doesEnvironmentExist());
    }

    @Test
    public void testDoesEnvironmentExistFalse() throws NoSuchFieldException, IllegalAccessException {
        ImmutableAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
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
        ImmutableAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());
        MutableEnvironment environment = emptyEnvironment();

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                environment.getAsImmutable()
        );

        assertSame(environment.getAsImmutable(), cache.getEnvironment());
    }

    @Test
    public void testAddEnvironment() throws NoSuchFieldException, IllegalAccessException {
        ImmutableAgentSet individualAgentCache = agentSetOfSize(20).getAsImmutable();
        IdentityHashMap<Predicate<Agent>, ImmutableAgentSet> filteredAgentsCache = new IdentityHashMap<>();
        filteredAgentsCache.put(a -> true, individualAgentCache);
        filteredAgentsCache.put(a -> a.attributeSetCount() > 4, agentSetOfSize(5).getAsImmutable());

        ContextCache cache = createContextCache(
                individualAgentCache,
                filteredAgentsCache,
                null
        );

        ImmutableEnvironment environment = emptyEnvironment().getAsImmutable();

        cache.addEnvironment(environment);

        assertSame(environment, getEnvironment(cache));
    }
}
