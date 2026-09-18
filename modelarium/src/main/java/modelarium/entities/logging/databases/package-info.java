/**
 * Provides memory- and disk-backed storage for attribute log series.
 *
 * <p>Both implementations share connection, missing-series, null-handling, type-enforcement, copying, and
 * disconnect semantics as documented by {@link modelarium.entities.logging.databases.AttributeSetLogDatabase}.
 */
@modelarium.api.PublicApi
package modelarium.entities.logging.databases;
