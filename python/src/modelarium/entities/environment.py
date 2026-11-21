from __future__ import annotations

from typing import overload, override, TYPE_CHECKING

from modelarium.entities.entity import Entity

if TYPE_CHECKING:
    from modelarium.entities.attributes.events.environment_event import EnvironmentEvent
    from modelarium.entities.attributes.properties.environment_property import EnvironmentProperty
    from modelarium.entities.attributes.routines.environment_routine import EnvironmentRoutine
    from modelarium.entities.attributes.sets.environment_attribute_set import EnvironmentAttributeSet
    from modelarium.entities.contexts.environment_context import EnvironmentContext
    from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment


class Environment(Entity):
    @overload
    def __init__(self, attribute_sets: list[EnvironmentAttributeSet]) -> None:
        ...

    @overload
    def __init__(self, name: str, attribute_sets: list[EnvironmentAttributeSet]) -> None:
        ...

    def __init__(
            self,
            name: str | list[EnvironmentAttributeSet],
            attribute_sets: list[EnvironmentAttributeSet] | None = None
    ) -> None:
        if not (
                isinstance(name, str) and isinstance(attribute_sets, list)
                or isinstance(name, list) and attribute_sets is None
        ):
            raise TypeError(
                f"invalid argument type combination given: "
                f"{type(name).__name__}, "
                f"{type(attribute_sets).__name__}"
            )

        from modelarium._java import java_class
        java_object = java_class("modelarium.entities.Environment")

        from modelarium._java.objects.wrapper_object import WrapperObject
        from modelarium._java import to_java
        if attribute_sets is None:
            WrapperObject.__init__(self, java_object(to_java(name)))
        else:
            WrapperObject.__init__(self, java_object(to_java(attribute_sets)))

    @property
    @override
    def context(self) -> EnvironmentContext:
        from modelarium.entities.contexts.environment_context import EnvironmentContext
        from modelarium._java import to_python
        return to_python(EnvironmentContext, self._java_object.context())

    @override
    def get_attribute_set(self, attribute_set_id: int | str) -> EnvironmentAttributeSet:
        from modelarium.entities.attributes.sets.environment_attribute_set import EnvironmentAttributeSet
        from modelarium._java import to_python
        return to_python(EnvironmentAttributeSet, self._java_object.getAttributeSet(attribute_set_id))

    def get_event(self, attribute_set_id: int | str, event_id: int | str) -> EnvironmentEvent:
        return self.get_attribute_set(attribute_set_id).get_event(event_id)

    def get_routine(self, attribute_set_id: int | str, routine_id: int | str) -> EnvironmentRoutine:
        return self.get_attribute_set(attribute_set_id).get_routine(routine_id)

    def get_property(self, attribute_set_id: int | str, property_id: int | str) -> EnvironmentProperty:
        return self.get_attribute_set(attribute_set_id).get_property(property_id)

    @override
    def get_as_immutable(self) -> ReadOnlyEnvironment:
        from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
        from modelarium._java import to_python
        return to_python(ReadOnlyEnvironment, self._java_object.getAsImmutable())
