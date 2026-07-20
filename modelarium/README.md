# Modelarium (library)

This directory contains the Modelarium library itself, published to Maven Central as `dev.modelarium:modelarium`.
For the repository overview see the [root README](../README.md); for complete runnable models see the
[examples module](../modelarium-examples/).

API (Javadoc): https://joshmcdonagh.github.io/Modelarium/

## Installation

```xml
<dependency>
  <groupId>dev.modelarium</groupId>
  <artifactId>modelarium</artifactId>
  <version>2.0.0</version>
</dependency>
```

Modelarium is a named Java module (`modelarium`), so JPMS consumers can declare `requires modelarium;`. It works
equally well on the classpath.

## Core concepts

- **`Model`** - runs a simulation from a `Config`: it generates the entities, launches one worker thread per core
  (plus a co-ordinator thread when synchronisation is enabled), drives the tick loop, and collects the results.
- **`Config`** - an immutable record describing a run, created through `Config.builder()`. See the
  [configuration reference](#configuration-reference) below.
- **`Agent` / `Environment`** - the model's entities. A model contains many agents and a single shared environment,
  each owning a list of attribute sets.
- **`AttributeSet`** - a named, ordered group of attributes belonging to an entity. Each tick, an entity runs its
  attribute sets in order, and each set runs its attributes in order, recording the values of logged attributes.
- **Attributes** - the units of state and behaviour, in three forms:
  - **Properties** carry a typed value. Each tick their run logic executes and (if logged) the value is recorded.
  - **Events** hold a trigger condition and behaviour. Each tick the trigger is checked, the behaviour runs if it
    holds, and (if logged) the trigger state is recorded.
  - **Routines** are behaviour only: they run unconditionally every tick and are never logged.

  Every attribute has an access level: `PUBLIC` attributes can be read by other entities, while `PRIVATE` ones can
  only be used by the framework's tick loop and throw an `AttributeAccessException` if retrieved.
- **Functional attributes** - `FunctionalAgentProperty`, `FunctionalAgentEvent`, `FunctionalAgentRoutine`, and
  their environment counterparts let you supply the logic as lambdas instead of subclassing. This is the quickest
  way to define behaviour, and the intended route for cross-language use (for example from Python via JPype).
- **Contexts** - every attribute's logic receives a context (`AgentContext` or `EnvironmentContext`) giving access
  to the owning entity, the model's clock, a seeded random generator, and the rest of the population via
  `getAgent(name)` and `getFilteredAgents(predicate)`.
- **Generators** - `AgentGenerator` and `EnvironmentGenerator` define how the population is created from the
  config. `FunctionalDefaultAgentGenerator` and `FunctionalEnvironmentGenerator` accept a creation function and,
  for agents, distribute the population across cores round-robin.
- **Schedulers** - the tick policy within a core: `InOrderScheduler`, `RandomOrderScheduler`, or your own
  `Scheduler` implementation (`FunctionalScheduler` accepts a lambda).
- **Results** - after `run()`, `model.getResults()` returns an immutable view of every logged series, queryable per
  agent, attribute set, and attribute. Calling it before a run completes throws an `IllegalStateException`.

## Quickstart

A population of one-dimensional random walkers, run single-threaded with a fixed seed:

```java
AtomicInteger nextIndex = new AtomicInteger(0);

Config config = Config.builder()
        .populationSize(50)
        .tickCount(200)
        .threadCount(1)
        .areThreadsSynced(false)
        .agentGenerator(new FunctionalDefaultAgentGenerator(cfg ->
                makeWalker("walker_" + nextIndex.getAndIncrement())))
        .environmentGenerator(new FunctionalEnvironmentGenerator(cfg ->
                new Environment("environment", List.of())))
        .seed(42L)
        .build();

Model model = new Model(config);
model.run();

ImmutableResults results = model.getResults();
List<Double> trajectory = results.agents()
        .attributeLogs("walker_0", "movement", "position", Double.class);
System.out.println("walker_0 finished at " + trajectory.get(trajectory.size() - 1));
```

where each walker is a single logged property whose run function adds a Gaussian step to the stored value:

```java
private static Agent makeWalker(String name) {
    FunctionalAgentProperty<Double> position = new FunctionalAgentProperty<>(
            "position",
            true,                              // logged each tick
            AttributeAccessLevel.PUBLIC,
            Double.class,
            (context, value) -> value,                          // getter
            (context, currentValue, newValue) -> newValue,      // setter
            (context, value) ->                                 // run each tick
                    (value == null ? 0.0 : value) + context.getRandom().nextGaussian()
    );

    return new Agent(name, List.of(
            new AgentAttributeSet(name, "movement", List.<Attribute>of(position))
    ));
}
```

The [examples module](../modelarium-examples/) contains this model in full, alongside an SIR contagion model
(events and population sampling) and a synchronised two-core model (cross-core agent access).

## Configuration reference

`Config.builder()` accepts the following, with `agentGenerator` and `environmentGenerator` required:

| Builder method | Default | Meaning |
| --- | --- | --- |
| `populationSize(int)` | `100` | The number of agents the model will contain. |
| `tickCount(int)` | `100` | The number of ticks the model will perform. |
| `threadCount(int)` | `2` | The number of worker cores the agents are distributed across. |
| `threadTimeout(Duration)` | 60 seconds | How long a thread waits for a co-ordinator response before timing out. |
| `areThreadsSynced(boolean)` | `true` | Whether cores progress in lockstep through the co-ordinator. |
| `agentGenerator(AgentGenerator)` | *required* | How the model's agents are created. |
| `environmentGenerator(EnvironmentGenerator)` | *required* | How the model's environment is created. |
| `scheduler(Scheduler)` | `InOrderScheduler` | The order agents are run within a core each tick. |
| `runLogDatabaseFactory(...)` | memory-based | Where logged attribute values are stored (see below). |
| `seed(long)` | `System.nanoTime()` | The seed for the model's random generators. |

## Multithreading and synchronisation

`threadCount` controls the number of worker threads, with agents distributed across them round-robin by the
default generators. `areThreadsSynced` controls whether those workers are coordinated:

**Synchronised (`true`, the default).** A co-ordinator thread holds a global view of the population. All cores
progress in lockstep: each tick every worker runs its agents, pushes its updated agent states to the co-ordinator,
and waits at a barrier before the next tick begins. Agents can read agents on *other* cores through their context -
those reads travel through the co-ordinator and observe the other core's state as of the end of the previous tick,
which keeps runs deterministic. Reads of same-core agents are always live. The environment's attributes are run by
the co-ordinator once per tick boundary.

**Unsynchronised (`false`).** Workers run completely independently with no co-ordinator, which is faster and suits
models whose agents only interact within their own core (or single-core models). Two consequences to be aware of:
agents cannot see agents on other cores (a remote lookup throws an `AgentNotFoundException`), and **the environment's attributes do not run**, since they are driven by
the co-ordinator's tick boundary.

Use synchronised mode when agents interact across the population or the environment carries behaviour; use
unsynchronised mode for independent agents where throughput matters.

## Results and storage

Logged attribute values are recorded per tick and retrieved after the run through `model.getResults()`, at any
granularity from a single series (`attributeLogs(agent, set, attribute)`) up to every log in the model
(`allLogs()`). Where the values are stored during the run is controlled by the config's log database factory:

- **`MemoryBasedAttributeSetLogDatabaseFactory`** (default) - fastest, suitable for most simulations.
- **`DiskBasedAttributeSetLogDatabaseFactory`** - backs each attribute set's log with a SQLite database in the
  system's temporary directory, with values serialised as JSON. Suitable for large populations or long runs where
  memory pressure matters. Databases are created and cleaned up automatically, including on JVM shutdown.

## Reproducibility

The config's seed drives a splittable random generator threaded through the model, the workers, the schedulers,
and every attribute's context (`context.getRandom()`). Given the same config and seed, runs are reproducible -
including in synchronised multi-core mode, where the cross-core visibility rule above keeps interactions
deterministic. If no seed is set, `System.nanoTime()` is used and each run differs.

## Extending Modelarium

The functional attribute classes cover most needs, but attributes can also be subclassed directly - extend
`AgentProperty<T>` / `EnvironmentProperty<T>`, `AgentEvent` / `EnvironmentEvent`, or `AgentRoutine` /
`EnvironmentRoutine` and implement the abstract methods, each of which receives the context:

```java
public class DecayingValueProperty extends AgentProperty<Double> {

    private final double decayRate;
    private double value;

    public DecayingValueProperty(String name, boolean isLogged, AttributeAccessLevel accessLevel,
                                 double initialValue, double decayRate) {
        super(name, isLogged, accessLevel, Double.class);
        this.value = initialValue;
        this.decayRate = decayRate;
    }

    @Override
    protected void run(AgentContext context) {
        value *= (1.0 - decayRate);
    }

    @Override
    protected void set(AgentContext context, Double newValue) {
        value = newValue;
    }

    @Override
    protected Double get(AgentContext context) {
        return value;
    }
}
```

Events implement `isTriggered(context)` and `run(context)`; routines implement `run(context)`.

A note on state: Modelarium deep clones entities reflectively (for example when distributing agents, sharing them
across cores, or returning immutable views), so any state your attribute holds should be safely deep-cloneable -
plain fields and standard collections are fine, and no copy constructor is needed. Functional attribute lambdas
are shared rather than cloned, and contexts are re-established by the framework after cloning.

## Building and testing this module

From the repository root:

```bash
mvn -B test --file modelarium/pom.xml
```

or, using the aggregator to build the examples against it as well:

```bash
mvn -B verify
```

The test suite includes unit tests and end-to-end integration tests covering threading, schedulers, agent
interaction, and both results backends.

> CI is configured for Java 21. If you run the tests locally with a newer JDK, Mockito/Byte Buddy may fail when
> instrumenting classes.

## License

MIT - see [`LICENSE`](../LICENSE).
