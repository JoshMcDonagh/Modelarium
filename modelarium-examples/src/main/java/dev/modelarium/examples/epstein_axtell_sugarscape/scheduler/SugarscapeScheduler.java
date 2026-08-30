package dev.modelarium.examples.epstein_axtell_sugarscape.scheduler;

import modelarium.clock.ReadOnlyClock;
import modelarium.entities.Agent;
import modelarium.entities.readonly.ReadOnlyEnvironment;
import modelarium.entities.agentsets.AgentSet;
import modelarium.scheduler.Scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.random.RandomGenerator;

/**
 * Sequential implementation of the Chapter II Sugarscape rules G, M, R, S, P and D.
 *
 * <p>The original model randomises the order in which agents execute M. This scheduler therefore intentionally runs
 * on one Modelarium worker and updates agents one at a time against the current state. A complete Sugarscape step is
 * still one Modelarium tick, so the example also exercises Modelarium's scheduler extension point directly.
 */
public final class SugarscapeScheduler implements Scheduler {
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private static final String MAP_RESOURCE =
            "dev/modelarium/examples/epstein_axtell_sugarscape/sugar-map.txt";

    public record Metrics(
            int step,
            int population,
            double meanVision,
            double meanMetabolism,
            double meanWealth,
            int maximumWealth,
            double gini,
            int northPopulation,
            int southPopulation,
            double highCapacityOccupancyFraction,
            double totalPollution,
            double meanHighCapacityPollution,
            int neighbourEdges,
            int neighbourComponents,
            int largestNeighbourComponent,
            boolean neighbourGraphHasCycle
    ) {}

    public record SnapshotCell(int step, int x, int y, int capacity, int sugar, double pollution, boolean occupied) {}

    private final SugarscapeRunSpec spec;
    private final int[][] capacity = loadCapacityMap();
    private final int[][] sugar = new int[HEIGHT][WIDTH];
    private final double[][] pollution = new double[HEIGHT][WIDTH];
    private final List<Metrics> metrics = new ArrayList<>();
    private final List<SnapshotCell> snapshots = new ArrayList<>();
    private List<Integer> initialWealths = List.of();
    private List<Integer> finalWealths = List.of();
    private boolean initialised = false;

    public SugarscapeScheduler(SugarscapeRunSpec spec) {
        this.spec = spec;
        for (int y = 0; y < HEIGHT; y++)
            System.arraycopy(capacity[y], 0, sugar[y], 0, WIDTH);
    }

    @Override
    public void runTick(
            String threadName,
            ReadOnlyClock clock,
            ReadOnlyEnvironment environment,
            AgentSet agentSet,
            RandomGenerator random
    ) {
        int tick = clock.currentTick();
        if (!initialised) {
            metrics.add(measure(0, agentSet));
            captureSnapshot(0, agentSet);
            initialised = true;
        }

        growback(tick);
        if (spec.pollution() && tick >= 100)
            diffusePollution();

        boolean[][] occupied = occupancy(agentSet);
        List<Agent> order = agentSet.getAsList();
        Collections.shuffle(order, random);

        List<Agent> newlyDead = new ArrayList<>();
        for (Agent agent : order) {
            if (!bool(agent, "alive"))
                continue;

            moveAndHarvest(agent, occupied, tick, random);

            int age = integer(agent, "age") + 1;
            setInt(agent, "age", age);
            if (integer(agent, "wealth") <= 0 || age > integer(agent, "max_age")) {
                occupied[integer(agent, "y")][integer(agent, "x")] = false;
                setBool(agent, "alive", false);
                newlyDead.add(agent);
            }
        }

        if (spec.replacement()) {
            Collections.shuffle(newlyDead, random);
            for (Agent dead : newlyDead)
                replace(dead, occupied, random);
        }

        int completedStep = tick + 1;
        metrics.add(measure(completedStep, agentSet));
        if (shouldCaptureSnapshot(completedStep))
            captureSnapshot(completedStep, agentSet);

        // Properties are passive state containers; running them here records the complete post-step agent state in
        // Modelarium's normal result logs as well as in the experiment-level CSVs.
        for (Agent agent : agentSet)
            agent.run();
    }

    public List<Metrics> metrics() { return List.copyOf(metrics); }
    public List<SnapshotCell> snapshots() { return List.copyOf(snapshots); }
    public List<Integer> initialWealths() { return List.copyOf(initialWealths); }
    public List<Integer> finalWealths() { return List.copyOf(finalWealths); }
    public int[][] capacityMap() {
        int[][] copy = new int[HEIGHT][WIDTH];
        for (int y = 0; y < HEIGHT; y++)
            System.arraycopy(capacity[y], 0, copy[y], 0, WIDTH);
        return copy;
    }

    private void growback(int tick) {
        switch (spec.growthMode()) {
            case IMMEDIATE -> {
                for (int y = 0; y < HEIGHT; y++)
                    System.arraycopy(capacity[y], 0, sugar[y], 0, WIDTH);
            }
            case CONSTANT -> growAllByOne();
            case SEASONAL -> growSeasonally(tick);
        }
    }

    private void growAllByOne() {
        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                if (sugar[y][x] < capacity[y][x])
                    sugar[y][x]++;
    }

    private void growSeasonally(int tick) {
        boolean northSummer = (tick / 50) % 2 == 0;
        boolean winterGrowbackTick = tick % 8 == 0;
        for (int y = 0; y < HEIGHT; y++) {
            boolean north = y >= HEIGHT / 2;
            boolean summer = north == northSummer;
            if (!summer && !winterGrowbackTick)
                continue;
            for (int x = 0; x < WIDTH; x++)
                if (sugar[y][x] < capacity[y][x])
                    sugar[y][x]++;
        }
    }

    private void moveAndHarvest(Agent agent, boolean[][] occupied, int tick, RandomGenerator random) {
        int oldX = integer(agent, "x");
        int oldY = integer(agent, "y");
        int vision = integer(agent, "vision");

        List<Candidate> best = new ArrayList<>();
        double bestWelfare = Double.NEGATIVE_INFINITY;
        int bestDistance = Integer.MAX_VALUE;

        // The current site is allowed. It is treated as available to its current occupant.
        Candidate current = candidate(oldX, oldY, 0);
        best.add(current);
        bestWelfare = welfare(oldX, oldY, tick);
        bestDistance = 0;

        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] direction : directions) {
            for (int distance = 1; distance <= vision; distance++) {
                int x = Math.floorMod(oldX + direction[0] * distance, WIDTH);
                int y = Math.floorMod(oldY + direction[1] * distance, HEIGHT);
                if (occupied[y][x])
                    continue;

                double welfare = welfare(x, y, tick);
                if (welfare > bestWelfare || (Double.compare(welfare, bestWelfare) == 0 && distance < bestDistance)) {
                    best.clear();
                    best.add(candidate(x, y, distance));
                    bestWelfare = welfare;
                    bestDistance = distance;
                } else if (Double.compare(welfare, bestWelfare) == 0 && distance == bestDistance) {
                    best.add(candidate(x, y, distance));
                }
            }
        }

        Candidate destination = best.get(random.nextInt(best.size()));
        occupied[oldY][oldX] = false;
        occupied[destination.y][destination.x] = true;
        setInt(agent, "x", destination.x);
        setInt(agent, "y", destination.y);

        int gathered = sugar[destination.y][destination.x];
        sugar[destination.y][destination.x] = 0;
        int metabolism = integer(agent, "metabolism");
        setInt(agent, "wealth", integer(agent, "wealth") + gathered - metabolism);

        // In Animation II-8 pollution formation begins at t=50. P_11 adds harvested sugar and metabolism pollution.
        if (spec.pollution() && tick >= 50)
            pollution[destination.y][destination.x] += gathered + metabolism;
    }

    private double welfare(int x, int y, int tick) {
        if (spec.pollution() && tick >= 50)
            return sugar[y][x] / (1.0 + pollution[y][x]);
        return sugar[y][x];
    }

    private void diffusePollution() {
        double[][] next = new double[HEIGHT][WIDTH];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                next[y][x] = (
                        pollution[y][Math.floorMod(x - 1, WIDTH)]
                                + pollution[y][Math.floorMod(x + 1, WIDTH)]
                                + pollution[Math.floorMod(y - 1, HEIGHT)][x]
                                + pollution[Math.floorMod(y + 1, HEIGHT)][x]
                ) / 4.0;
            }
        }
        for (int y = 0; y < HEIGHT; y++)
            System.arraycopy(next[y], 0, pollution[y], 0, WIDTH);
    }

    private void replace(Agent agent, boolean[][] occupied, RandomGenerator random) {
        int freeCount = 0;
        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                if (!occupied[y][x])
                    freeCount++;
        if (freeCount == 0)
            return;

        int choice = random.nextInt(freeCount);
        int targetX = 0;
        int targetY = 0;
        outer:
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (occupied[y][x])
                    continue;
                if (choice-- == 0) {
                    targetX = x;
                    targetY = y;
                    break outer;
                }
            }
        }

        setInt(agent, "x", targetX);
        setInt(agent, "y", targetY);
        setInt(agent, "vision", randomInclusive(random, spec.visionMinimum(), spec.visionMaximum()));
        setInt(agent, "metabolism", randomInclusive(random, spec.metabolismMinimum(), spec.metabolismMaximum()));
        setInt(agent, "wealth", randomInclusive(random, spec.initialWealthMinimum(), spec.initialWealthMaximum()));
        setInt(agent, "age", 0);
        setInt(agent, "max_age", randomInclusive(random, spec.maximumAgeMinimum(), spec.maximumAgeMaximum()));
        setBool(agent, "alive", true);
        occupied[targetY][targetX] = true;
    }

    private Metrics measure(int step, AgentSet agentSet) {
        List<Agent> alive = alive(agentSet);
        List<Integer> wealthSnapshot = alive.stream().map(a -> integer(a, "wealth")).toList();
        if (step == 0) initialWealths = wealthSnapshot;
        finalWealths = wealthSnapshot;
        int population = alive.size();
        double meanVision = mean(alive, "vision");
        double meanMetabolism = mean(alive, "metabolism");
        double meanWealth = mean(alive, "wealth");
        int maximumWealth = alive.stream().mapToInt(a -> integer(a, "wealth")).max().orElse(0);
        double gini = gini(alive);
        int north = (int) alive.stream().filter(a -> integer(a, "y") >= HEIGHT / 2).count();
        int south = population - north;
        long highCapacityCells = 0;
        long occupiedHighCapacity = 0;
        boolean[][] occupied = occupancy(agentSet);
        double totalPollution = 0;
        double highPollution = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                totalPollution += pollution[y][x];
                if (capacity[y][x] >= 3) {
                    highCapacityCells++;
                    highPollution += pollution[y][x];
                    if (occupied[y][x])
                        occupiedHighCapacity++;
                }
            }
        }
        double highOccupancy = highCapacityCells == 0 ? 0 : occupiedHighCapacity / (double) highCapacityCells;
        double meanHighPollution = highCapacityCells == 0 ? 0 : highPollution / highCapacityCells;

        NetworkMetrics network = spec.recordNeighbourNetwork()
                ? networkMetrics(alive, occupied)
                : new NetworkMetrics(0, population, population > 0 ? 1 : 0, false);

        return new Metrics(
                step, population, meanVision, meanMetabolism, meanWealth, maximumWealth, gini,
                north, south, highOccupancy, totalPollution, meanHighPollution,
                network.edges, network.components, network.largestComponent, network.hasCycle
        );
    }

    private NetworkMetrics networkMetrics(List<Agent> alive, boolean[][] occupied) {
        Map<Integer, Integer> positionToIndex = new HashMap<>();
        for (int i = 0; i < alive.size(); i++)
            positionToIndex.put(integer(alive.get(i), "y") * WIDTH + integer(alive.get(i), "x"), i);

        List<Set<Integer>> adjacency = new ArrayList<>(alive.size());
        for (int i = 0; i < alive.size(); i++) adjacency.add(new HashSet<>());
        int edges = 0;
        int[][] dirs = {{1,0},{0,1}}; // count each toroidal edge once
        for (int i = 0; i < alive.size(); i++) {
            int x = integer(alive.get(i), "x");
            int y = integer(alive.get(i), "y");
            for (int[] d : dirs) {
                Integer j = positionToIndex.get(Math.floorMod(y + d[1], HEIGHT) * WIDTH + Math.floorMod(x + d[0], WIDTH));
                if (j != null && j != i) {
                    adjacency.get(i).add(j);
                    adjacency.get(j).add(i);
                    edges++;
                }
            }
        }

        boolean[] seen = new boolean[alive.size()];
        int components = 0;
        int largest = 0;
        for (int start = 0; start < alive.size(); start++) {
            if (seen[start]) continue;
            components++;
            int size = 0;
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            queue.add(start);
            seen[start] = true;
            while (!queue.isEmpty()) {
                int node = queue.removeFirst();
                size++;
                for (int neighbour : adjacency.get(node)) {
                    if (!seen[neighbour]) {
                        seen[neighbour] = true;
                        queue.add(neighbour);
                    }
                }
            }
            largest = Math.max(largest, size);
        }
        boolean hasCycle = edges >= alive.size() - components + 1;
        return new NetworkMetrics(edges, components, largest, hasCycle);
    }

    private void captureSnapshot(int step, AgentSet agentSet) {
        boolean[][] occupied = occupancy(agentSet);
        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                snapshots.add(new SnapshotCell(step, x, y, capacity[y][x], sugar[y][x], pollution[y][x], occupied[y][x]));
    }

    private boolean shouldCaptureSnapshot(int step) {
        if (step == spec.ticks())
            return true;
        if (spec.pollution())
            return step == 50 || step == 100 || step == 150 || step == 250 || step == 332;
        if (spec.growthMode() == SugarscapeRunSpec.GrowthMode.SEASONAL)
            return step % 50 == 0;
        if (spec.placementMode() == SugarscapeRunSpec.PlacementMode.LOWER_RIGHT_BLOCK)
            return step == 10 || step == 25 || step == 50 || step == 100 || step == 150;
        return false;
    }

    private boolean[][] occupancy(AgentSet agents) {
        boolean[][] occupied = new boolean[HEIGHT][WIDTH];
        for (Agent agent : agents) {
            if (bool(agent, "alive"))
                occupied[integer(agent, "y")][integer(agent, "x")] = true;
        }
        return occupied;
    }

    private List<Agent> alive(AgentSet agentSet) {
        List<Agent> result = new ArrayList<>();
        for (Agent agent : agentSet)
            if (bool(agent, "alive")) result.add(agent);
        return result;
    }

    private double mean(List<Agent> agents, String property) {
        if (agents.isEmpty()) return 0;
        return agents.stream().mapToInt(a -> integer(a, property)).average().orElse(0);
    }

    private double gini(List<Agent> agents) {
        if (agents.isEmpty()) return 0;
        int[] wealth = agents.stream().mapToInt(a -> Math.max(0, integer(a, "wealth"))).sorted().toArray();
        long total = 0;
        long weighted = 0;
        for (int i = 0; i < wealth.length; i++) {
            total += wealth[i];
            weighted += (long) (i + 1) * wealth[i];
        }
        if (total == 0) return 0;
        int n = wealth.length;
        return (2.0 * weighted) / (n * (double) total) - (n + 1.0) / n;
    }

    private static int integer(Agent agent, String name) {
        return (Integer) agent.getProperty("state", name).get();
    }

    private static boolean bool(Agent agent, String name) {
        return (Boolean) agent.getProperty("state", name).get();
    }

    private static void setInt(Agent agent, String name, int value) {
        @SuppressWarnings("unchecked")
        modelarium.entities.attributes.properties.AgentProperty<Integer> property =
                (modelarium.entities.attributes.properties.AgentProperty<Integer>) agent.getProperty("state", name);
        property.set(value);
    }

    private static void setBool(Agent agent, String name, boolean value) {
        @SuppressWarnings("unchecked")
        modelarium.entities.attributes.properties.AgentProperty<Boolean> property =
                (modelarium.entities.attributes.properties.AgentProperty<Boolean>) agent.getProperty("state", name);
        property.set(value);
    }

    private static int randomInclusive(RandomGenerator random, int minimum, int maximum) {
        if (minimum == Integer.MAX_VALUE && maximum == Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (minimum == maximum) return minimum;
        return random.nextInt(minimum, maximum + 1);
    }

    private static Candidate candidate(int x, int y, int distance) { return new Candidate(x, y, distance); }
    private record Candidate(int x, int y, int distance) {}
    private record NetworkMetrics(int edges, int components, int largestComponent, boolean hasCycle) {}

    private static int[][] loadCapacityMap() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(MAP_RESOURCE);
        if (stream == null)
            throw new IllegalStateException("Missing canonical Sugarscape map resource: " + MAP_RESOURCE);
        int[][] map = new int[HEIGHT][WIDTH];
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            for (int y = 0; y < HEIGHT; y++) {
                String line = reader.readLine();
                if (line == null) throw new IllegalStateException("Sugarscape map contains fewer than 50 rows");
                String[] values = line.trim().split("\\s+");
                if (values.length != WIDTH) throw new IllegalStateException("Sugarscape map row does not contain 50 values");
                for (int x = 0; x < WIDTH; x++) map[y][x] = Integer.parseInt(values[x]);
            }
            return map;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Sugarscape map", e);
        }
    }
}
