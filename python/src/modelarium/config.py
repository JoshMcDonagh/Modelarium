from dataclasses import dataclass
from datetime import timedelta


@dataclass(frozen=True)
class Config:
    population_size: int
    tick_count: int
    thread_count: int
    thread_timeout: timedelta
    are_threads_synced: bool
    agent_generator: object # TODO: change to AgentGenerator type
    environment_generator: object # TODO: change to EnvironmentGenerator type
    scheduler: object # TODO: change to Scheduler type
    run_log_database_factory: object # TODO: change to AttributeSetLogDatabaseFactory
    seed: int
