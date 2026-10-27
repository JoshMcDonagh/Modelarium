from typing import override

from modelarium._java.wrapper import to_python
from modelarium.entities.attributes.sets.read_only.read_only_environment_attribute_set import \
    ReadOnlyEnvironmentAttributeSet
from modelarium.entities.read_only.read_only_entity import ReadOnlyEntity


class ReadOnlyEnvironment(ReadOnlyEntity):
    @override
    def get_attribute_set(self, attribute_set_identifier: int | str) -> ReadOnlyEnvironmentAttributeSet:
        return to_python(ReadOnlyEnvironmentAttributeSet, self._java_object.getAttributeSet(attribute_set_identifier))
