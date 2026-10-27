from abc import ABC
from typing import Generic, override

from modelarium._java.entities.attributes.properties import java_to_python, python_to_java
from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from modelarium.entities.attributes.properties.property import PROPERTY_T, Property
from modelarium.entities.contexts.environment_context import EnvironmentContext


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
            "modelarium.python.entities.attributes.properties.functional.PythonFunctionalEnvironmentProperty",
            "modelarium.entities.attributes.properties.functional.EnvironmentPropertyGetterFunction",
            "modelarium.entities.attributes.properties.functional.EnvironmentPropertySetterFunction",
            "modelarium.entities.attributes.properties.functional.EnvironmentPropertyRunFunction"
        )

    @override
    def _java_set(self, java_context: object, java_current_value: object, java_new_value: object) -> object:
        context = EnvironmentContext(java_context)
        current_value = java_to_python(self.type, java_current_value)
        new_value = java_to_python(self.type, java_new_value)
        return python_to_java(self.type, self._set(context, current_value, new_value))

    @override
    def _java_get(self, java_context: object, java_current_value: object) -> object:
        context = EnvironmentContext(java_context)
        current_value = java_to_python(self.type, java_current_value)
        return python_to_java(self.type, self._get(context, current_value))

    @override
    def _java_run(self, java_context: object, java_current_value: object) -> object:
        context = EnvironmentContext(java_context)
        current_value = java_to_python(self.type, java_current_value)
        return python_to_java(self.type, self._run(context, current_value))
