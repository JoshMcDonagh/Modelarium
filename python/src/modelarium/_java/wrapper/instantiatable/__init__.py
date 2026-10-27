from modelarium._java.wrapper import WrapperObject
from modelarium.read_only_clock import ReadOnlyClock


class _ReadOnlyClock(ReadOnlyClock):
    def __init__(self, java_object: object) -> None:
        WrapperObject.__init__(self, java_object)
