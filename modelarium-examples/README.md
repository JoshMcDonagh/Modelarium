# Modelarium Examples

Worked example models built against the [Modelarium](https://github.com/JoshMcDonagh/Modelarium) agent-based
modelling framework. Each example is a self-contained package with a `main` method, and all of them are compiled
and smoke-tested on every build so they always reflect the current API.

## The examples

**Random walk** (`dev.modelarium.examples.randomwalk`) - the smallest useful model: a population of one-dimensional
walkers on a single core, each with a logged `position` property driven by seeded Gaussian steps. Demonstrates the
functional agent generator, an inert environment, and reading logged series from the results.

**SIR contagion** (`dev.modelarium.examples.sir`) - a susceptible/infected/recovered epidemic on a single core.
Demonstrates event triggers, reading and writing a property from event logic, and sampling the population through
the context's filtered agent access.

**Cross-core interaction** (`dev.modelarium.examples.multicore`) - a synchronised two-core model in which every
agent reads its partner's state on the other core through the co-ordinator each tick, plus an environment property
running at the co-ordinator. Demonstrates the synchronisation semantics: remote reads observe the other core's
state as of the previous tick boundary, which makes the run fully deterministic.

## Running an example

From the repository root:

```
mvn -q -pl modelarium-examples -am package
mvn -q -pl modelarium-examples exec:java -Dexec.mainClass=dev.modelarium.examples.randomwalk.RandomWalkExample
```

Substitute the main class for `dev.modelarium.examples.sir.SirExample` or
`dev.modelarium.examples.multicore.CrossCoreInteractionExample` as desired.

## Running the smoke tests

```
mvn -pl modelarium-examples -am test
```
