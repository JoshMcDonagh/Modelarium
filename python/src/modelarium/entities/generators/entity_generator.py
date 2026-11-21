from abc import ABC
from collections.abc import Callable

import jpype

from modelarium._java.objects.wrapper_object import WrapperObject


class EntityGenerator(ABC, WrapperObject):
    def __init__(
            self,
            generator_function: Callable[[object, object], object],
            java_class_name: str
    ) -> None:
        generator_function = jpype.JProxy(
            "java.util.function.BiFunction",
            dict={"apply": generator_function}
        )
        reset_function = jpype.JProxy("java.lang.Runnable", dict={"run": self.reset})

        from modelarium._java import java_class
        super().__init__(java_class(java_class_name)(generator_function, reset_function))

    def reset(self) -> None:
        pass    # No-op by default
