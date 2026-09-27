from typing import overload, override

import jpype

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
                f"invalid argument type combination given: {type(name).__name__}, {type(attribute_sets).__name__}"
            )

        if attribute_sets is None:
            super().__init__(java_object(name))
        else:
            super().__init__(java_object(name, attribute_sets))

    def get_event(self, attribute_set_id: int | str, event_id: int | str): # TODO: Update to return an EnvironmentEvent wrapper type
        return self.get_attribute_set(attribute_set_id).get_event(event_id)

    def get_routine(self, attribute_set_id: int | str, routine_id: int | str): # TODO: Update to return an EnvironmentRoutine wrapper type
        return self.get_attribute_set(attribute_set_id).get_routine(routine_id)

    def get_property(self, attribute_set_id: int | str, property_id: int | str): # TODO: Update to return an EnvironmentProperty wrapper type
        return self.get_attribute_set(attribute_set_id).get_property(property_id)

    @override
    def get_as_immutable(self): # TODO: Update to return a ReadOnlyEnvironment wrapper type
        return self._java_object.getAsImmutable()