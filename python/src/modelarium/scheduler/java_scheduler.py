from __future__ import annotations

from abc import ABC
from typing import override, TYPE_CHECKING

from modelarium._java.wrapper.wrapper_object import WrapperObject
from modelarium.scheduler.scheduler import Scheduler

if TYPE_CHECKING:
    from modelarium.read_only_clock import ReadOnlyClock
    from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
    from modelarium.entities.agent_sets.agent_set import AgentSet
    from modelarium.utils.random_generator import RandomGenerator


class JavaScheduler(ABC, Scheduler):
    def __init__(self, java_obj: object):
        WrapperObject.__init__(self, java_obj)

    @override
    def run_tick(
            self,
            thread_name: str,
            clock: ReadOnlyClock,
            environment: ReadOnlyEnvironment,
            agent_set: AgentSet,
            random: RandomGenerator
    ) -> None:
        self._java_object.runTick(
            thread_name,
            clock._java_object,
            environment._java_object,
            agent_set._java_object,
            random._java_object
        )
