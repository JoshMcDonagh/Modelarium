package dev.modelarium.examples.schelling_segregation.entities.agents.attributes.location;

/**
 * A request by an agent to occupy a currently vacant cell.
 *
 * <p>The random priority is generated when the proposal is made. If several agents propose the same destination,
 * the lowest priority wins, with agent name used as a deterministic tie-breaker.
 */
public record MoveProposal(Cell target, long priority) {}
