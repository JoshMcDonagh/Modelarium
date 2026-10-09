from abc import ABC
from typing import Generic

from python.src.modelarium.entities.attributes.agent_attribute import AgentAttribute
from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.properties.property import T, Property


class AgentProperty(ABC, Generic[T], Property[T], AgentAttribute):
    def __init__(self, name: str, is_logged:bool, access_level: AttributeAccessLevel) -> None:
        super().__init__(
            name,
            is_logged,
            access_level,
            "modelarium.entities.attributes.properties.functional.FunctionalAgentProperty",
            "modelarium.entities.attributes.properties.functional.AgentPropertyGetterFunction",
            "modelarium.entities.attributes.properties.functional.AgentPropertySetterFunction",
            "modelarium.entities.attributes.properties.functional.AgentPropertyRunFunction"
        )


class _AgentProperty(Generic[T], AgentProperty[T]):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None, None)
        super()._java_obj = java_object
