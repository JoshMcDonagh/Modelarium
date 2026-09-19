package modelarium.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a package or element as part of Modelarium's supported public API.
 *
 * <p>Public API elements are covered by the library's semantic-versioning
 * compatibility policy. Public Java elements that are instead marked
 * {@code @Internal} are framework implementation details and are not covered
 * by that compatibility guarantee.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD,
        ElementType.CONSTRUCTOR, ElementType.FIELD })
public @interface PublicApi {
}
