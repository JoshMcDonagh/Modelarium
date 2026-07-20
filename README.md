# Modelarium

[![Java CI with Maven](https://github.com/JoshMcDonagh/Modelarium/actions/workflows/maven.yml/badge.svg)](https://github.com/JoshMcDonagh/Modelarium/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/dev.modelarium/modelarium)](https://central.sonatype.com/artifact/dev.modelarium/modelarium)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Modelarium** is a modular, extensible, and multithreaded agent-based modelling (ABM) framework for Java. It
provides a flexible architecture for defining agents, environments, and behaviours using composable attributes, and
supports high-performance simulation with optional cross-thread coordination.

## Highlights

- Attribute-based modelling of agents and environments, with properties, events, and routines
- Functional attribute variants, so behaviours can be defined with lambdas rather than subclassing
- Multi-core execution with optional lockstep synchronisation through a co-ordinator thread
- Pluggable schedulers (in-order, random order, or custom)
- Configurable results recording to memory or SQLite
- Seeded, reproducible randomness threaded through the whole model
- Java 21, with full Java Platform Module System support

## Repository structure

This repository contains two Maven modules, built together by a thin aggregator `pom.xml` at the root:

- **[`modelarium/`](modelarium/)** - the library itself, published to Maven Central as
  `dev.modelarium:modelarium`. See the [library README](modelarium/README.md) for concepts, a quickstart, and the
  full configuration reference.
- **[`modelarium-examples/`](modelarium-examples/)** - worked example models built against the library. These are
  compiled and smoke-tested on every build, so they always reflect the current API, and they are never published.
  See the [examples README](modelarium-examples/README.md) for how to run them.

## Documentation

API (Javadoc): https://joshmcdonagh.github.io/Modelarium/

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

## Building from source

Build and test everything (library, examples, and the smoke tests) from the repository root:

```bash
mvn -B verify
```

To work on the library module alone:

```bash
mvn -B test --file modelarium/pom.xml
```

Install the library to your local Maven repository:

```bash
mvn -B install --file modelarium/pom.xml
```

## License

MIT - see [`LICENSE`](LICENSE).
