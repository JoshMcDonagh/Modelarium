from __future__ import annotations

from typing import override, TYPE_CHECKING

from modelarium._java.wrapper import to_python
from modelarium.entities.read_only.read_only_entity import ReadOnlyEntity

if TYPE_CHECKING:
    from modelarium.entities.attributes.sets.read_only.read_only_environment_attribute_set import \
        ReadOnlyEnvironmentAttributeSet


class ReadOnlyEnvironment(ReadOnlyEntity):
    @override
    def get_attribute_set(self, attribute_set_identifier: int | str) -> ReadOnlyEnvironmentAttributeSet:
        from modelarium.entities.attributes.sets.read_only.read_only_environment_attribute_set import \
            ReadOnlyEnvironmentAttributeSet
        return to_python(ReadOnlyEnvironmentAttributeSet, self._java_object.getAttributeSet(attribute_set_identifier))
