# Modelarium public API policy

Modelarium distinguishes between the supported public API and the Java-public elements in order to be able to maintain
an intuitive package structure.

- Packages marked `@PublicApi` form the supported API by default.
- A type or member marked `@Internal` is an implementation detail even when Java visibility requires it to be
  `public`.
- Public and protected elements in a `@PublicApi` package are covered by semantic-versioning compatibility guarantees
  unless they are marked `@Internal`.
- Internal elements may change between releases without a major-version increase and must not be called or extended
  by application code.

The public API exposes primarily model configuration and execution, entities, attributes, user-facing contexts,
generators, schedulers, result views, logging backends and user-relevant exceptions.

When using Modelarium, the public API should be adhered to.
