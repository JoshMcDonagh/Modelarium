from abc import ABC, abstractmethod

from modelarium.config import Config, _java_config_to_config
from modelarium.entities.environment import Environment
from modelarium.entities.generators.entity_generator import EntityGenerator
from modelarium.utils.random_generator import RandomGenerator, _RandomGenerator


class EnvironmentGenerator(ABC, EntityGenerator):
    def __init__(self) -> None:
        super().__init__(
            self._java_generate_environment,
            "modelarium.entities.generators.FunctionalEnvironmentGenerator",
            "generateEnvironment"
        )

    def _java_generate_environment(self, java_config: object, java_random: object) -> object:
        config = _java_config_to_config(java_config)
        random = _RandomGenerator(java_random)
        return self.generate_environment(config, random)._java_object

    @abstractmethod
    def generate_environment(self, config: Config, random: RandomGenerator) -> Environment:
        pass


class _EnvironmentGenerator(EnvironmentGenerator):
    def __init__(self, java_object: object) -> None:
        super().__init__()
        self._java_obj = java_object
