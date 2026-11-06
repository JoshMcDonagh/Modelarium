from __future__ import annotations

from typing import TYPE_CHECKING

from modelarium import start_jvm
from modelarium._java import java_class
from modelarium._java.wrapper import to_java, to_python
from modelarium._java.wrapper.wrapper_object import WrapperObject

if TYPE_CHECKING:
    from modelarium.config import Config
    from modelarium.results.read_only.read_only_results import ReadOnlyResults


class Model(WrapperObject):
    def __init__(self, config: Config) -> None:
        start_jvm()
        super().__init__(java_class("modelarium.Model")(to_java(config)))

    def run(self) -> None:
        self._java_object.run()

    def get_results(self) -> ReadOnlyResults:
        from modelarium.results.read_only.read_only_results import ReadOnlyResults
        return to_python(ReadOnlyResults, self._java_object.getResults())
