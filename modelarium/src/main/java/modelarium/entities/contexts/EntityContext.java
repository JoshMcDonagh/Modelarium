package modelarium.entities.contexts;

/**
 * Interface for marking a context as belonging to an entity.
 *
 * <p>This interface is extended by the entity-flavoured context interfaces: {@link AgentContext} and
 * {@link EnvironmentContext}.
 */
public sealed interface EntityContext extends Context permits AgentContext, EnvironmentContext {
}
