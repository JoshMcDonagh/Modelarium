from typing import override

from python.src.modelarium.entities.attributes.agent_attribute import AgentAttribute
from python.src.modelarium.entities.attributes.events.agent_event import AgentEvent, _AgentEvent
from python.src.modelarium.entities.attributes.properties.agent_property import AgentProperty, _AgentProperty
from python.src.modelarium.entities.attributes.routines.agent_routine import AgentRoutine, _AgentRoutine
from python.src.modelarium.entities.attributes.sets import _convert_to_python_agent_attribute
from python.src.modelarium.entities.attributes.sets.read_only.read_only_attribute_set import ReadOnlyAttributeSet


class ReadOnlyAgentAttributeSet(ReadOnlyAttributeSet):
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
