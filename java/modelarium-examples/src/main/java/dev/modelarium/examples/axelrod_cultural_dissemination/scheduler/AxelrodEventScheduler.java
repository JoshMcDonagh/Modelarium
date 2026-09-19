package dev.modelarium.examples.axelrod_cultural_dissemination.scheduler;

import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.culture.Culture;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.culture.CultureProperty;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.geography.GridPosition;
import dev.modelarium.examples.axelrod_cultural_dissemination.spatial.AxelrodSpatialUtils;
import modelarium.clock.ReadOnlyClock;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.agentsets.ReadOnlyAgentSet;
import modelarium.entities.readonly.ReadOnlyEnvironment;
import modelarium.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Executes one complete Axelrod replication run as a sequential event process.
 *
 * <p>The original model is asynchronous: one randomly selected site and one neighbour are considered per event.
 * Events are therefore executed one at a time against the same mutable worker state. Modelarium uses two ticks for
 * each replication: the first performs Axelrod events until an absorbing state is detected (or the configured safety
 * limit is reached), while the second is deliberately a no-op so that the environment can observe and log the final
 * synchronised culture state.
 */
public final class AxelrodEventScheduler implements Scheduler {
    private final int width;
    private final int height;
    private final int maxNumOfEvents;
    private final int stabilityCheckIntervalEvents;

    private boolean simulationExecuted = false;
    private boolean stableStateReached = false;
    private long eventsProcessed = 0;
    private long successfulInteractions = 0;

    public AxelrodEventScheduler(
            int width,
            int height,
            int maxNumOfEvents,
            int stabilityCheckIntervalEvents
    ) {
        this.width = width;
        this.height = height;
        this.maxNumOfEvents = maxNumOfEvents;
        this.stabilityCheckIntervalEvents = stabilityCheckIntervalEvents;
    }

    @Override
    public void runTick(
            String threadName,
            ReadOnlyClock clock,
            ReadOnlyEnvironment environment,
            AgentSet agentSet,
            RandomGenerator random
    ) {
        if (simulationExecuted)
            return;

        simulationExecuted = true;
        ReadOnlyAgentSet readOnlyAgents = new ReadOnlyAgentSet(agentSet);

        // This is extraordinarily unlikely for Axelrod's standard initialisation, but it makes the stopping rule
        // exact even for unusual parameterisations.
        if (AxelrodSpatialUtils.potentialInteractionPairCount(readOnlyAgents, width, height) == 0) {
            stableStateReached = true;
            return;
        }

        while (eventsProcessed < maxNumOfEvents) {
            int eventsInBatch = (int) Math.min(
                    stabilityCheckIntervalEvents,
                    maxNumOfEvents - eventsProcessed
            );

            for (int event = 0; event < eventsInBatch; event++) {
                if (runEvent(agentSet, random))
                    successfulInteractions++;
            }

            eventsProcessed += eventsInBatch;

            // Once the active-pair count is zero the process is absorbing, so checking periodically cannot change
            // the final cultural configuration; it only means the reported detection event is rounded up by at most
            // stabilityCheckIntervalEvents - 1 attempted events.
            if (AxelrodSpatialUtils.potentialInteractionPairCount(readOnlyAgents, width, height) == 0) {
                stableStateReached = true;
                return;
            }
        }
    }

    /** Returns whether an absorbing cultural configuration was detected before the safety limit. */
    public boolean stableStateReached() {
        return stableStateReached;
    }

    /** Returns the number of attempted Axelrod activation events processed in this run. */
    public long eventsProcessed() {
        return eventsProcessed;
    }

    /** Returns the number of events that actually copied one cultural trait. */
    public long successfulInteractions() {
        return successfulInteractions;
    }

    private boolean runEvent(AgentSet agentSet, RandomGenerator random) {
        if (agentSet.isEmpty())
            return false;

        Agent activeSite = agentSet.get(random.nextInt(agentSet.size()));
        GridPosition activePosition = (GridPosition) activeSite
                .getProperty("geography", "location")
                .get();

        List<Agent> neighbours = neighbours(activePosition, agentSet);
        if (neighbours.isEmpty())
            return false;

        Agent neighbour = neighbours.get(random.nextInt(neighbours.size()));

        CultureProperty activeCultureProperty = (CultureProperty) activeSite
                .getProperty("culture", "culture");
        Culture activeCulture = activeCultureProperty.get();
        Culture neighbourCulture = (Culture) neighbour
                .getProperty("culture", "culture")
                .get();

        double similarity = activeCulture.similarity(neighbourCulture);

        // No common features means no interaction; identical cultures have nothing left to transmit.
        if (similarity <= 0.0 || similarity >= 1.0)
            return false;

        if (random.nextDouble() >= similarity)
            return false;

        List<Integer> differingFeatures = activeCulture.differingFeatures(neighbourCulture);
        int feature = differingFeatures.get(random.nextInt(differingFeatures.size()));
        activeCultureProperty.set(activeCulture.withTrait(feature, neighbourCulture.trait(feature)));
        return true;
    }

    private List<Agent> neighbours(GridPosition position, AgentSet agentSet) {
        List<Agent> neighbours = new ArrayList<>(4);

        addNeighbour(neighbours, agentSet, position.x() - 1, position.y());
        addNeighbour(neighbours, agentSet, position.x() + 1, position.y());
        addNeighbour(neighbours, agentSet, position.x(), position.y() - 1);
        addNeighbour(neighbours, agentSet, position.x(), position.y() + 1);

        return neighbours;
    }

    private void addNeighbour(List<Agent> neighbours, AgentSet agentSet, int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return;
        neighbours.add(agentSet.get(siteName(x, y)));
    }

    private static String siteName(int x, int y) {
        return "site_" + x + "_" + y;
    }
}
