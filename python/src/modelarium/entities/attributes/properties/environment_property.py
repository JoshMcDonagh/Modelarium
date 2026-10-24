from abc import ABC
from typing import Generic, override

from modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from modelarium.entities.attributes.properties import _java_to_python, _python_to_java
from modelarium.entities.attributes.properties.property import PROPERTY_T, Property
from modelarium.entities.contexts.environment_context import EnvironmentContext
from modelarium.wrapper_object import WrapperObject


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
    def _java_set(self, java_context: object, value: PROPERTY_T) -> None:
        context = EnvironmentContext(java_context)
        return self._set(context, _java_to_python(self.type, value))

    @override
    def _java_get(self, java_context: object) -> PROPERTY_T:
        context = EnvironmentContext(java_context)
        return _python_to_java(self.type, self._get(context))

    @override
    def _java_run(self, java_context: object) -> None:
        context = EnvironmentContext(java_context)
        return self._run(context)


class _EnvironmentProperty(Generic[PROPERTY_T], EnvironmentProperty[PROPERTY_T]):
    def __init__(self, java_object: object) -> None:
        WrapperObject.__init__(self, java_object)
