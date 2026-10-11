from abc import ABC, abstractmethod

import jpype

from python.src.modelarium.config import Config
from python.src.modelarium.entities.agent_sets.agent_set import AgentSet
from python.src.modelarium.entities.generators.entity_generator import EntityGenerator
from python.src.modelarium.utils.random_generator import RandomGenerator, _RandomGenerator


class AgentGenerator(ABC, EntityGenerator):
    def __init__(self) -> None:
        super().__init__(
            self._java_generate_agents_for_each_thread,
            "modelarium.entities.generators.FunctionalAgentGenerator",
            "generateAgentsForEachThread",
        )

    def _java_generate_agents_for_each_thread(self, java_config: object, java_random: object) -> object:
        config = java_config # TODO: Update with java to python config converter
        random = _RandomGenerator(java_random)
        agent_sets = self.generate_agents_for_each_thread(config, random)
        java_agent_sets = jpype.JCLass("java.util.ArrayList")()
        for agent_set in agent_sets:
            java_agent_sets.add(agent_set._java_object)
        return java_agent_sets

    @abstractmethod
    def generate_agents_for_each_thread(self, config: Config, random: RandomGenerator) -> list[AgentSet]:
        pass