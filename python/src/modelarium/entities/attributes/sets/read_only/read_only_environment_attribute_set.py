from typing import override

from modelarium.entities.attributes.events.environment_event import _EnvironmentEvent
from modelarium.entities.attributes.events.read_only_event import ReadOnlyEvent
from modelarium.entities.attributes.properties.environment_property import _EnvironmentProperty
from modelarium.entities.attributes.properties.read_only_property import ReadOnlyProperty
from modelarium.entities.attributes.read_only_attribute import ReadOnlyAttribute
from modelarium.entities.attributes.routines.environment_routine import _EnvironmentRoutine
from modelarium.entities.attributes.routines.read_only_routine import ReadOnlyRoutine
from modelarium.entities.attributes.sets import _convert_to_python_environment_attribute
from modelarium.entities.attributes.sets.read_only.read_only_attribute_set import ReadOnlyAttributeSet


class ReadOnlyEnvironmentAttributeSet(ReadOnlyAttributeSet):
    @override
    def get(self, attribute_id: int | str) -> ReadOnlyAttribute:
        return _convert_to_python_environment_attribute(self._java_object.get(attribute_id)).get_as_immutable()

    @override
    def get_event(self, event_id: int | str) -> ReadOnlyEvent:
        return _EnvironmentEvent(self._java_object.getEvent(event_id)).get_as_immutable()

    @override
    def get_routine(self, routine_id: int | str) -> ReadOnlyRoutine:
        return _EnvironmentRoutine(self._java_object.getRoutine(routine_id)).get_as_immutable()

    @override
    def get_property(self, property_id: int | str) -> ReadOnlyProperty:
        return _EnvironmentProperty(self._java_object.getProperty(property_id)).get_as_immutable()
