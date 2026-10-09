from abc import ABC
from typing import Generic

from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from python.src.modelarium.entities.attributes.properties.property import T, Property


class EnvironmentProperty(ABC, Generic[T], Property[T], EnvironmentAttribute):
    def __init__(self, name: str, is_logged:bool, access_level: AttributeAccessLevel) -> None:
        super().__init__(
            name,
            is_logged,
            access_level,
            "modelarium.entities.attributes.properties.functional.FunctionalEnvironmentProperty",
            "modelarium.entities.attributes.properties.functional.EnvironmentPropertyGetterFunction",
            "modelarium.entities.attributes.properties.functional.EnvironmentPropertySetterFunction",
            "modelarium.entities.attributes.properties.functional.EnvironmentPropertyRunFunction"
        )


class _EnvironmentProperty(Generic[T], EnvironmentProperty[T]):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None, None)
        super()._java_obj = java_object
