import pandas as pd

from python.src.modelarium.results.read_only import _java_map_to_dataframe
from python.src.modelarium.wrapper_object import WrapperObject


class ReadOnlyResultsForAgents(WrapperObject):
    @property
    def agent_log_count(self) -> int:
        return self._java_object.agentLogCount()

    def attribute_set_log_count(self, agent_name: str) -> int:
        return self._java_object.attributeSetLogCount(agent_name)

    def attribute_log_count(self, agent_name: str, attribute_set_name: str) -> int:
        return self._java_object.attributeLogCount(agent_name, attribute_set_name)

    def attribute_logs(self, agent_name: str, attribute_set_name: str, attribute_name: str) -> list[object]:
        java_list = self._java_object.attributeLogs(agent_name, attribute_set_name, attribute_name)
        log_value_list = []
        for log_value in java_list:
            log_value_list.append(log_value)
        return log_value_list

    def attribute_set_logs(self, agent_name: str, attribute_set_name: str) -> pd.DataFrame:
        return _java_map_to_dataframe(
            self._java_object.attributeSetLogs(agent_name, attribute_set_name),
            ["attribute"]
        )

    def agent_logs(self, agent_name: str) -> pd.DataFrame:
        return _java_map_to_dataframe(
            self._java_object.agentLogs(agent_name),
            ["attribute_set", "attribute"]
        )

    def all_logs(self) -> pd.DataFrame:
        return _java_map_to_dataframe(
            self._java_object.allLogs(),
            ["agent", "attribute_set", "attribute"]
        )
