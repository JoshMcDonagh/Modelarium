package modelarium.internal;

import java.lang.annotation.*;

/**
 * Marks a package, type or member as internal to the framework.
 *
 * <p>Annotated elements are not part of Modelarium's supported public API and may change without notice between
 * releases. Some internal elements must remain Java-public so framework code in another package can use them.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD })
public @interface Internal {

    /**
     * Returns the message describing why the member is internal.
     *
     * @return the description of the member's internal status
     */
    String value() default "Internal framework element - not part of the supported public API";
}
