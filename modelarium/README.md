# Modelarium library

Modelarium is a modular, extensible, and multithreaded agent-based modelling framework for Java 21. This module is
the library published to Maven Central as `dev.modelarium:modelarium`.

- [Repository overview](../README.md)
- [Runnable examples](../modelarium-examples/README.md)
- [Javadoc](https://joshmcdonagh.github.io/Modelarium/)
- [Supported public API policy](../PUBLIC_API.md)

## Installation

```xml
<dependency>
  <groupId>dev.modelarium</groupId>
  <artifactId>modelarium</artifactId>
  <version>2.0.0</version>
</dependency>
```

Modelarium requires Java 21. The JAR declares the stable automatic module name `modelarium`, allowing it to be used
on either the class path or module path. It deliberately has no explicit `module-info.java`: class-path use is the
most plug-and-play option, while explicitly modular applications may need to open packages containing model objects
to the reflective cloning dependency.

## Contents

- [Core concepts](#core-concepts)
- [Quickstart](#quickstart)
- [Configuration](#configuration)
- [Defining attributes](#defining-attributes)
- [Contexts and interaction](#contexts-and-interaction)
- [Generators and repeated runs](#generators-and-repeated-runs)
- [Scheduling](#scheduling)
- [Multithreading and synchronisation](#multithreading-and-synchronisation)
- [Results and export](#results-and-export)
- [Logging backends](#logging-backends)
- [Reproducibility](#reproducibility)
- [Errors and lifecycle rules](#errors-and-lifecycle-rules)
- [Extending Modelarium](#extending-modelarium)
- [Building and testing](#building-and-testing)

## Core concepts

### Model and configuration

`Model` executes a simulation from an immutable `Config`. A run generates the population and environment, creates
the worker threads, advances the simulation for the configured number of ticks, and collects logged results.
Configuration is normally created with `Config.builder()`.

### Entities and attribute sets

A model contains many `Agent` instances and one shared `Environment`. Each entity owns named `AttributeSet`
instances. An attribute set is an ordered group: its attributes run in list order on every tick.

### Attributes

Model behaviour is composed from three attribute kinds:

- A `Property<T>` carries a typed value. Its run logic can update that value and, when logging is enabled, its value
  is recorded once per tick.
- An `Event` evaluates a trigger, runs its behaviour when triggered, and can log the trigger result.
- A `Routine` performs unconditional behaviour each tick and is not logged.

Agent and environment variants give each attribute an appropriately typed context. Attributes also have an
`AttributeAccessLevel`: `PUBLIC` attributes may be retrieved by other model components, while `PRIVATE` attributes
are not exposed through the normal public lookup methods.

### Contexts

Attribute logic receives an `AgentContext` or `EnvironmentContext`. Contexts expose the owning entity, current
attribute set and attribute, clock, configuration, seeded random generator, environment, and visible agents. Use
the context rather than global mutable state when implementing behaviour.

### Results

After a completed run, `model.getResults()` returns `ReadOnlyResults`. Agent and environment logs can be queried as
individual typed series or nested maps, or exported to files.

## Quickstart

The following model runs 50 one-dimensional random walkers for 200 ticks with a fixed seed:

```java
import modelarium.Config;
import modelarium.Model;
import modelarium.entities.Agent;
import modelarium.entities.Environment;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.properties.functional.FunctionalAgentProperty;
import modelarium.entities.attributes.sets.AgentAttributeSet;
import modelarium.entities.generators.FunctionalDefaultAgentGenerator;
import modelarium.entities.generators.FunctionalEnvironmentGenerator;
import modelarium.results.readonly.ReadOnlyResults;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

AtomicInteger nextIndex = new AtomicInteger();

Config config = Config.builder()
        .populationSize(50)
        .tickCount(200)
        .threadCount(1)
        .areThreadsSynced(false)
        .agentGenerator(new FunctionalDefaultAgentGenerator((cfg, random) ->
                makeWalker("walker_" + nextIndex.getAndIncrement())))
        .environmentGenerator(new FunctionalEnvironmentGenerator((cfg, random) ->
                new Environment(List.of())))
        .seed(42L)
        .build();

Model model = new Model(config);
model.run();

ReadOnlyResults results = model.getResults();
List<Double> trajectory = results.agents()
        .attributeLogs("walker_0", "movement", "position", Double.class);
System.out.println("walker_0 finished at " + trajectory.getLast());
```

The helper constructs one logged property. Its value starts at zero and changes by one Gaussian step per tick:

```java
private static Agent makeWalker(String name) {
    FunctionalAgentProperty<Double> position = new FunctionalAgentProperty<>(
            "position",
            true,
            AttributeAccessLevel.PUBLIC,
            Double.class,
            (context, value) -> value,
            (context, currentValue, newValue) -> newValue,
            (context, value) ->
                    (value == null ? 0.0 : value) + context.getRandom().nextGaussian()
    );

    return new Agent(name, List.of(
            new AgentAttributeSet("movement", List.<Attribute>of(position))
    ));
}
```

See the [examples module](../modelarium-examples/README.md) for five complete models covering events, routines,
spatial interaction, custom schedulers, multiple cores, replication experiments, and result export.

## Configuration

`agentGenerator` and `environmentGenerator` are required. Every other builder setting has a default:

| Builder method | Default | Meaning |
| --- | --- | --- |
| `populationSize(int)` | `100` | Number of agents generated for the run; must be greater than zero. |
| `tickCount(int)` | `100` | Number of model ticks; must be greater than zero. |
| `threadCount(int)` | `2` | Number of worker threads; must be greater than zero. |
| `threadTimeout(Duration)` | 60 seconds | Maximum wait for inter-thread responses; must be positive. |
| `areThreadsSynced(boolean)` | `true` | Whether workers advance in lockstep through the co-ordinator. |
| `agentGenerator(AgentGenerator)` | Required | Creates and distributes the agent population. |
| `environmentGenerator(EnvironmentGenerator)` | Required | Creates the shared environment. |
| `scheduler(Scheduler)` | `InOrderScheduler` | Chooses the order in which a worker runs its agents. |
| `runLogDatabaseFactory(...)` | Memory-backed | Creates storage for each attribute-set log. |
| `seed(long)` | `System.nanoTime()` | Root seed from which run randomness is derived. |

`Config` validates these invariants whether it is built through the builder or its record constructor. Null required
components fail immediately with a descriptive `NullPointerException`; invalid numeric settings produce an
`IllegalArgumentException`.

## Defining attributes

### Functional attributes

The quickest approach is to supply lambdas to `FunctionalAgentProperty`, `FunctionalAgentEvent`, and
`FunctionalAgentRoutine`, or their environment counterparts. Functional properties accept getter, setter, and run
functions. A property's run function may be `null` for a no-op, but invoking a missing getter or setter throws
`MissingAttributeFunctionException`.

Functional attributes are particularly useful when integrating from another JVM language or from Python through
JPype. The functional interfaces are public and may also be implemented explicitly when a lambda is inconvenient.

### Subclassed attributes

For named, reusable components, subclass `AgentProperty<T>` / `EnvironmentProperty<T>`, `AgentEvent` /
`EnvironmentEvent`, or `AgentRoutine` / `EnvironmentRoutine`. For example:

```java
public final class DecayingValueProperty extends AgentProperty<Double> {
    private final double decayRate;
    private double value;

    public DecayingValueProperty(double initialValue, double decayRate) {
        super("decaying_value", true, AttributeAccessLevel.PUBLIC, Double.class);
        this.value = initialValue;
        this.decayRate = decayRate;
    }

    @Override
    protected void run(AgentContext context) {
        value *= 1.0 - decayRate;
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

Events implement `isTriggered(context)` and `run(context)`. Routines implement `run(context)`.

## Contexts and interaction

An agent context provides access to the current agent and environment. The inherited `SimulationContext` operations
also provide the current `Config`, clock, random generator, agent lookup, and filtered population lookup. Prefer
`context.getRandom()` for all stochastic behaviour so runs remain seed-controlled.

Population visibility depends on execution mode. In synchronised mode, same-worker reads are live and remote reads
observe the other worker's state at the previous completed tick boundary. In unsynchronised mode, an agent cannot
look up agents assigned to another worker; attempting to do so throws `AgentNotFoundException`.

Attribute lookup honours access control. Trying to retrieve a private attribute through a public lookup throws
`AttributeAccessException`. Asking for an absent agent or environment produces the corresponding documented
exception rather than returning a partially initialised object.

## Generators and repeated runs

`DefaultAgentGenerator` generates one agent at a time and distributes the completed population across workers in
round-robin order. Override `generateAgent(Config, RandomGenerator)` for the usual case. Implement
`AgentGenerator` directly when the model needs custom partitioning.

`EnvironmentGenerator` creates the run's environment. Functional variants accept `BiFunction<Config,
RandomGenerator, ...>` instances, avoiding the need for subclasses.

Generators may hold temporary counters while creating entities. Override the protected `reset()` hook to clear
that state. Modelarium invokes it after every generation attempt, including one which throws, so the same generator
and `Model` configuration can be reused safely. Functional generators which accept a reset callback provide the
same facility.

## Scheduling

The scheduler determines the order in which each worker runs its local agents on a tick:

- `InOrderScheduler` preserves the agent-set order.
- `RandomOrderScheduler` shuffles through the run's seeded random generator.
- `FunctionalScheduler` delegates tick logic to a supplied function.
- A custom `Scheduler` can implement model-specific semantics, including event-driven processing.

A scheduler controls agent execution within each worker; thread synchronisation and environment execution remain
the responsibility of the model runtime.

## Multithreading and synchronisation

`threadCount` determines the number of worker threads. Default generators distribute agents across them in
round-robin order.

In **synchronised mode** (`areThreadsSynced(true)`, the default), a co-ordinator maintains the shared population
view. Workers advance in lockstep, publish their updated agent states, and wait at a tick barrier. Cross-worker
queries are supported and the environment's attributes run once per tick at the co-ordinator boundary.

In **unsynchronised mode**, workers run independently. This avoids co-ordination overhead but agents cannot access
agents on other workers. The environment's attributes do not run because no co-ordinator drives their tick loop.
Use this mode for a single worker or for populations whose partitions do not interact.

Worker, co-ordinator, and request/response classes are implementation details rather than supported extension
points. Configure concurrency through `Config` and interact through contexts.

## Results and export

Call `model.getResults()` only after `model.run()` has completed. The returned `ReadOnlyResults` exposes:

- `agents()` for per-agent logs;
- `environment()` for environment logs; and
- `export(String)` / `export(Path)` for file export.

Typed retrieval avoids repeated casts:

```java
List<Double> values = results.agents().attributeLogs(
        "agent_0", "health", "risk", Double.class
);
```

Broader methods return every attribute in a set, every set for an entity, or all agent/environment logs. Returned
collections are unmodifiable, and the storage backends return detached values so callers cannot mutate the stored
run history through a retrieved list.

Export creates a timestamped result directory beneath the requested path and writes the configuration and logged
series. The returned `Path` is the directory actually created, which is useful when an example adds its own summary
files alongside the standard export.

## Logging backends

`runLogDatabaseFactory` selects the storage used while a run is executing:

- `MemoryBasedAttributeSetLogDatabaseFactory` is the default and is appropriate for most runs.
- `DiskBasedAttributeSetLogDatabaseFactory` stores each attribute set in a temporary SQLite database, with values
  serialised as JSON. It is useful when log volume would otherwise create memory pressure.

Both implementations follow the same contract:

- `connect()` must be called before reading or writing and is idempotent;
- missing or cleared series return an empty list;
- null entries are retained but do not establish a series type;
- the first non-null append establishes the accepted runtime type;
- replacing a series validates all non-null values before changing stored data and may establish a new type;
- inputs are copied on write and results are detached on read; and
- `disconnect()` is idempotent, discards stored data, and makes subsequent operations fail until reconnection.

Normal model users do not manage these connections directly: `AttributeSetLog` connects storage when constructed,
and result cleanup disconnects it. Direct backend users must follow the lifecycle above.

## Reproducibility

The configured seed initialises a `SplittableRandom`; derived generators are passed through model setup, workers,
schedulers, and contexts. A fixed seed therefore controls every stochastic decision which uses the provided random
generators. Synchronised cross-worker visibility is tick-boundary based, keeping the ordering rule deterministic.

Reproducibility still depends on user code: avoid unseeded randomness, wall-clock decisions, iteration over
unordered external data, and shared mutable state. If no seed is supplied, the default `System.nanoTime()` value
intentionally produces a different run.

## Errors and lifecycle rules

- `getResults()` before a run throws `IllegalStateException`.
- Invalid configuration fails during `Config` construction.
- A worker or co-ordinator failure is surfaced as a Modelarium run exception rather than silently producing partial
  results.
- Missing cross-worker entities, private attribute access, missing functional callbacks, timeouts, and interruption
  have dedicated exceptions in `modelarium.exceptions`.
- Reading or writing a disconnected log database throws `IllegalStateException`.

Consult the [Javadoc](https://joshmcdonagh.github.io/Modelarium/) for method-level contracts and exception types.

## Extending Modelarium

The supported extension points are attributes, generators, schedulers, log database factories, and their functional
interfaces. Packages marked `@PublicApi` form the supported API unless a type or member is marked `@Internal`; see
the [public API policy](../PUBLIC_API.md). Do not depend on the multithreading, mutable results, or other internal
implementation packages.

Modelarium deep-clones entities when distributing or sharing state and when constructing immutable views. Model
objects should therefore contain cloneable state. Plain fields, records, and ordinary collections are suitable.
Functional callbacks are shared rather than reflectively cloned, and simulation contexts are re-established by the
runtime after cloning.

## Building and testing

From the repository root, run the release-representative build:

```bash
mvn -B verify
```

This builds and tests the library and examples and applies the configured coverage checks. To test only the library:

```bash
mvn -B test --file modelarium/pom.xml
```

To install a development build locally:

```bash
mvn -B install --file modelarium/pom.xml
```

CI and the Maven Enforcer configuration require Java 21. A newer JDK is not treated as a supported build runtime.

## Licence

Modelarium is available under the MIT Licence. See [`LICENSE`](../LICENSE).
