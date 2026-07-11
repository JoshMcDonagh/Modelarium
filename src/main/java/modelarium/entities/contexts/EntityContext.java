package modelarium.entities.contexts;

public sealed interface EntityContext extends Context permits AgentContext, EnvironmentContext {
}
