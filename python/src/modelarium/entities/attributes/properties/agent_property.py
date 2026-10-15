from abc import ABC
from typing import Generic, override

from python.src.modelarium.entities.attributes.agent_attribute import AgentAttribute
from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel
from python.src.modelarium.entities.attributes.properties import _java_to_python, _python_to_java
from python.src.modelarium.entities.attributes.properties.property import PROPERTY_T, Property
from python.src.modelarium.entities.contexts.agent_context import AgentContext


class AgentProperty(ABC, Generic[PROPERTY_T], Property[PROPERTY_T], AgentAttribute):
    def __init__(
            self,
            name: str,
            is_logged: bool,
            access_level: AttributeAccessLevel,
            stored_type: type[PROPERTY_T]
    ) -> None:
        super().__init__(
            name,
            is_logged,
            access_level,
            stored_type,
            "modelarium.entities.attributes.properties.functional.python.PythonFunctionalAgentProperty",
            "modelarium.entities.attributes.properties.functional.AgentPropertyGetterFunction",
            "modelarium.entities.attributes.properties.functional.AgentPropertySetterFunction",
            "modelarium.entities.attributes.properties.functional.AgentPropertyRunFunction"
        )

    @override
    def _java_set(self, java_context: object, value: PROPERTY_T) -> None:
        context = AgentContext(java_context)
        return self._set(context, _java_to_python(self.type, value))

    @override
    def _java_get(self, java_context: object) -> PROPERTY_T:
        context = AgentContext(java_context)
        return _python_to_java(self.type, self._get(context))

    @override
    def _java_run(self, java_context: object) -> None:
        context = AgentContext(java_context)
        return self._run(context)


class _AgentProperty(Generic[PROPERTY_T], AgentProperty[PROPERTY_T]):
    def __init__(self, java_object: object) -> None:
        super().__init__(None, None, None, None)
        super()._java_obj = java_object
