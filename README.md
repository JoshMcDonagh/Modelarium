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
  <version>1.1.2</version>
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

- ModelElement – base concept for agents and environments  
- Attribute / Property / Event – defines state and behaviour run each tick  
- AttributeSet – groups related attributes and controls execution order  
- AttributeSetCollection – attached to a ModelElement; runs all its sets  
- AgentSet – collection of named agents  
- AgentGenerator – defines how agents are created  
- Environment – shared component ticked after agents  
- EnvironmentGenerator – defines how the environment is created  
- ModelScheduler – tick policy (in order, random, or custom)  
- Results – stores raw and processed simulation outputs  

## Quickstart example

A model run is driven by `ModelSettings`. A minimal configuration looks like:

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

The integration tests under `src/test/java/integration/` provide complete, working examples of model setup.

## Results storage

Results can be stored:

- In memory (fastest for iteration)
- On disk using SQLite (useful for large simulations)

Toggle with:

```java
settings.setAreAttributeSetResultsStoredOnDisk(true);
```

## Multithreading and synchronisation

- `numOfCores` controls the worker thread count  
- `areProcessesSynced` enables a coordinated tick progression path  

Different combinations are useful for performance experiments and correctness checks.

## Testing

Run all tests:

```bash
mvn -B test
```

The test suite includes both unit tests and end-to-end integration tests covering threading, schedulers, warm-up behaviour, and results backends.

## License

See `LICENSE`.
