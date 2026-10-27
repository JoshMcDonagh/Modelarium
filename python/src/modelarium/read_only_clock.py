from modelarium._java.wrapper.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject


class ReadOnlyClock(ProtectedConstructorWrapperObject):
    @property
    def current_tick(self) -> int:
        return self._java_object.currentTick()

    @property
    def total_tick_count(self) -> int:
        return self._java_object.totalTickCount()

    @property
    def is_finished(self) -> bool:
        return self._java_object.isFinished()
