import jpype

from modelarium._java import java_class
from modelarium._java.wrapper.wrapper_object import WrapperObject


class RandomGenerator(WrapperObject):
    def __init__(self, seed: int | None = None) -> None:
        java_random_generator = java_class("java.util.Random")
        if seed is None:
            super().__init__(java_random_generator())
        else:
            super().__init__(java_random_generator(jpype.JLong(seed)))

    def next_int(self, bound: int | None = None) -> int:
        if bound is None:
            return int(self._java_object.nextInt())
        return int(self._java_object.nextInt(bound))

    def next_double(self) -> float:
        return float(self._java_object.nextDouble())

    def next_boolean(self) -> bool:
        return bool(self._java_object.nextBoolean())
