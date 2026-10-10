from typing import override

import jpype

from python.src.modelarium.entities.attributes.agent_attribute import AgentAttribute
from python.src.modelarium.entities.attributes.events.agent_event import AgentEvent, _AgentEvent
from python.src.modelarium.entities.attributes.properties.agent_property import AgentProperty, _AgentProperty
from python.src.modelarium.entities.attributes.routines.agent_routine import AgentRoutine, _AgentRoutine
from python.src.modelarium.entities.attributes.sets.attribute_set import AttributeSet


class AgentAttributeSet(AttributeSet):
    def __init__(self, name: str, attributes: list[AgentAttribute]):
        super().__init__(name, attributes, "modelarium.entities.attributes.sets.AgentAttributeSet")

    @override
    def get(self, attribute_id: int | str) -> AgentAttribute:
        java_attribute = self._java_object.get(attribute_id)

        if isinstance(
                java_attribute,
                jpype.JClass("modelarium.entities.attributes.events.functional.FunctionalAgentEvent")
        ):
            return _AgentEvent(java_attribute)

        if isinstance(
                java_attribute,
                jpype.JClass("modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine")
        ):
            return _AgentRoutine(java_attribute)

        if isinstance(
                java_attribute,
                jpype.JClass("modelarium.entities.attributes.properties.functional.python.PythonFunctionalAgentProperty")
        ):
            return _AgentProperty(java_attribute)

        raise TypeError(f"Unsupported Java attribute type: {java_attribute.getClass().getName()}")

    @override
    def get_event(self, event_id: int | str) -> AgentEvent:
        return _AgentEvent(self._java_object.getEvent(event_id))

    @override
    def get_routine(self, routine_id: int | str) -> AgentRoutine:
        return _AgentRoutine(self._java_object.getRoutine(routine_id))

    @override
    def get_property(self, property_id: int | str) -> AgentProperty:
        return _AgentProperty(self._java_object.getProperty(property_id))
