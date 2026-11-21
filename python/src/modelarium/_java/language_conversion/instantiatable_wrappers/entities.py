from __future__ import annotations

from typing import Generic, override, TYPE_CHECKING

from modelarium.entities.agent import Agent
from modelarium.entities.agent_sets.agent_set import AgentSet
from modelarium.entities.agent_sets.read_only_agent_set import ReadOnlyAgentSet
from modelarium.entities.attributes.events.agent_event import AgentEvent
from modelarium.entities.attributes.events.environment_event import EnvironmentEvent
from modelarium.entities.attributes.events.read_only_event import ReadOnlyEvent
from modelarium.entities.attributes.properties import PROPERTY_T
from modelarium.entities.attributes.properties.agent_property import AgentProperty
from modelarium.entities.attributes.properties.environment_property import EnvironmentProperty
from modelarium.entities.attributes.properties.read_only_property import ReadOnlyProperty
from modelarium.entities.attributes.read_only_attribute import ReadOnlyAttribute
from modelarium.entities.attributes.routines.agent_routine import AgentRoutine
from modelarium.entities.attributes.routines.environment_routine import EnvironmentRoutine
from modelarium.entities.attributes.routines.read_only_routine import ReadOnlyRoutine
from modelarium.entities.attributes.sets.agent_attribute_set import AgentAttributeSet
from modelarium.entities.attributes.sets.environment_attribute_set import EnvironmentAttributeSet
from modelarium.entities.attributes.sets.read_only.read_only_agent_attribute_set import ReadOnlyAgentAttributeSet
from modelarium.entities.attributes.sets.read_only.read_only_environment_attribute_set import \
    ReadOnlyEnvironmentAttributeSet
from modelarium.entities.contexts.agent_context import AgentContext
from modelarium.entities.contexts.context import Context
from modelarium.entities.contexts.environment_context import EnvironmentContext
from modelarium.entities.environment import Environment
from modelarium.entities.generators.agent_generator import AgentGenerator
from modelarium.entities.generators.default_agent_generator import DefaultAgentGenerator
from modelarium.entities.generators.environment_generator import EnvironmentGenerator
from modelarium.entities.logging.attribute_set_log import AttributeSetLog
from modelarium.entities.logging.databases.attribute_set_log_database import AttributeSetLogDatabase
from modelarium.entities.logging.databases.disk_based_attribute_set_log_database import DiskBasedAttributeSetLogDatabase
from modelarium.entities.logging.databases.factories.attribute_set_log_database_factory import \
    AttributeSetLogDatabaseFactory
from modelarium.entities.logging.databases.memory_based_attribute_set_log_database import \
    MemoryBasedAttributeSetLogDatabase
from modelarium.entities.logging.entity_log import EntityLog
from modelarium.entities.read_only.read_only_agent import ReadOnlyAgent
from modelarium.entities.read_only.read_only_environment import ReadOnlyEnvironment
from modelarium.utils.random_generator import RandomGenerator

if TYPE_CHECKING:
    from modelarium.config import Config


class _Agent(Agent):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _Environment(Environment):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _AgentSet(AgentSet):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _ReadOnlyAgentSet(ReadOnlyAgentSet):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _ReadOnlyAttribute(ReadOnlyAttribute):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _AgentEvent(AgentEvent):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def _run(self, context: Context) -> None:
        from modelarium._java import to_java
        self._java_object.run(to_java(context))

    @override
    def _is_triggered(self, context: Context) -> bool:
        from modelarium._java import to_java
        return self._java_object.isTriggered(to_java(context))


class _EnvironmentEvent(EnvironmentEvent):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def _run(self, context: Context) -> None:
        from modelarium._java import to_java
        self._java_object.run(to_java(context))

    @override
    def _is_triggered(self, context: Context) -> bool:
        from modelarium._java import to_java
        return self._java_object.isTriggered(to_java(context))


class _ReadOnlyEvent(ReadOnlyEvent):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _AgentProperty(Generic[PROPERTY_T], AgentProperty[PROPERTY_T]):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def _set(
            self,
            context: Context,
            current_value: PROPERTY_T | None,
            new_value: PROPERTY_T | None
    ) -> PROPERTY_T | None:
        from modelarium._java import to_java
        self._java_object.set(to_java(new_value))
        return new_value

    @override
    def _get(self, context: Context, current_value: PROPERTY_T | None) -> PROPERTY_T | None:
        from modelarium._java import to_python
        return to_python(self.type, self._java_object.get())


class _EnvironmentProperty(Generic[PROPERTY_T], EnvironmentProperty[PROPERTY_T]):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def _set(
            self,
            context: Context,
            current_value: PROPERTY_T | None,
            new_value: PROPERTY_T | None
    ) -> PROPERTY_T | None:
        from modelarium._java import to_java
        self._java_object.set(to_java(new_value))
        return new_value

    @override
    def _get(self, context: Context, current_value: PROPERTY_T | None) -> PROPERTY_T | None:
        from modelarium._java import to_python
        return to_python(self.type, self._java_object.get())


class _ReadOnlyProperty(Generic[PROPERTY_T], ReadOnlyProperty[PROPERTY_T]):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _AgentRoutine(AgentRoutine):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def _run(self, context: Context) -> None:
        from modelarium._java import to_java
        self._java_object.run(to_java(context))


class _EnvironmentRoutine(EnvironmentRoutine):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def _run(self, context: Context) -> None:
        from modelarium._java import to_java
        self._java_object.run(to_java(context))


class _ReadOnlyRoutine(ReadOnlyRoutine):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _AgentAttributeSet(AgentAttributeSet):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _EnvironmentAttributeSet(EnvironmentAttributeSet):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _ReadOnlyAgentAttributeSet(ReadOnlyAgentAttributeSet):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _ReadOnlyEnvironmentAttributeSet(ReadOnlyEnvironmentAttributeSet):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _AgentContext(AgentContext):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _EnvironmentContext(EnvironmentContext):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _AgentGenerator(AgentGenerator):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def generate_agents_for_each_thread(self, config: Config, random: RandomGenerator) -> list[AgentSet]:
        from modelarium._java import to_java, to_python
        java_list = self._java_object.generateAgentsForEachThread(to_java(config), to_java(random))
        python_list = []
        for item in java_list:
            python_list.append(to_python(AgentSet, item))
        return python_list

    @override
    def reset(self) -> None:
        self._java_object.internalReset()


class _DefaultAgentGenerator(DefaultAgentGenerator):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def generate_agent(self, config: Config, random: RandomGenerator) -> Agent:
        from modelarium._java import to_java
        return _Agent(self._java_object.generateAgent(to_java(config), to_java(random)))

    @override
    def reset(self) -> None:
        self._java_object.internalReset()


class _EnvironmentGenerator(EnvironmentGenerator):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def generate_environment(self, config: Config, random: RandomGenerator) -> Environment:
        from modelarium._java import to_java
        return _Environment(self._java_object.generateEnvironment(to_java(config), to_java(random)))

    @override
    def reset(self) -> None:
        self._java_object.internalReset()


class _AttributeSetLog(AttributeSetLog):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _EntityLog(EntityLog):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _AttributeSetLogDatabase(AttributeSetLogDatabase):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def connect(self) -> None:
        self._java_object.connect()

    @override
    def disconnect(self) -> None:
        self._java_object.disconnect()

    @override
    def add_attribute_value(self, attribute_name: str, attribute_value: object) -> None:
        self._java_object.addAttributeValue(attribute_name, attribute_value)

    @override
    def set_attribute_column(self, attribute_name: str, attribute_values: list[object]) -> None:
        from modelarium._java import to_java
        self._java_object.setAttributeColumn(attribute_name, to_java(attribute_values))

    @override
    def get_attribute_column_as_list(self, attribute_name: str) -> list[object]:
        from modelarium._java import to_python
        return to_python(list[object], self._java_object.getAttributeColumnAsList(attribute_name))


class _DiskBasedAttributeSetLogDatabase(DiskBasedAttributeSetLogDatabase):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _MemoryBasedAttributeSetLogDatabase(MemoryBasedAttributeSetLogDatabase):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _AttributeSetLogDatabaseFactory(AttributeSetLogDatabaseFactory):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)

    @override
    def create(self) -> AttributeSetLogDatabase:
        return _AttributeSetLogDatabase(self._java_object.create())


class _ReadOnlyAgent(ReadOnlyAgent):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _ReadOnlyEnvironment(ReadOnlyEnvironment):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.objects.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)
