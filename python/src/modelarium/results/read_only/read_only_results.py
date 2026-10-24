from pathlib import Path

from modelarium._java.conversion import java_to_python_path, python_to_java_path
from modelarium.results.read_only.read_only_results_for_agents import ReadOnlyResultsForAgents
from modelarium.results.read_only.read_only_results_for_environment import ReadOnlyResultsForEnvironment
from modelarium.wrapper_object import WrapperObject


class ReadOnlyResults(WrapperObject):
    def agents(self) -> ReadOnlyResultsForAgents:
        return ReadOnlyResultsForAgents(self._java_object.agents())

    def environment(self) -> ReadOnlyResultsForEnvironment:
        return ReadOnlyResultsForEnvironment(self._java_object.environment())

    def export(self, export_location: str | Path) -> Path:
        if isinstance(export_location, Path):
            return java_to_python_path(self._java_object.export(python_to_java_path(export_location)))
        return java_to_python_path(self._java_object.export(export_location))
