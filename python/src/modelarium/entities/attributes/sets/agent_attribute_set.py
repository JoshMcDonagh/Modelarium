from typing import override

from python.src.modelarium.entities.attributes.agent_attribute import AgentAttribute
from python.src.modelarium.entities.attributes.attribute import Attribute
from python.src.modelarium.entities.attributes.events.agent_event import AgentEvent, _AgentEvent
from python.src.modelarium.entities.attributes.properties.agent_property import AgentProperty, _AgentProperty
from python.src.modelarium.entities.attributes.routines.agent_routine import AgentRoutine, _AgentRoutine
from python.src.modelarium.entities.attributes.sets.attribute_set import AttributeSet


class AgentAttributeSet(AttributeSet):
    def __init__(self, name: str, attributes: list[Attribute]):
        super().__init__(name, attributes, "modelarium.entities.attributes.sets.AgentAttributeSet")

    @override
    def get(self, attribute_id: int | str) -> AgentAttribute:
        return self._java_object.get(attribute_id) # TODO: Work out how you're going to make this a python AgentAttribute instance

    @override
    def get_event(self, event_id: int | str) -> AgentEvent:
        return _AgentEvent(self._java_object.getEvent(event_id))

    @override
    def get_routine(self, routine_id: int | str) -> AgentRoutine:
        return _AgentRoutine(self._java_object.getRoutine(routine_id))

    @override
    def get_property(self, property_id: int | str) -> AgentProperty:
        return _AgentProperty(self._java_object.getProperty(property_id))
