import jpype

from python.src.modelarium.entities.attributes.agent_attribute import AgentAttribute
from python.src.modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from python.src.modelarium.entities.attributes.events.agent_event import _AgentEvent
from python.src.modelarium.entities.attributes.events.environment_event import _EnvironmentEvent
from python.src.modelarium.entities.attributes.properties.agent_property import _AgentProperty
from python.src.modelarium.entities.attributes.properties.environment_property import _EnvironmentProperty
from python.src.modelarium.entities.attributes.routines.agent_routine import _AgentRoutine
from python.src.modelarium.entities.attributes.routines.environment_routine import _EnvironmentRoutine


def _convert_to_python_agent_attribute(java_attribute: object) -> AgentAttribute:
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


def _convert_to_python_environment_attribute(java_attribute: object) -> EnvironmentAttribute:
    if isinstance(
            java_attribute,
            jpype.JClass("modelarium.entities.attributes.events.functional.FunctionalEnvironmentEvent")
    ):
        return _EnvironmentEvent(java_attribute)

    if isinstance(
            java_attribute,
            jpype.JClass("modelarium.entities.attributes.routines.functional.FunctionalEnvironmentRoutine")
    ):
        return _EnvironmentRoutine(java_attribute)

    if isinstance(
            java_attribute,
            jpype.JClass(
                "modelarium.entities.attributes.properties.functional.python.PythonFunctionalEnvironmentProperty")
    ):
        return _EnvironmentProperty(java_attribute)

    raise TypeError(f"Unsupported Java attribute type: {java_attribute.getClass().getName()}")
