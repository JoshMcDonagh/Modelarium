from modelarium._java.wrapper.wrapper_object import WrapperObject


class ReadOnlyClock(WrapperObject):
    @property
    def current_tick(self) -> int:
        return self._java_object.currentTick()

    @property
    def total_tick_count(self) -> int:
        return self._java_object.totalTickCount()

    @property
    def is_finished(self) -> bool:
        return self._java_object.isFinished()
