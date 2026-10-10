from typing import override

import jpype

from python.src.modelarium.entities.contexts import AgentContext
from python.src.modelarium.entities.attributes.events.agent_event import AgentEvent, _AgentEvent
from python.src.modelarium.entities.attributes.properties.agent_property import AgentProperty, _AgentProperty
from python.src.modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
from python.src.modelarium.entities.attributes.routines.agent_routine import AgentRoutine, _AgentRoutine
from python.src.modelarium.entities.entity import Entity


class Agent(Entity):
    def __init__(self, name: str, attribute_sets: list[object]) -> None: # TODO: Add AgentAttributeSet wrapper type
        super().__init__(jpype.JClass("modelarium.entities.Agent")(name, attribute_sets))

    @property
    @override
    def context(self) -> AgentContext:
        return AgentContext(self._java_object.context())

    @override
    def get_attribute_set(self, attribute_set_id: int | str): # TODO: Update to return a AgentAttributeSet wrapper type
        return super().get_attribute_set(attribute_set_id)

    def get_event(self, attribute_set_id: int | str, event_id: int | str) -> AgentEvent:
        return _AgentEvent(self.get_attribute_set(attribute_set_id).get_event(event_id))

    def get_routine(self, attribute_set_id: int | str, routine_id: int | str) -> AgentRoutine:
        return _AgentRoutine(self.get_attribute_set(attribute_set_id).get_routine(routine_id))

    def get_property(self, attribute_set_id: int | str, property_id: int | str) -> AgentProperty:
        return _AgentProperty(self.get_attribute_set(attribute_set_id).get_property(property_id))

    def kill(self) -> None:
        self._java_object.kill()

    @property
    def is_dead(self) -> bool:
        return self._java_object.isDead()

    @override
    def get_as_immutable(self) -> ReadOnlyAgent:
        return ReadOnlyAgent(self._java_object.getAsImmutable())


class _Agent(Agent):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None)
        super()._java_obj = java_object
