import jpype

from python.src.modelarium.config import Config, _config_to_java_config
from python.src.modelarium.results.read_only.read_only_results import ReadOnlyResults
from python.src.modelarium.wrapper_object import WrapperObject


class Model(WrapperObject):
    def __init__(self, config: Config) -> None:
        super().__init__(jpype.JClass("modelarium.Model")(_config_to_java_config(config)))

    def run(self) -> None:
        self._java_object.run()

    def get_results(self) -> ReadOnlyResults:
        return ReadOnlyResults(self._java_object.getResults())
