from typing import Generic, override, Type, get_args

from python.src.modelarium.entities.attributes.properties.property import T
from python.src.modelarium.entities.attributes.read_only_attribute import ReadOnlyAttribute


class ReadOnlyProperty(ReadOnlyAttribute, Generic[T]):
    def __init__(self, java_obj: object) -> None:
        super().__init__(java_obj)

    @override
    def __str__(self) -> str:
        return self._java_object.toString()

    @property
    def type(self) -> Type[T]:
        orig_class = getattr(self, "__orig_class__", None)

        if orig_class is None:
            return object

        args = get_args(orig_class)

        if not args:
            return object

        return args[0]

    def get(self) -> T:
        return self._java_object.get()


