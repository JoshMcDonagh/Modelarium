from modelarium._java.objects.wrapper_object import WrapperObject


class ProtectedConstructorWrapperObject(WrapperObject):
    # noinspection PyMissingConstructor
    def __init__(self) -> None:
        raise Exception("This class could not be instantiated.")
