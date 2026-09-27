from abc import ABC, abstractmethod

from python.src.modelarium.entities.attributes.attribute_access_level import AttributeAccessLevel


class Attribute(ABC):
    @property
    @abstractmethod
    def name(self) -> str:
        pass

    @property
    @abstractmethod
    def is_logged(self) -> bool:
        pass

    @property
    @abstractmethod
    def access_level(self) -> AttributeAccessLevel:
        pass

    @abstractmethod
    def run(self) -> None:
        pass

    @abstractmethod
    def get_as_immutable(self) -> object: # TODO: Update to return a ReadOnlyAttribute type
        pass
