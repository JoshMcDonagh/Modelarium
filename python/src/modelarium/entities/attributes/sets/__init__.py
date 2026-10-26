from typing import Any, cast

from modelarium._java import java_class
from modelarium.entities.attributes.agent_attribute import AgentAttribute
from modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from modelarium.entities.attributes.events.agent_event import _AgentEvent
from modelarium.entities.attributes.events.environment_event import _EnvironmentEvent
from modelarium.entities.attributes.properties.agent_property import _AgentProperty
from modelarium.entities.attributes.properties.environment_property import _EnvironmentProperty
from modelarium.entities.attributes.routines.agent_routine import _AgentRoutine
from modelarium.entities.attributes.routines.environment_routine import _EnvironmentRoutine


def _convert_to_python_agent_attribute(java_attribute: object) -> AgentAttribute:
    if isinstance(
            java_attribute,
            java_class("modelarium.entities.attributes.events.AgentEvent")
    ):
        return _AgentEvent(java_attribute)

    if isinstance(
            java_attribute,
            java_class("modelarium.entities.attributes.routines.AgentRoutine")
    ):
        return _AgentRoutine(java_attribute)

    if isinstance(
            java_attribute,
            java_class("modelarium.python.entities.attributes.properties.AgentProperty")
    ):
        return _AgentProperty(java_attribute)

    raise TypeError(f"Unsupported Java attribute type: {cast(Any, java_attribute).getClass().getName()}")


def _convert_to_python_environment_attribute(java_attribute: object) -> EnvironmentAttribute:
    if isinstance(
            java_attribute,
            java_class("modelarium.entities.attributes.events.EnvironmentEvent")
    ):
        return _EnvironmentEvent(java_attribute)

    if isinstance(
            java_attribute,
            java_class("modelarium.entities.attributes.routines.EnvironmentRoutine")
    ):
        return _EnvironmentRoutine(java_attribute)

    if isinstance(
            java_attribute,
            java_class(
                "modelarium.python.entities.attributes.properties.EnvironmentProperty")
    ):
        return _EnvironmentProperty(java_attribute)

    raise TypeError(f"Unsupported Java attribute type: {cast(Any, java_attribute).getClass().getName()}")
