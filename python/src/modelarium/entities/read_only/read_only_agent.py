from typing import override

from python.src.modelarium.entities.attributes.sets.read_only.read_only_agent_attribute_set import \
    ReadOnlyAgentAttributeSet
from python.src.modelarium.entities.read_only.read_only_entity import ReadOnlyEntity


class ReadOnlyAgent(ReadOnlyEntity):
    @override
    def get_attribute_set(self, attribute_set_identifier: int | str) -> ReadOnlyAgentAttributeSet:
        return ReadOnlyAgentAttributeSet(self._java_object.getAttributeSet(attribute_set_identifier))

    def is_dead(self) -> bool:
        return self._java_object.isDead()
