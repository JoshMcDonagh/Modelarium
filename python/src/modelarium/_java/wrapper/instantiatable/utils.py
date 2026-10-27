from modelarium._java.wrapper import WrapperObject
from modelarium.utils.random_generator import RandomGenerator


class _RandomGenerator(RandomGenerator):
    def __init__(self, java_object: object) -> None:
        WrapperObject.__init__(self, java_object)
