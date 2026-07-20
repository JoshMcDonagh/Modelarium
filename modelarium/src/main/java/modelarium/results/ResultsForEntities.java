package modelarium.results;

/**
 * Interface for marking a results view as belonging to a kind of entity.
 *
 * <p>This interface is extended by the entity-flavoured results interfaces: {@link ResultsForAgents} and
 * {@link ResultsForEnvironment}.
 */
public sealed interface ResultsForEntities permits ResultsForAgents, ResultsForEnvironment {
}
