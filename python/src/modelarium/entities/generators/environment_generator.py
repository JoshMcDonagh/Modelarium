from __future__ import annotations

from abc import abstractmethod
from typing import TYPE_CHECKING

from modelarium._java.wrapper import to_python, to_java
from modelarium.entities.generators.entity_generator import EntityGenerator

if TYPE_CHECKING:
    from modelarium.config import Config
    from modelarium.entities.environment import Environment
    from modelarium.utils.random_generator import RandomGenerator


class EnvironmentGenerator(EntityGenerator):
    def __init__(self) -> None:
        super().__init__(
            self._java_generate_environment,
            "modelarium.entities.generators.FunctionalEnvironmentGenerator",
            "generateEnvironment"
        )

    def _java_generate_environment(self, java_config: object, java_random: object) -> object:
        from modelarium.config import Config
        config = to_python(Config, java_config)
        from modelarium.utils.random_generator import RandomGenerator
        random = to_python(RandomGenerator, java_random)
        return to_java(self.generate_environment(config, random))

    @abstractmethod
    def generate_environment(self, config: Config, random: RandomGenerator) -> Environment:
        pass
