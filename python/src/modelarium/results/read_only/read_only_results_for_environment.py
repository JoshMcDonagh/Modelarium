import pandas as pd

from python.src.modelarium.results.read_only import _java_map_to_dataframe
from python.src.modelarium.wrapper_object import WrapperObject


class ReadOnlyResultsForEnvironment(WrapperObject):
    @property
    def attribute_set_log_count(self) -> int:
        return self._java_object.attributeSetLogCount()

    def attribute_log_count(self, attribute_set_name: str) -> int:
        return self._java_object.attributeLogCount(attribute_set_name)

    def attribute_logs(self, attribute_set_name: str, attribute_name: str) -> list[object]:
        java_list = self._java_object.attributeLogs(attribute_set_name, attribute_name)
        log_value_list = []
        for log_value in java_list:
            log_value_list.append(log_value)
        return log_value_list

    def attribute_set_logs(self, attribute_set_name: str) -> pd.DataFrame:
        return _java_map_to_dataframe(
            self._java_object.attributeSetLogs(attribute_set_name),
            ["attribute"]
        )

    def environment_logs(self) -> pd.DataFrame:
        return _java_map_to_dataframe(
            self._java_object.environmentLogs(),
            ["attribute_set", "attribute"]
        )
