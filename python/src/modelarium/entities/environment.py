from typing import overload, override

import jpype

from python.src.modelarium.entities.contexts import EnvironmentContext
from python.src.modelarium.entities.attributes.events.environment_event import EnvironmentEvent, _EnvironmentEvent
from python.src.modelarium.entities.attributes.properties.environment_property import EnvironmentProperty, \
    _EnvironmentProperty
from python.src.modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
from python.src.modelarium.entities.attributes.routines.environment_routine import EnvironmentRoutine, \
    _EnvironmentRoutine
from python.src.modelarium.entities.entity import Entity


class Environment(Entity):
    @overload
    def __init__(self, attribute_sets: list[object]) -> None:
        ...

    @overload
    def __init__(self, name: str, attribute_sets: list[object]) -> None:
        ...

    def __init__(self, name: str | list[object], attribute_sets: list[object] | None = None) -> None: # TODO: Update to use an EnvironmentAttributeSet wrapper type
        java_object = jpype.JClass("modelarium.entities.Environment")

        if not (
                isinstance(name, str) and isinstance(attribute_sets, list)
                or isinstance(name, list) and attribute_sets is None
        ):
            raise TypeError(
                f"invalid argument type combination given: "
                f"{type(name).__name__}, "
                f"{type(attribute_sets).__name__}"
            )

        if attribute_sets is None:
            super().__init__(java_object(name))
        else:
            super().__init__(java_object(name, attribute_sets))

    @property
    @override
    def context(self) -> EnvironmentContext:
        return EnvironmentContext(self._java_object.context())

    def get_event(self, attribute_set_id: int | str, event_id: int | str) -> EnvironmentEvent:
        return _EnvironmentEvent(self.get_attribute_set(attribute_set_id).get_event(event_id))

    def get_routine(self, attribute_set_id: int | str, routine_id: int | str) -> EnvironmentRoutine:
        return _EnvironmentRoutine(self.get_attribute_set(attribute_set_id).get_routine(routine_id))

    def get_property(self, attribute_set_id: int | str, property_id: int | str) -> EnvironmentProperty:
        return _EnvironmentProperty(self.get_attribute_set(attribute_set_id).get_property(property_id))

    @override
    def get_as_immutable(self) -> ReadOnlyEnvironment:
        return ReadOnlyEnvironment(self._java_object.getAsImmutable())


class _Environment(Environment):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None)
        super()._java_obj = java_object
