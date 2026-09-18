# Modelarium examples

This module contains complete, runnable models built against the current Modelarium API. The examples are part of
the repository build but are not published to Maven Central. Each one has a reduced smoke test which loads its
configuration, runs the model, checks important invariants, and exercises result export.

## Requirements

- Java 21
- Maven 3.9 or later

Run all commands below from the repository root. Build the library and examples first:

```bash
mvn -B verify
```

## Choosing an example

| Example | Start here to learn about | Default workload |
| --- | --- | --- |
| [SIR epidemic](#sir-epidemic) | Properties, events, routines, spatial contact, environment metrics, and multi-core execution | 5,100 agents for 800 ticks |
| [Schelling segregation](#schelling-segregation) | Grid-based movement, neighbourhood calculations, and agent interaction | 360 agents for 100 ticks |
| [El Farol Bar](#el-farol-bar) | Adaptive decisions, predictor selection, shared history, and aggregate feedback | 100 agents for 100 weeks |
| [Epstein-Axtell Sugarscape](#epstein-axtell-sugarscape) | Custom scheduling, death/replacement, landscapes, and multi-run experiments | Eight experiment families |
| [Axelrod cultural dissemination](#axelrod-cultural-dissemination) | Event-driven simulation, absorbing states, replication, and statistical summaries | 100 independent runs |

The default Sugarscape and Axelrod experiments do considerably more work than the first three examples. Their smoke
tests use reduced settings and are a quicker way to verify the implementations.

## Running an example

Use Maven's `exec:java` goal and select the example's main class:

```bash
mvn -pl modelarium-examples exec:java \
  -Dexec.mainClass=dev.modelarium.examples.sir.SIRMain
```

On Windows PowerShell, enter the command on one line or use PowerShell's backtick instead of `\` for continuation.

The configuration files are classpath resources under
[`src/main/resources/dev/modelarium/examples/`](src/main/resources/dev/modelarium/examples/). Edit a configuration
file and rebuild before rerunning an example. Generated files are written beneath `modelarium-examples/output/`,
which is intentionally ignored by Git.

## SIR epidemic

Main class:
`dev.modelarium.examples.sir.SIRMain`

Configuration:
[`sir-config.json`](src/main/resources/dev/modelarium/examples/sir/sir-config.json)

This spatial susceptible-infectious-recovered model creates agents with a location and an SIR state. Agents move,
become infected after contact with infectious neighbours, and recover probabilistically. An environment routine
derives prevalence measures from the population. The example demonstrates:

- custom agent and environment generators;
- typed properties, infection and recovery events, and an environment routine;
- cross-agent spatial queries in synchronised multi-core mode;
- a random-order scheduler and a fixed seed; and
- environment-level prevalence logging.

Results are exported to `modelarium-examples/output/sir/`.

## Schelling segregation

Main class:
`dev.modelarium.examples.schelling_segregation.SchellingSegregationMain`

Configuration:
[`schelling-segregation-config.json`](src/main/resources/dev/modelarium/examples/schelling_segregation/schelling-segregation-config.json)

Agents occupy unique cells on a rectangular grid and belong to one of two groups. They measure the composition of
their neighbourhood and dissatisfied agents propose moves to vacant cells. The example demonstrates:

- representing a bounded spatial environment;
- querying neighbouring agents and coordinating relocation;
- combining derived properties with a relocation routine;
- validating configuration before starting a run; and
- calculating and printing segregation summaries from immutable results.

Results are exported to `modelarium-examples/output/schelling_segregation/`.

## El Farol Bar

Main class:
`dev.modelarium.examples.el_farol_bar.ElFarolBarMain`

Configuration:
[`el-farol-bar-config.json`](src/main/resources/dev/modelarium/examples/el_farol_bar/el-farol-bar-config.json)

Each agent owns a sample of forecasting strategies, selects the strategy that has recently performed best, predicts
attendance, and attends only when it expects the bar not to be crowded. The resulting attendance becomes public
history for the next decision. The example demonstrates:

- heterogeneous per-agent strategy collections;
- decision routines and logged intermediate properties;
- feedback between individual decisions and an environment aggregate;
- synchronised execution across two worker cores; and
- post-run aggregation and burn-in-aware summary statistics.

Results are exported to `modelarium-examples/output/el_farol_bar/`.

## Epstein-Axtell Sugarscape

Main class:
`dev.modelarium.examples.epstein_axtell_sugarscape.EpsteinAxtellSugarscapeMain`

Configuration:
[`sugarscape-config.json`](src/main/resources/dev/modelarium/examples/epstein_axtell_sugarscape/sugarscape-config.json)

Landscape:
[`sugar-map.txt`](src/main/resources/dev/modelarium/examples/epstein_axtell_sugarscape/sugar-map.txt)

This example implements a set of experiments from Epstein and Axtell's Sugarscape model rather than a single run.
The suite covers immediate growback, selection, carrying capacity, wealth, neighbour networks, waves, seasons, and
pollution. It demonstrates:

- using a custom scheduler for model-specific update semantics;
- creating runs from explicit experiment specifications;
- agent death and replacement;
- loading a fixed resource landscape;
- running reproducible experiment families from a base seed; and
- exporting both raw Modelarium results and experiment-level CSV summaries.

Results are exported to `modelarium-examples/output/epstein_axtell_sugarscape/`. The landscape format is described
in [`LANDSCAPE_README.txt`](src/main/resources/dev/modelarium/examples/epstein_axtell_sugarscape/LANDSCAPE_README.txt).

## Axelrod cultural dissemination

Main class:
`dev.modelarium.examples.axelrod_cultural_dissemination.AxelrodCulturalDisseminationMain`

Configuration:
[`axelrod-cultural-dissemination-config.json`](src/main/resources/dev/modelarium/examples/axelrod_cultural_dissemination/axelrod-cultural-dissemination-config.json)

Agents occupy a grid and carry a vector of cultural features. Neighbours interact with probability proportional to
their similarity, after which one differing feature may be copied. Each run stops at an absorbing state or at its
safety limit. The default configuration reproduces Axelrod's 10-by-10, five-feature, ten-trait setup over 100 seeded
runs. The example demonstrates:

- a custom event scheduler;
- model-specific stopping conditions within a bounded Modelarium run;
- spatial interaction and population-level cultural metrics;
- independent reproducible replications; and
- experiment summaries alongside one representative raw result export.

Results are exported to `modelarium-examples/output/axelrod_cultural_dissemination/`.

## Running the example tests

Run every library test and example smoke test:

```bash
mvn -B verify
```

Run only the examples module and its dependencies:

```bash
mvn -B -pl modelarium-examples -am test
```

Run one smoke-test class while still building the required library module:

```bash
mvn -B -pl modelarium-examples -am \
  -Dtest=dev.modelarium.examples.smoke.SIRSmokeTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

The other smoke-test class names are `SchellingSegregationSmokeTest`, `ElFarolBarSmokeTest`,
`EpsteinAxtellSugarscapeSmokeTest`, and `AxelrodCulturalDisseminationSmokeTest`.

## Adding another example

Keep a new model self-contained beneath `dev.modelarium.examples.<example_name>` and place its default resources in
the matching resource package. Add a reduced smoke test under `dev.modelarium.examples.smoke` which verifies at
least configuration loading, a complete run, important model invariants, and result export. Avoid tests which run
the example's full research-scale defaults.
