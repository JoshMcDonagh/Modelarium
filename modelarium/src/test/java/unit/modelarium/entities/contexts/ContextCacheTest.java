package unit.modelarium.entities.contexts;

import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.agents.immutable.ReadOnlyAgentSet;
import modelarium.entities.contexts.ContextCache;
import modelarium.entities.environments.ReadOnlyEnvironment;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.entities.contexts.ContextTestHelpers.*;

public class ContextCacheTest {

    @Test
    public void testClear_ClearsAllCachedValues() {
        ContextCache cache = new ContextCache();
        Predicate<ReadOnlyAgent> allAgentsFilter = agent -> true;
        Predicate<ReadOnlyAgent> livingAgentsFilter = agent -> true;
        ReadOnlyAgentSet globalAgentSet = agentSetOfSize(4).getAsImmutable();
        ReadOnlyAgentSet livingAgentSet = agentSetOfSize(3).getAsImmutable();
        ReadOnlyEnvironment environment = emptyEnvironment().getAsImmutable();
        ReadOnlyAgent individualAgent = emptyAgent("cached").getAsImmutable();

        cache.addFilteredAgents(allAgentsFilter, globalAgentSet);
        cache.addLivingOnlyFilteredAgents(livingAgentsFilter, livingAgentSet);
        cache.addGlobalAgentSet(globalAgentSet);
        cache.addAgent(individualAgent);
        cache.addCurrentPopulationSize(4);
        cache.addEnvironment(environment);

        cache.clear();

        assertFalse(cache.doesAgentFilterExist(allAgentsFilter));
        assertFalse(cache.doesLivingOnlyAgentFilterExist(livingAgentsFilter));
        assertFalse(cache.doesGlobalAgentSetExist());
        assertFalse(cache.doesAgentExist(individualAgent.name()));
        assertFalse(cache.doesCurrentPopulationSizeExist());
        assertFalse(cache.doesEnvironmentExist());
        assertNull(cache.getGlobalAgentSet());
        assertNull(cache.getEnvironment());
    }

    @Test
    public void testGlobalAgentSetCache_AddGetAndExists() {
        ContextCache cache = new ContextCache();
        ReadOnlyAgentSet agentSet = agentSetOfSize(5).getAsImmutable();

        assertFalse(cache.doesGlobalAgentSetExist());
        cache.addGlobalAgentSet(agentSet);
        assertTrue(cache.doesGlobalAgentSetExist());
        assertSame(agentSet, cache.getGlobalAgentSet());
    }

    @Test
    public void testFilteredAgentsCache_AddGetAndExists() {
        ContextCache cache = new ContextCache();
        Predicate<ReadOnlyAgent> filter = agent -> true;
        ReadOnlyAgentSet results = agentSetOfSize(5).getAsImmutable();

        assertFalse(cache.doesAgentFilterExist(filter));
        cache.addFilteredAgents(filter, results);
        assertTrue(cache.doesAgentFilterExist(filter));
        assertSame(results, cache.getFilteredAgents(filter));
    }

    @Test
    public void testLivingOnlyFilteredAgentsCache_AddGetAndExists() {
        ContextCache cache = new ContextCache();
        Predicate<ReadOnlyAgent> filter = agent -> true;
        ReadOnlyAgentSet results = agentSetOfSize(5).getAsImmutable();

        assertFalse(cache.doesLivingOnlyAgentFilterExist(filter));
        cache.addLivingOnlyFilteredAgents(filter, results);
        assertTrue(cache.doesLivingOnlyAgentFilterExist(filter));
        assertSame(results, cache.getLivingOnlyFilteredAgents(filter));
    }

    @Test
    public void testFilteredCaches_AreSeparateForSamePredicate() {
        ContextCache cache = new ContextCache();
        Predicate<ReadOnlyAgent> filter = agent -> true;
        ReadOnlyAgentSet includingDead = agentSetOfSize(5).getAsImmutable();
        ReadOnlyAgentSet livingOnly = agentSetOfSize(4).getAsImmutable();

        cache.addFilteredAgents(filter, includingDead);
        cache.addLivingOnlyFilteredAgents(filter, livingOnly);

        assertSame(includingDead, cache.getFilteredAgents(filter));
        assertSame(livingOnly, cache.getLivingOnlyFilteredAgents(filter));
    }

    @Test
    public void testFilteredCaches_UsePredicateIdentity() {
        ContextCache cache = new ContextCache();
        Predicate<ReadOnlyAgent> cachedFilter = agent -> true;
        Predicate<ReadOnlyAgent> equivalentButDifferentFilter = agent -> true;

        cache.addFilteredAgents(cachedFilter, agentSetOfSize(2).getAsImmutable());
        cache.addLivingOnlyFilteredAgents(cachedFilter, agentSetOfSize(2).getAsImmutable());

        assertTrue(cache.doesAgentFilterExist(cachedFilter));
        assertTrue(cache.doesLivingOnlyAgentFilterExist(cachedFilter));
        assertFalse(cache.doesAgentFilterExist(equivalentButDifferentFilter));
        assertFalse(cache.doesLivingOnlyAgentFilterExist(equivalentButDifferentFilter));
    }

    @Test
    public void testIndividualAgentCache_AddGetAndExists() {
        ContextCache cache = new ContextCache();
        ReadOnlyAgent agent = emptyAgent("Steve").getAsImmutable();

        assertFalse(cache.doesAgentExist(agent.name()));
        cache.addAgent(agent);
        assertTrue(cache.doesAgentExist(agent.name()));
        assertSame(agent, cache.getAgent(agent.name()));
    }

    @Test
    public void testCurrentPopulationSizeCache_AddGetAndExists() {
        ContextCache cache = new ContextCache();

        assertFalse(cache.doesCurrentPopulationSizeExist());
        cache.addCurrentPopulationSize(17);
        assertTrue(cache.doesCurrentPopulationSizeExist());
        assertEquals(17, cache.getCurrentPopulationSize());
    }

    @Test
    public void testEnvironmentCache_AddGetAndExists() {
        ContextCache cache = new ContextCache();
        ReadOnlyEnvironment environment = emptyEnvironment().getAsImmutable();

        assertFalse(cache.doesEnvironmentExist());
        cache.addEnvironment(environment);
        assertTrue(cache.doesEnvironmentExist());
        assertSame(environment, cache.getEnvironment());
    }
}
