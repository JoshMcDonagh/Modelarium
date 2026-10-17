from pathlib import Path

import jpype

from python.src.modelarium.results.read_only.read_only_results_for_agents import ReadOnlyResultsForAgents
from python.src.modelarium.wrapper_object import WrapperObject


class ReadOnlyResults(WrapperObject):
    def agents(self) -> ReadOnlyResultsForAgents:
        return ReadOnlyResultsForAgents(self._java_object.agents())

    def environment(self) -> object: # TODO: Update with ReadOnlyResultsForEnvironment type hint
        return self._java_object.environment()

    @staticmethod
    def _python_to_java_path(python_path: Path) -> object:
        return jpype.JClass("java.nio.file.Path").of(str(python_path))

    @staticmethod
    def _java_to_python_path(java_path: object) -> Path:
        return Path(str(java_path))

    def export(self, export_location: str | Path) -> Path:
        if isinstance(export_location, Path):
            return self._java_to_python_path(self._java_object.export(self._python_to_java_path(export_location)))
        return self._java_to_python_path(self._java_object.export(export_location))
