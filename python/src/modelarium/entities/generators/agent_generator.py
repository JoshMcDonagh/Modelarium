from abc import ABC, abstractmethod

import jpype

from modelarium._java import java_class
from modelarium.config import Config, _java_config_to_config
from modelarium.entities.agent_sets.agent_set import AgentSet
from modelarium.entities.generators.entity_generator import EntityGenerator
from modelarium.utils.random_generator import RandomGenerator, _RandomGenerator


class AgentGenerator(ABC, EntityGenerator):
    def __init__(self) -> None:
        super().__init__(
            self._java_generate_agents_for_each_thread,
            "modelarium.entities.generators.FunctionalAgentGenerator",
            "generateAgentsForEachThread",
        )

    def _java_generate_agents_for_each_thread(self, java_config: object, java_random: object) -> object:
        config = _java_config_to_config(java_config)
        random = _RandomGenerator(java_random)
        agent_sets = self.generate_agents_for_each_thread(config, random)
        java_agent_sets = java_class("java.util.ArrayList")()
        for agent_set in agent_sets:
            java_agent_sets.add(agent_set._java_object)
        return java_agent_sets

    @abstractmethod
    def generate_agents_for_each_thread(self, config: Config, random: RandomGenerator) -> list[AgentSet]:
        pass


class _AgentGenerator(AgentGenerator):
    def __init__(self, java_object: object) -> None:
        super().__init__()
        self._java_obj = java_object
