from modelarium._java.language_conversion import PYTHON_OBJECT_T
from modelarium.entities.attributes.agent_attribute import AgentAttribute
from modelarium.entities.attributes.attribute import Attribute
from modelarium.entities.attributes.environment_attribute import EnvironmentAttribute
from modelarium.entities.attributes.events.event import Event
from modelarium.entities.attributes.properties.property import Property
from modelarium.entities.attributes.routines.routine import Routine
from modelarium.entities.attributes.sets.attribute_set import AttributeSet
from modelarium.entities.attributes.sets.read_only.read_only_attribute_set import ReadOnlyAttributeSet
from modelarium.entities.contexts.context import Context
from modelarium.entities.entity import Entity
from modelarium.entities.read_only.read_only_entity import ReadOnlyEntity

_PARENT_CLASSES_TO_CONVERT_TO_SUBCLASSES = [
    Entity,
    AgentAttribute,
    EnvironmentAttribute,
    Attribute,
    Event,
    Property,
    Routine,
    AttributeSet,
    ReadOnlyAttributeSet,
    Context,
    ReadOnlyEntity
]


def subclass_needs_to_be_converted_from(python_type: type[PYTHON_OBJECT_T]) -> bool:
    return python_type in _PARENT_CLASSES_TO_CONVERT_TO_SUBCLASSES