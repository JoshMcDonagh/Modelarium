package dev.modelarium.examples.axelrod_cultural_dissemination.scheduler;

import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.culture.Culture;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.culture.CultureProperty;
import dev.modelarium.examples.axelrod_cultural_dissemination.entities.agents.attributes.geography.GridPosition;
import modelarium.clock.ReadOnlyClock;
import modelarium.entities.Agent;
import modelarium.entities.agentsets.AgentSet;
import modelarium.entities.readonly.ReadOnlyEnvironment;
import modelarium.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Executes Axelrod's asynchronous social-influence process in sequential event batches.
 *
 * <p>Every individual event selects exactly one active site and one of its cardinal neighbours. Events within a
 * Modelarium tick are deliberately processed sequentially against the mutable worker state, so a later event in the
 * same batch sees the cultural changes produced by earlier events. This retains Axelrod's event-at-a-time semantics
 * while avoiding a full Modelarium synchronisation barrier after every micro-event.
 */
public final class AxelrodEventScheduler implements Scheduler {
    private final int width;
    private final int height;
    private final int numOfEvents;
    private final int eventsPerModelariumTick;

    public AxelrodEventScheduler(
            int width,
            int height,
            int numOfEvents,
            int eventsPerModelariumTick
    ) {
        this.width = width;
        this.height = height;
        this.numOfEvents = numOfEvents;
        this.eventsPerModelariumTick = eventsPerModelariumTick;
    }

    @Override
    public void runTick(
            String threadName,
            ReadOnlyClock clock,
            ReadOnlyEnvironment environment,
            AgentSet agentSet,
            RandomGenerator random
    ) {
        long firstEventIndex = clock.currentTick() * (long) eventsPerModelariumTick;
        if (firstEventIndex >= numOfEvents)
            return; // Deliberate final no-op tick so the environment can observe the final worker state.

        int eventsThisTick = (int) Math.min(
                eventsPerModelariumTick,
                numOfEvents - firstEventIndex
        );

        for (int event = 0; event < eventsThisTick; event++)
            runEvent(agentSet, random);
    }

    private void runEvent(AgentSet agentSet, RandomGenerator random) {
        if (agentSet.isEmpty())
            return;

        Agent activeSite = agentSet.get(random.nextInt(agentSet.size()));
        GridPosition activePosition = (GridPosition) activeSite
                .getProperty("geography", "location")
                .get();

        List<Agent> neighbours = neighbours(activePosition, agentSet);
        if (neighbours.isEmpty())
            return;

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
            return;

        if (random.nextDouble() >= similarity)
            return;

        List<Integer> differingFeatures = activeCulture.differingFeatures(neighbourCulture);
        int feature = differingFeatures.get(random.nextInt(differingFeatures.size()));
        activeCultureProperty.set(activeCulture.withTrait(feature, neighbourCulture.trait(feature)));
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
