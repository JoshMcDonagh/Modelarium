from pathlib import Path

from modelarium._java.type_conversion import java_to_python_path, python_to_java_path
from modelarium._java.wrapper import to_python
from modelarium._java.wrapper.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject
from modelarium.results.read_only.read_only_results_for_agents import ReadOnlyResultsForAgents
from modelarium.results.read_only.read_only_results_for_environment import ReadOnlyResultsForEnvironment


class ReadOnlyResults(ProtectedConstructorWrapperObject):
    def agents(self) -> ReadOnlyResultsForAgents:
        return to_python(ReadOnlyResultsForAgents, self._java_object.agents())

    def environment(self) -> ReadOnlyResultsForEnvironment:
        return to_python(ReadOnlyResultsForEnvironment, self._java_object.environment())

    def export(self, export_location: str | Path) -> Path:
        if isinstance(export_location, Path):
            return java_to_python_path(self._java_object.export(python_to_java_path(export_location)))
        return java_to_python_path(self._java_object.export(export_location))
