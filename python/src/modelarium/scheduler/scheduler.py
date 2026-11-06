from __future__ import annotations

from abc import ABC, abstractmethod
from typing import TYPE_CHECKING

import jpype

from modelarium._java import java_class
from modelarium._java.wrapper import to_python
from modelarium._java.wrapper.wrapper_object import WrapperObject

if TYPE_CHECKING:
    from modelarium.read_only_clock import ReadOnlyClock
    from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
    from modelarium.entities.agent_sets.agent_set import AgentSet
    from modelarium.utils.random_generator import RandomGenerator


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
        from modelarium.read_only_clock import ReadOnlyClock
        from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
        from modelarium.entities.agent_sets.agent_set import AgentSet
        from modelarium.utils.random_generator import RandomGenerator
        self.run_tick(
            thread_name,
            to_python(ReadOnlyClock, java_clock),
            to_python(ReadOnlyEnvironment, java_environment),
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
