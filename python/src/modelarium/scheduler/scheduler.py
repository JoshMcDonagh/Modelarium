from abc import ABC, abstractmethod
from typing import override

import jpype

from modelarium._java import java_class
from modelarium._java.wrapper import to_python
from modelarium.entities.agent_sets.agent_set import AgentSet
from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
from modelarium.read_only_clock import ReadOnlyClock
from modelarium.utils.random_generator import RandomGenerator
from modelarium._java.wrapper.wrapper_object import WrapperObject


class Scheduler(ABC, WrapperObject):
    def __init__(self):
        tick_function = jpype.JProxy(
            "modelarium.scheduler.functional.TickFunction",
            dict={"runTick": self._java_run_tick}
        )

        super().__init__(java_class("modelarium.scheduler.functional.FunctionalScheduler")(tick_function))

    def _java_run_tick(
            self,
            thread_name: str,
            java_clock: object,
            java_environment: object,
            java_agent_set: object,
            java_random: object
    ):
        self.run_tick(
            thread_name,
            ReadOnlyClock(java_clock),
            ReadOnlyEnvironment(java_environment),
            to_python(AgentSet, java_agent_set),
            to_python(RandomGenerator, java_random)
        )

    @abstractmethod
    def run_tick(
            self,
            thread_name: str,
            clock: ReadOnlyClock,
            environment: ReadOnlyEnvironment,
            agent_set: AgentSet,
            random: RandomGenerator
    ) -> None:
        pass
