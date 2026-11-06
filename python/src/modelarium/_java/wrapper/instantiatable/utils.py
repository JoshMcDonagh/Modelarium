from modelarium.utils.random_generator import RandomGenerator


class _RandomGenerator(RandomGenerator):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.wrapper.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)
