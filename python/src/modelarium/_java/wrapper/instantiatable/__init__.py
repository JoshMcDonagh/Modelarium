from modelarium.read_only_clock import ReadOnlyClock


class _ReadOnlyClock(ReadOnlyClock):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.wrapper.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)
