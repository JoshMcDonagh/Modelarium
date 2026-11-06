from modelarium.results.read_only.read_only_results import ReadOnlyResults
from modelarium.results.read_only.read_only_results_for_agents import ReadOnlyResultsForAgents
from modelarium.results.read_only.read_only_results_for_environment import ReadOnlyResultsForEnvironment


class _ReadOnlyResults(ReadOnlyResults):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.wrapper.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _ReadOnlyResultsForAgents(ReadOnlyResultsForAgents):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.wrapper.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)


class _ReadOnlyResultsForEnvironment(ReadOnlyResultsForEnvironment):
    def __init__(self, java_object: object) -> None:
        from modelarium._java.wrapper.wrapper_object import WrapperObject
        WrapperObject.__init__(self, java_object)
