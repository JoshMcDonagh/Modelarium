/**
 * Environment abstractions for Modelarium simulations.
 *
 * <p>This package defines the concepts used to represent the shared context
 * in which agents operate. Environments typically model global or spatial
 * state, mediate interactions between agents, and provide access to
 * external conditions that influence agent behaviour.</p>
 *
 * <p>Unlike agents, environments do not encapsulate autonomous behaviour.
 * Instead, they act as structured state containers and interaction surfaces
 * that are read from and written to by agents and simulation logic.</p>
 *
 * <p>Users may extend environment implementations to model domain-specific
 * contexts such as spatial layouts, resource distributions, or global
 * constraints.</p>
 */
package modelarium.environments;
