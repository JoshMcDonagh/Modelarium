from __future__ import annotations

from typing import override, TYPE_CHECKING

from modelarium._java.wrapper import to_python
from modelarium.entities.contexts.context import Context

if TYPE_CHECKING:
    from modelarium.entities.environment import Environment
    from modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
    from modelarium.entities.attributes.sets.environment_attribute_set import EnvironmentAttributeSet


class EnvironmentContext(Context):
    @override
    def get_this_attribute(self) -> EnvironmentAttribute:
        if (self._java_class_name ==
                "modelarium.entities.attributes.events.functional.FunctionalEnvironmentEvent"):
            from modelarium.entities.attributes.events.environment_event import EnvironmentEvent
            return to_python(EnvironmentEvent, self._java_object.getThisAttribute())

        if (self._java_class_name ==
                "modelarium.python.entities.attributes.properties.functional.PythonFunctionalEnvironmentProperty"):
            from modelarium.entities.attributes.properties.environment_property import EnvironmentProperty
            return to_python(EnvironmentProperty, self._java_object.getThisAttribute())

        if (self._java_class_name ==
                "modelarium.entities.attributes.routines.functional.FunctionalEnvironmentRoutine"):
            from modelarium.entities.attributes.routines.environment_routine import EnvironmentRoutine
            return to_python(EnvironmentRoutine, self._java_object.getThisAttribute())

        raise RuntimeError("Java attribute class type could not be converted to python object")

    @override
    def get_this_entity(self) -> Environment:
        from modelarium.entities.environment import Environment
        return to_python(Environment, self._java_object.getThisEntity())

    @override
    def get_this_attribute_set(self) -> EnvironmentAttributeSet:
        from modelarium.entities.attributes.sets.environment_attribute_set import EnvironmentAttributeSet
        return to_python(EnvironmentAttributeSet, self._java_object.getThisAttributeSet())
