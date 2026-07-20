package modelarium.internal;

import java.lang.annotation.*;

/**
 * Annotation for marking a member as internal to the framework.
 *
 * <p>Members marked with this annotation are used by the framework to wire the model together and are not intended
 * to be called from user-defined attribute or entity code.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD })
public @interface Internal {

    /**
     * Returns the message describing why the member is internal.
     *
     * @return the description of the member's internal status
     */
    String value() default "Internal member - not for callback use";
}
