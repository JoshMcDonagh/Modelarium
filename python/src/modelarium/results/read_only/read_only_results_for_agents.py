import pandas as pd

from modelarium._java.objects.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject


class ReadOnlyResultsForAgents(ProtectedConstructorWrapperObject):
    @property
    def agent_log_count(self) -> int:
        return self._java_object.agentLogCount()

    def attribute_set_log_count(self, agent_name: str) -> int:
        return self._java_object.attributeSetLogCount(agent_name)

    def attribute_log_count(self, agent_name: str, attribute_set_name: str) -> int:
        return self._java_object.attributeLogCount(agent_name, attribute_set_name)

    def attribute_logs(self, agent_name: str, attribute_set_name: str, attribute_name: str) -> list[object]:
        from modelarium._java import to_python
        return to_python(list[object], self._java_object.attributeLogs(agent_name, attribute_set_name, attribute_name))

    def attribute_set_logs(self, agent_name: str, attribute_set_name: str) -> pd.DataFrame:
        from modelarium._java import to_python
        return pd.DataFrame(to_python(
            dict[str, list[object]],
            self._java_object.attributeSetLogs(agent_name, attribute_set_name)
        ))

    def agent_logs(self, agent_name: str) -> pd.DataFrame:
        from modelarium._java import to_python
        return pd.DataFrame(to_python(
            dict[str, dict[str, list[object]]],
            self._java_object.agentLogs(agent_name)
        ))

    def all_logs(self) -> pd.DataFrame:
        from modelarium._java import to_python
        return pd.DataFrame(to_python(
            dict[str, dict[str, dict[str, list[object]]]],
            self._java_object.allLogs()
        ))
