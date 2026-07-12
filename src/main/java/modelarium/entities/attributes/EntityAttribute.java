package modelarium.entities.attributes;

public sealed interface EntityAttribute permits AgentAttribute, EnvironmentAttribute {
    String name();
    boolean isLogged();
    AttributeAccessLevel accessLevel();
    void run();
}
