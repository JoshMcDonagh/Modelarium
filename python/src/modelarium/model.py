from modelarium import start_jvm
from modelarium._java import java_class
from modelarium._java.wrapper import to_java
from modelarium.config import Config
from modelarium.results.read_only.read_only_results import ReadOnlyResults
from modelarium._java.wrapper.wrapper_object import WrapperObject


class Model(WrapperObject):
    def __init__(self, config: Config) -> None:
        start_jvm()
        super().__init__(java_class("modelarium.Model")(to_java(config)))

    def run(self) -> None:
        self._java_object.run()

    def get_results(self) -> ReadOnlyResults:
        return ReadOnlyResults(self._java_object.getResults())
