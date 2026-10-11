from abc import ABC, abstractmethod

from python.src.modelarium.config import Config
from python.src.modelarium.entities.environment import Environment
from python.src.modelarium.entities.generators.entity_generator import EntityGenerator
from python.src.modelarium.utils.random_generator import RandomGenerator, _RandomGenerator


class EnvironmentGenerator(ABC, EntityGenerator):
    def __init__(self) -> None:
        super().__init__(
            self._java_generate_environment,
            "modelarium.entities.generators.FunctionalEnvironmentGenerator",
            "generateEnvironment"
        )

    def _java_generate_environment(self, java_config: object, java_random: object) -> object:
        config = java_config  # TODO: Update with java to python config converter
        random = _RandomGenerator(java_random)
        return self.generate_environment(config, random)._java_object

    @abstractmethod
    def generate_environment(self, config: Config, random: RandomGenerator) -> Environment:
        pass
