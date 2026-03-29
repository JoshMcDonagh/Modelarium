# Modelarium Library for Agent-Based Modelling in Java

**Modelarium** is a modular, extensible, and multithreaded agent-based modelling (ABM) framework for Java. It provides a flexible architecture for defining agents, environments, and behaviours using composable attributes, and supports high-performance simulation with optional cross-thread coordination.

## Documentation

API (Javadoc): https://joshmcdonagh.github.io/Modelarium/

## Highlights

- Attribute-based modelling of agents and environments  
- Multi-core execution with optional cross-thread synchronisation  
- Pluggable schedulers (in-order, random, or custom)  
- Configurable results recording to memory or SQLite  
- Warm-up ticks supported (run simulation without recording, then record)

## Requirements

- Java 21  
- Maven

> CI is configured for Java 21. If you run tests locally with a newer JDK, Mockito/Byte Buddy may fail when instrumenting classes.

## Installation (Maven Central)

Modelarium is published to Maven Central. Add it to your `pom.xml`:

```xml
<dependency>
  <groupId>dev.modelarium</groupId>
  <artifactId>modelarium</artifactId>
  <version>1.2.0</version>
</dependency>
```

Then build your project as usual:
```bash
mvn clean package
```
## Building from source

Clone and run tests:

```bash
mvn -B test
```

Build a jar:

```bash
mvn -B package
```

Install to your local Maven repository:

```bash
mvn -B install
```

## Core concepts

- ModelElement - base concept for agents and environments  
- Attribute / Property / Event - defines state and behaviour run each tick  
- AttributeSet - groups related attributes and controls execution order  
- AttributeSetCollection - attached to a ModelElement; runs all its sets  
- AgentSet - collection of named agents  
- AgentGenerator - defines how agents are created  
- Environment - shared component ticked after agents  
- EnvironmentGenerator - defines how the environment is created  
- ModelScheduler - tick policy (in order, random, or custom)  
- Results - stores raw and processed simulation outputs  

## Built-in attributes

Modelarium provides a set of built-in properties, events, and actions to reduce the need for custom implementations.

These can be composed to define common behaviours without writing new classes.

### Properties

- `ConstantDoubleProperty` - immutable numeric value
- `AddDoubleProperty` - increments a value each tick using a reference
- `ClampDoubleProperty` - constrains a value within bounds
- `CopyDoubleProperty` - derives its value from another property

### Value references

- `LiteralDoubleRef` - fixed numeric value
- `PropertyDoubleRef` - resolves a value from another property at runtime

### Actions

- `SetDoubleAction` - sets a property to a value
- `AddDoubleAction` - increments a property
- `SetBooleanAction` - sets a boolean property
- `ToggleBooleanAction` - toggles a boolean property

### Events

- `TimerEvent` - triggers periodically based on tick count
- `ThresholdCrossingEvent` - triggers when a property crosses a threshold

### Example

```java
// Hunger increases over time but is capped at 100
AddDoubleProperty hunger = new AddDoubleProperty("hunger", true,
                new LiteralDoubleRef(1.0)
        );

ClampDoubleProperty boundedHunger = new ClampDoubleProperty(
        "boundedHunger",
        true,
        hunger,
        0.0,
        100.0
);
```

## Extending Modelarium

Modelarium is designed to be extended with custom attributes.

### Custom properties

To implement a custom `Property<T>`:

- Extend `Property<T>`
- Implement `get()` and `set()` as required
- Provide a copy constructor

Example:

```java
public class MyProperty extends Property<Double> {
    public MyProperty(MyProperty other) {
        super(other);
        // copy fields here
    }
    
    @Override
    public Double get() { ... }
    
    @Override 
    public void set(Double value) { ... }
}
```

Important:

Modelarium uses reflection-based copying. Your property must provide a copy constructor taking its own type or deep copying will fail at runtime.

### Custom events

Custom `Event` implementations should:

- Define `isTriggered()`
- Define `run()`
- Maintain any required internal state (for example, previous values for threshold logic)

Providing a copy constructor is recommended if your event stores state.

## Quickstart example

A model run is driven by `ModelSettings`. The following example configures and runs a simple simulation model:

```java
ModelSettings settings = new ModelSettings();

settings.setNumOfAgents(10);
settings.setNumOfCores(2);
settings.setNumOfTicksToRun(20);
settings.setNumOfWarmUpTicks(10);

settings.setBaseAgentAttributeSetCollection(
    ModelAttributes.getAgentAttributeSetCollection()
);
settings.setBaseEnvironmentAttributeSetCollection(
    ModelAttributes.getEnvironmentAttributeSetCollection()
);

settings.setAreProcessesSynced(true);
settings.setIsCacheUsed(true);

settings.setResultsClass(ModelResults.class);
settings.setResults(new ModelResults());

settings.setAgentGenerator(new DefaultAgentGenerator());
settings.setEnvironmentGenerator(new DefaultEnvironmentGenerator());
settings.setModelScheduler(new RandomOrderScheduler());

Results results = new Model(settings).run();
```

## Results storage

Results can be stored:

- In memory - fastest, suitable for smaller simulations
- On disk (SQLite) - suitable for large simulations or long runs

Enable disk storage:

```java
settings.setAreAttributeSetResultsStoredOnDisk(true);
```

### Notes

- Disk-backed results use SQLite internally
- Data is written incrementally during the simulation
- The database is automatically created and managed by the framework
- File lifecycle is handled by the results backend (no manual cleanup required in typical usage)

Disk storage is slower than in-memory storage but avoids memory pressure for large-scale simulations.

## Multithreading and synchronisation

- `numOfCores` controls the worker thread count  
- `areProcessesSynced` controls whether ticks are coordinated across threads

### Modes

- Synced (`true`)
  - All agents progress in lockstep per tick
  - Deterministic and safer for complex interactions
  - Slightly slower
- Unsynced (`false`)
  - Agents may progress independently across threads
  - Higher performance
  - May introduce non-deterministic behaviour

### Recommendation

- Use synced mode when correctness and reproducibility matter
- Use unsynced mode for performance experiments or independent agents

## Examples

The integration tests under `src/test/java/integration` provide complete working examples, including:

- Model setup
- Multithreaded execution
- Warm-up ticks
- Results recording

These are the best places to start when building your own models.

## Testing

Run all tests:

```bash
mvn -B test
```

The test suite includes both unit tests and end-to-end integration tests covering threading, schedulers, warm-up behaviour, and results backends.

## License

See `LICENSE`.
