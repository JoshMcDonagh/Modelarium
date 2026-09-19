package modelarium.entities.attributes;

/**
 * Enum representing the access levels an attribute can have, determining whether other entities may read it.
 */
public enum AttributeAccessLevel {

    /**
     * Indicates that the attribute can be read by other entities.
     */
    PUBLIC,

    /**
     * Indicates that the attribute can only be accessed by the entity that owns it.
     */
    PRIVATE
}
