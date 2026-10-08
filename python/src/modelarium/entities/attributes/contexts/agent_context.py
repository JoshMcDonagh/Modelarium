from abc import ABC
from typing import override

from python.src.modelarium.entities.agent import Agent
from python.src.modelarium.entities.attributes.contexts.context import Context


class AgentContext(ABC, Context):
    @override
    def get_this_entity(self) -> Agent:
        pass

    @override
    def get_this_attribute_set(self) -> object:  # TODO: Update with AgentAttributeSet type hint
        pass

    @override
    def get_environment(self) -> object:  # TODO: Update with ReadOnlyEnvironment
        pass
