package modelarium.entities.attributes;

public sealed interface Attribute permits AgentAttribute, AttributeBase, EnvironmentAttribute {
    String name();
    boolean isLogged();
    AttributeAccessLevel accessLevel();
    void run();
}
