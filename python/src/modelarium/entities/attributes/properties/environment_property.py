from abc import ABC
from typing import Generic, override

from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from python.src.modelarium.entities.attributes.properties.property import PROPERTY_T, Property
from python.src.modelarium.entities.contexts.environment_context import EnvironmentContext


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

    @override
    def _java_set(self, java_context: object, value: PROPERTY_T) -> None:
        context = EnvironmentContext(java_context)
        return self._set(context, value)

    @override
    def _java_get(self, java_context: object) -> PROPERTY_T:
        context = EnvironmentContext(java_context)
        return self._get(context)

    @override
    def _java_run(self, java_context: object) -> None:
        context = EnvironmentContext(java_context)
        return self._run(context)


class _EnvironmentProperty(Generic[PROPERTY_T], EnvironmentProperty[PROPERTY_T]):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None, None, None)
        super()._java_obj = java_object
