from typing import override

from modelarium._java.wrapper import to_python
from modelarium.entities.attributes.sets.read_only.read_only_agent_attribute_set import \
    ReadOnlyAgentAttributeSet
from modelarium.entities.read_only.read_only_entity import ReadOnlyEntity


class ReadOnlyAgent(ReadOnlyEntity):
    @override
    def get_attribute_set(self, attribute_set_identifier: int | str) -> ReadOnlyAgentAttributeSet:
        return to_python(ReadOnlyAgentAttributeSet, self._java_object.getAttributeSet(attribute_set_identifier))

    def is_dead(self) -> bool:
        return self._java_object.isDead()
