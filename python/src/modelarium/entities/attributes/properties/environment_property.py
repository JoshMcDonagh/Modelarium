from abc import ABC
from typing import Generic

from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from python.src.modelarium.entities.attributes.properties.property import PROPERTY_T, Property


class EnvironmentProperty(ABC, Generic[PROPERTY_T], Property[PROPERTY_T], EnvironmentAttribute):
    def __init__(
            self,
            name: str,
            is_logged:bool,
            access_level: AttributeAccessLevel,
            stored_type: type[PROPERTY_T]
    ) -> None:
        super().__init__(
            name,
            is_logged,
            access_level,
            stored_type,
            "modelarium.entities.attributes.properties.functional.python.PythonFunctionalEnvironmentProperty",
            "modelarium.entities.attributes.properties.functional.EnvironmentPropertyGetterFunction",
            "modelarium.entities.attributes.properties.functional.EnvironmentPropertySetterFunction",
            "modelarium.entities.attributes.properties.functional.EnvironmentPropertyRunFunction"
        )


class _EnvironmentProperty(Generic[PROPERTY_T], EnvironmentProperty[PROPERTY_T]):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None, None, None)
        super()._java_obj = java_object
