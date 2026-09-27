from typing import override

import jpype

from python.src.modelarium.entities.entity import Entity


class Agent(Entity):
    def __init__(self, name: str, attribute_sets: list[object]) -> None: # TODO: Add AgentAttributeSet wrapper type
        super().__init__(jpype.JClass("modelarium.entities.Agent")(name, attribute_sets))

    @override
    def get_attribute_set(self, attribute_set_id: int | str): # TODO: Update to return a AgentAttributeSet wrapper type
        super().get_attribute_set(attribute_set_id)

    def get_event(self, attribute_set_id: int | str, event_id: int | str): # TODO: Update to return an AgentEvent wrapper type
        return self.get_attribute_set(attribute_set_id).get_event(event_id)

    def get_routine(self, attribute_set_id: int | str, routine_id: int | str): # TODO: Update to return an AgentRoutine wrapper type
        return self.get_attribute_set(attribute_set_id).get_routine(routine_id)

    def get_property(self, attribute_set_id: int | str, property_id: int | str): # TODO: Update to return an AgentProperty wrapper type
        return self.get_attribute_set(attribute_set_id).get_property(property_id)

    def kill(self) -> None:
        self._java_object.kill()

    @property
    def is_dead(self) -> bool:
        return self._java_object.isDead()

    @override
    def get_as_immutable(self): # TODO: Update to return a ReadOnlyAgent wrapper type
        return self._java_object.getAsImmutable()
