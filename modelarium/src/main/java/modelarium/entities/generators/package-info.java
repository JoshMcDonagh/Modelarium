/**
 * Provides extension points for constructing a model's agents and environment.
 *
 * <p>Default and functional generators cover common creation and partitioning strategies. Stateful generators may
 * override their protected reset hook so instances remain reusable across runs.
 */
@modelarium.api.PublicApi
package modelarium.entities.generators;
