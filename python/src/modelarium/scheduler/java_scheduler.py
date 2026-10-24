from abc import ABC
from typing import override

from modelarium.entities.agent_sets.agent_set import AgentSet
from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
from modelarium.read_only_clock import ReadOnlyClock
from modelarium.scheduler.scheduler import Scheduler
from modelarium.utils.random_generator import RandomGenerator
from modelarium.wrapper_object import WrapperObject


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
