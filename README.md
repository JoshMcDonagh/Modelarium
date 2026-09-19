# Modelarium

[![Java CI with Maven](https://github.com/JoshMcDonagh/Modelarium/actions/workflows/maven.yml/badge.svg)](https://github.com/JoshMcDonagh/Modelarium/actions/workflows/maven.yml)
[![Python CI](https://github.com/JoshMcDonagh/Modelarium/actions/workflows/python.yml/badge.svg)](https://github.com/JoshMcDonagh/Modelarium/actions/workflows/python.yml)
[![Maven Central](https://img.shields.io/maven-central/v/dev.modelarium/modelarium)](https://central.sonatype.com/artifact/dev.modelarium/modelarium)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Modelarium** is a modular, extensible, and multithreaded agent-based modelling (ABM) framework. Its Java 21
library provides a flexible architecture for defining agents, environments, and behaviours using composable
attributes, with a Python wrapper under development in the same repository.

## Highlights

- Attribute-based modelling of agents and environments, with properties, events, and routines
- Functional attribute variants, so behaviours can be defined with lambdas rather than subclassing
- Multi-core execution with optional lockstep synchronisation through a co-ordinator thread
- Pluggable schedulers (in-order, random order, or custom)
- Configurable results recording to memory or SQLite
- Seeded, reproducible randomness threaded through the whole model
- Java 21, with a stable automatic module name for module-path compatibility

## Repository structure

The repository is organised by language. The root `pom.xml` remains a thin Maven aggregator for the Java modules:

- **[`java/modelarium/`](java/modelarium/)** - the Java library, published to Maven Central as
  `dev.modelarium:modelarium`. See the [Java library README](java/modelarium/README.md) for concepts, a quickstart,
  and the full configuration reference.
- **[`java/modelarium-examples/`](java/modelarium-examples/)** - worked Java example models built against the library. These are
  compiled and smoke-tested on every build, so they always reflect the current API, and they are never published.
  See the [Java examples README](java/modelarium-examples/README.md) for how to run them.
- **[`python/`](python/)** - the in-development Python wrapper and its independent PyPI packaging configuration.
  Python CI builds it against the Java library from the same repository revision.

## Examples

The examples module contains five complete models. They progress from a conventional compartmental epidemic model
to published classic agent-based models and replication experiments:

| Example | What it demonstrates | Original literature |
| --- | --- | --- |
| [SIR epidemic](java/modelarium-examples/src/main/java/dev/modelarium/examples/sir/) | Agent properties and events, spatial contact, an environment-level prevalence summary, seeded randomness, and multi-core execution. | — |
| [Schelling segregation](java/modelarium-examples/src/main/java/dev/modelarium/examples/schelling_segregation/) | A spatial grid, agent relocation, neighbourhood queries, cross-agent interaction, and aggregate segregation measures. | — |
| [El Farol Bar](java/modelarium-examples/src/main/java/dev/modelarium/examples/el_farol_bar/) | Adaptive decision-making, agent-specific predictor sets, shared attendance history, and environment feedback. | — |
| [Epstein-Axtell Sugarscape](java/modelarium-examples/src/main/java/dev/modelarium/examples/epstein_axtell_sugarscape/) | A suite of replication experiments using a custom scheduler, resource landscapes, death and replacement, and experiment-level CSV output. | [Epstein and Axtell (1996)](https://mitpress.mit.edu/9780262550253/growing-artificial-societies/) |
| [Axelrod cultural dissemination](java/modelarium-examples/src/main/java/dev/modelarium/examples/axelrod_cultural_dissemination/) | An event-driven replication study, spatial cultural interaction, absorbing-state detection, repeated seeded runs, and summary statistics. | [Axelrod (1997)](https://doi.org/10.1177/0022002797041002001) |

Build all modules, then run an example from the repository root:

```bash
mvn -B verify
mvn -pl java/modelarium-examples exec:java \
  -Dexec.mainClass=dev.modelarium.examples.sir.SIRMain
```

Each example reads its default settings from `java/modelarium-examples/src/main/resources`, prints or exports a summary,
and writes detailed results beneath `java/modelarium-examples/output/`. See the
[examples README](java/modelarium-examples/README.md) for every main class, configuration file, expected output, and
shorter smoke-test commands.

## Documentation

- [Java library guide and quickstart](java/modelarium/README.md)
- [Worked Java examples](java/modelarium-examples/README.md)
- [Python wrapper status](python/README.md)
- [API reference (Javadoc)](https://joshmcdonagh.github.io/Modelarium/)
- [Supported public API policy](PUBLIC_API.md)

## Installation (Maven Central)

Modelarium is published to Maven Central. Add it to your `pom.xml`:

```xml
<dependency>
  <groupId>dev.modelarium</groupId>
  <artifactId>modelarium</artifactId>
  <version>2.0.0</version>
</dependency>
```

## Requirements

- Java 21
- Maven
- Python 3.9 or later for Python-wrapper development

## Building from source

Build and test everything (library, examples, and the smoke tests) from the repository root:

```bash
mvn -B verify
```

To work on the library module alone:

```bash
mvn -B test --file java/modelarium/pom.xml
```

Install the library to your local Maven repository:

```bash
mvn -B install --file java/modelarium/pom.xml
```

## License

MIT - see [`LICENSE`](LICENSE).
