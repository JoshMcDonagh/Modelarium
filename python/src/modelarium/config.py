from __future__ import annotations

from dataclasses import dataclass
from datetime import timedelta

import jpype

from python.src.modelarium.entities.generators.agent_generator import AgentGenerator
from python.src.modelarium.entities.generators.environment_generator import EnvironmentGenerator
from python.src.modelarium.scheduler.scheduler import Scheduler
from python.src.modelarium.utils.language_conversion import _timedelta_to_java_duration


def _config_to_java(config: Config):
    return jpype.JClass("modelarium.Config")(
        config.population_size,
        config.tick_count,
        config.thread_count,
        _timedelta_to_java_duration(config.thread_timeout),
        config.are_threads_synced,
        config.agent_generator._java_object,
        config.environment_generator._java_object,
        config.scheduler._java_object,
        config.run_log_database_factory._java_object,
        jpype.JLong(config.seed)
    )


@dataclass(frozen=True)
class Config:
    population_size: int
    tick_count: int
    thread_count: int
    thread_timeout: timedelta
    are_threads_synced: bool
    agent_generator: AgentGenerator
    environment_generator: EnvironmentGenerator
    scheduler: Scheduler
    run_log_database_factory: object # TODO: change to AttributeSetLogDatabaseFactory
    seed: int
