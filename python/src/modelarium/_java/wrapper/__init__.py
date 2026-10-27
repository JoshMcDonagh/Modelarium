from typing import TypeVar

from modelarium._java.wrapper.wrapper_object import WrapperObject

WRAPPER_OBJECT_T = TypeVar("WRAPPER_OBJECT_T", bound=WrapperObject)


def wrap_java_object(wrapper_type: type[WRAPPER_OBJECT_T], obj: object) -> WRAPPER_OBJECT_T:

