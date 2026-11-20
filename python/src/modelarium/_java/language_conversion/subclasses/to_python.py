from __future__ import annotations

from typing import TYPE_CHECKING

from modelarium._java.language_conversion import PYTHON_OBJECT_T

if TYPE_CHECKING:
    from modelarium.entities.entity import Entity
    from modelarium.entities.attributes.agent_attribute import AgentAttribute
    from modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
    from modelarium.entities.attributes.attribute import Attribute
    from modelarium.entities.attributes.sets.attribute_set import AttributeSet
    from modelarium.entities.attributes.sets.read_only.read_only_attribute_set import ReadOnlyAttributeSet
    from modelarium.entities.contexts.context import Context
    from modelarium.entities.read_only.read_only_entity import ReadOnlyEntity
    from modelarium.entities.attributes.events.event import Event
    from modelarium.entities.attributes.properties.property import Property
    from modelarium.entities.attributes.routines.routine import Routine


def _java_entity_subclass_to_python(java_entity: object) -> Entity:
    from modelarium._java import java_class_name_of

    if java_class_name_of(java_entity) == "modelarium.entities.Agent":
        from modelarium._java import to_python
        from modelarium.entities.agent import Agent
        return to_python(Agent, java_entity)

    if java_class_name_of(java_entity) == "modelarium.entities.Environment":
        from modelarium._java import to_python
        from modelarium.entities.environment import Environment
        return to_python(Environment, java_entity)

    raise RuntimeError("Java entity class type could not be converted to python object")


def _java_agent_attribute_subclass_to_python(java_attribute: object) -> AgentAttribute:
    from modelarium._java import java_class_name_of

    if (java_class_name_of(java_attribute) ==
            "modelarium.entities.attributes.events.functional.FunctionalAgentEvent"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.events.agent_event import AgentEvent
        return to_python(AgentEvent, java_attribute)

    if (java_class_name_of(java_attribute) ==
            "modelarium.python.entities.attributes.properties.functional.PythonFunctionalAgentProperty"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.properties.agent_property import AgentProperty
        return to_python(AgentProperty, java_attribute)

    if (java_class_name_of(java_attribute) ==
            "modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.routines.agent_routine import AgentRoutine
        return to_python(AgentRoutine, java_attribute)

    raise RuntimeError("Java agent attribute class type could not be converted to python object")


def _java_environment_attribute_subclass_to_python(java_attribute: object) -> EnvironmentAttribute:
    from modelarium._java import java_class_name_of

    if (java_class_name_of(java_attribute) ==
            "modelarium.entities.attributes.events.functional.FunctionalEnvironmentEvent"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.events.environment_event import EnvironmentEvent
        return to_python(EnvironmentEvent, java_attribute)

    if (java_class_name_of(java_attribute) ==
            "modelarium.entities.attributes.properties.functional.FunctionalEnvironmentProperty"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.properties.environment_property import EnvironmentProperty
        return to_python(EnvironmentProperty, java_attribute)

    if (java_class_name_of(java_attribute) ==
            "modelarium.entities.attributes.routines.functional.FunctionalEnvironmentRoutine"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.routines.environment_routine import EnvironmentRoutine
        return to_python(EnvironmentRoutine, java_attribute)

    raise RuntimeError("Java environment attribute class type could not be converted to python object")


def _java_attribute_subclass_to_python(java_attribute: object) -> Attribute:
    from modelarium._java import java_parent_class_names_of
    java_parent_classes = java_parent_class_names_of(java_attribute)

    if "modelarium.entities.attributes.AgentAttribute" in java_parent_classes:
        return _java_agent_attribute_subclass_to_python(java_attribute)

    if "modelarium.entities.attributes.EnvironmentAttribute" in java_parent_classes:
        return _java_environment_attribute_subclass_to_python(java_attribute)

    raise RuntimeError("Java attribute class type could not be converted to python object")


def _java_event_subclass_to_python(java_event: object) -> Event:
    from modelarium._java import java_class_name_of

    if (java_class_name_of(java_event) ==
            "modelarium.entities.attributes.events.functional.FunctionalAgentEvent"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.events.agent_event import AgentEvent
        return to_python(AgentEvent, java_event)

    if (java_class_name_of(java_event) ==
            "modelarium.entities.attributes.events.functional.FunctionalEnvironmentEvent"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.events.environment_event import EnvironmentEvent
        return to_python(EnvironmentEvent, java_event)

    raise RuntimeError("Java event class type could not be converted to python object")


def _java_property_subclass_to_python(java_property: object) -> Property:
    from modelarium._java import java_class_name_of

    if (java_class_name_of(java_property) ==
            "modelarium.entities.attributes.properties.functional.FunctionalAgentProperty"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.properties.agent_property import AgentProperty
        return to_python(AgentProperty, java_property)

    if (java_class_name_of(java_property) ==
            "modelarium.entities.attributes.properties.functional.FunctionalEnvironmentProperty"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.properties.environment_property import EnvironmentProperty
        return to_python(EnvironmentProperty, java_property)

    raise RuntimeError("Java property class type could not be converted to python object")


def _java_routine_subclass_to_python(java_routine: object) -> Routine:
    from modelarium._java import java_class_name_of

    if (java_class_name_of(java_routine) ==
            "modelarium.entities.attributes.routines.functional.FunctionalAgentRoutine"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.routines.agent_routine import AgentRoutine
        return to_python(AgentRoutine, java_routine)

    if (java_class_name_of(java_routine) ==
            "modelarium.entities.attributes.routines.functional.FunctionalEnvironmentRoutine"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.routines.environment_routine import EnvironmentRoutine
        return to_python(EnvironmentRoutine, java_routine)

    raise RuntimeError("Java routine class type could not be converted to python object")


def _java_attribute_set_subclass_to_python(java_attribute_set: object) -> AttributeSet:
    from modelarium._java import java_class_name_of

    if java_class_name_of(java_attribute_set) == "modelarium.entities.attributes.sets.AgentAttributeSet":
        from modelarium._java import to_python
        from modelarium.entities.attributes.sets.agent_attribute_set import AgentAttributeSet
        return to_python(AgentAttributeSet, java_attribute_set)

    if java_class_name_of(java_attribute_set) == "modelarium.entities.attributes.sets.EnvironmentAttributeSet":
        from modelarium._java import to_python
        from modelarium.entities.attributes.sets.environment_attribute_set import EnvironmentAttributeSet
        return to_python(EnvironmentAttributeSet, java_attribute_set)

    raise RuntimeError("Java attribute set class type could not be converted to python object")


def _java_read_only_attribute_set_subclass_to_python(java_read_only_attribute_set: object) -> ReadOnlyAttributeSet:
    from modelarium._java import java_class_name_of

    if (java_class_name_of(java_read_only_attribute_set)
            == "modelarium.entities.attributes.sets.readonly.ReadOnlyAgentAttributeSet"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.sets.read_only.read_only_agent_attribute_set import ReadOnlyAgentAttributeSet
        return to_python(ReadOnlyAgentAttributeSet, java_read_only_attribute_set)

    if (java_class_name_of(java_read_only_attribute_set)
            == "modelarium.entities.attributes.sets.readonly.ReadOnlyEnvironmentAttributeSet"):
        from modelarium._java import to_python
        from modelarium.entities.attributes.sets.read_only.read_only_environment_attribute_set import ReadOnlyEnvironmentAttributeSet
        return to_python(ReadOnlyEnvironmentAttributeSet, java_read_only_attribute_set)

    raise RuntimeError("Java read only attribute set class type could not be converted to python object")


def _java_context_subclass_to_python(java_context: object) -> Context:
    from modelarium._java import java_class_name_of

    if java_class_name_of(java_context) == "modelarium.entities.contexts.AgentSimulationContext":
        from modelarium._java import to_python
        from modelarium.entities.contexts.agent_context import AgentContext
        return to_python(AgentContext, java_context)

    if java_class_name_of(java_context) == "modelarium.entities.contexts.EnvironmentSimulationContext":
        from modelarium._java import to_python
        from modelarium.entities.contexts.environment_context import EnvironmentContext
        return to_python(EnvironmentContext, java_context)

    raise RuntimeError("Java context class type could not be converted to python object")


def _java_read_only_entity_subclass_to_python(java_read_only_entity: object) -> ReadOnlyEntity:
    from modelarium._java import java_class_name_of

    if java_class_name_of(java_read_only_entity) == "modelarium.entities.readonly.ReadOnlyAgent":
        from modelarium._java import to_python
        from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
        return to_python(ReadOnlyAgent, java_read_only_entity)

    if java_class_name_of(java_read_only_entity) == "modelarium.entities.readonly.ReadOnlyEnvironment":
        from modelarium._java import to_python
        from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
        return to_python(ReadOnlyEnvironment, java_read_only_entity)

    raise RuntimeError("Java read only entity class type could not be converted to python object")


def to_python_subclass_type(python_type: type[PYTHON_OBJECT_T], java_object: object) -> PYTHON_OBJECT_T:
    if python_type is Entity:
        return _java_entity_subclass_to_python(java_object)

    if python_type is AgentAttribute:
        return _java_agent_attribute_subclass_to_python(java_object)

    if python_type is EnvironmentAttribute:
        return _java_environment_attribute_subclass_to_python(java_object)

    if python_type is Attribute:
        return _java_attribute_subclass_to_python(java_object)

    if python_type is Event:
        return _java_event_subclass_to_python(java_object)

    if python_type is Property:
        return _java_property_subclass_to_python(java_object)

    if python_type is Routine:
        return _java_routine_subclass_to_python(java_object)

    if python_type is AttributeSet:
        return _java_attribute_set_subclass_to_python(java_object)

    if python_type is ReadOnlyAttributeSet:
        return _java_read_only_attribute_set_subclass_to_python(java_object)

    if python_type is Context:
        return _java_context_subclass_to_python(java_object)

    if python_type is ReadOnlyEntity:
        return _java_read_only_entity_subclass_to_python(java_object)

    raise RuntimeError(f"Cannot convert {java_object} to {python_type}")
