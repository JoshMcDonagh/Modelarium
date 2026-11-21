from __future__ import annotations

from pathlib import Path
from typing import TYPE_CHECKING

from modelarium._java.objects.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject

if TYPE_CHECKING:
    from modelarium.results.read_only.read_only_results_for_agents import ReadOnlyResultsForAgents
    from modelarium.results.read_only.read_only_results_for_environment import ReadOnlyResultsForEnvironment


class ReadOnlyResults(ProtectedConstructorWrapperObject):
    def agents(self) -> ReadOnlyResultsForAgents:
        from modelarium.results.read_only.read_only_results_for_agents import ReadOnlyResultsForAgents
        from modelarium._java import to_python
        return to_python(ReadOnlyResultsForAgents, self._java_object.agents())

    def environment(self) -> ReadOnlyResultsForEnvironment:
        from modelarium.results.read_only.read_only_results_for_environment import ReadOnlyResultsForEnvironment
        from modelarium._java import to_python
        return to_python(ReadOnlyResultsForEnvironment, self._java_object.environment())

    def export(self, export_location: str | Path) -> Path:
        from modelarium._java import to_python
        if isinstance(export_location, Path):
            from modelarium._java import to_java
            return to_python(Path, self._java_object.export(to_java(export_location)))
        return to_python(Path, self._java_object.export(export_location))
