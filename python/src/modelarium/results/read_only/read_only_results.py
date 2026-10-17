from python.src.modelarium.wrapper_object import WrapperObject


class ReadOnlyResults(WrapperObject):
    def agents(self) -> object: # TODO: Update with ReadOnlyResultsForAgents type hint
        return self._java_object.agents()

    def environment(self) -> object: # TODO: Update with ReadOnlyResultsForEnvironment type hint
        return self._java_object.environment()

    def export(self, export_location: str | object) -> object: # TODO: Update type hint with a Python equivalent to Java's Path
        return self._java_object.export(export_location)
