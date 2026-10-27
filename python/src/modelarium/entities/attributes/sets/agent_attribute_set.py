from typing import override

from modelarium._java.wrapper import to_python
from modelarium.entities.attributes.agent_attribute import AgentAttribute
from modelarium.entities.attributes.events.agent_event import AgentEvent
from modelarium.entities.attributes.properties.agent_property import AgentProperty
from modelarium.entities.attributes.routines.agent_routine import AgentRoutine
from modelarium.entities.attributes.sets.attribute_set import AttributeSet


class AgentAttributeSet(AttributeSet):
    def __init__(self, name: str, attributes: list[AgentAttribute]) -> None:
        super().__init__(name, attributes, "modelarium.entities.attributes.sets.AgentAttributeSet")

    @override
    def get(self, attribute_id: int | str) -> AgentAttribute:
        return to_python(AgentAttribute, self._java_object.get(attribute_id))

    @override
    def get_event(self, event_id: int | str) -> AgentEvent:
        return to_python(AgentEvent, self._java_object.getEvent(event_id))

    @override
    def get_routine(self, routine_id: int | str) -> AgentRoutine:
        return to_python(AgentRoutine, self._java_object.getRoutine(routine_id))

    @override
    def get_property(self, property_id: int | str) -> AgentProperty:
        return to_python(AgentProperty, self._java_object.getProperty(property_id))
