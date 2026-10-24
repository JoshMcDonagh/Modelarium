from typing import override

from modelarium.entities.attributes.agent_attribute import AgentAttribute
from modelarium.entities.attributes.events.agent_event import AgentEvent, _AgentEvent
from modelarium.entities.attributes.properties.agent_property import AgentProperty, _AgentProperty
from modelarium.entities.attributes.routines.agent_routine import AgentRoutine, _AgentRoutine
from modelarium.entities.attributes.sets import _convert_to_python_agent_attribute
from modelarium.entities.attributes.sets.attribute_set import AttributeSet


class AgentAttributeSet(AttributeSet):
    def __init__(self, name: str, attributes: list[AgentAttribute]) -> None:
        super().__init__(name, attributes, "modelarium.entities.attributes.sets.AgentAttributeSet")

    @override
    def get(self, attribute_id: int | str) -> AgentAttribute:
        return _convert_to_python_agent_attribute(self._java_object.get(attribute_id))

    @override
    def get_event(self, event_id: int | str) -> AgentEvent:
        return _AgentEvent(self._java_object.getEvent(event_id))

    @override
    def get_routine(self, routine_id: int | str) -> AgentRoutine:
        return _AgentRoutine(self._java_object.getRoutine(routine_id))

    @override
    def get_property(self, property_id: int | str) -> AgentProperty:
        return _AgentProperty(self._java_object.getProperty(property_id))


class _AgentAttributeSet(AgentAttributeSet):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None)
        self._java_obj = java_object
