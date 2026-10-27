import pandas as pd

from modelarium._java.type_conversion import java_map_to_dataframe, java_list_to_list
from modelarium._java.wrapper.wrapper_object import WrapperObject


class ReadOnlyResultsForEnvironment(WrapperObject):
    @property
    def attribute_set_log_count(self) -> int:
        return self._java_object.attributeSetLogCount()

    def attribute_log_count(self, attribute_set_name: str) -> int:
        return self._java_object.attributeLogCount(attribute_set_name)

    def attribute_logs(self, attribute_set_name: str, attribute_name: str) -> list[object]:
        return java_list_to_list(
            self._java_object.attributeLogs(attribute_set_name, attribute_name),
            convert_log_values=True
        )

    def attribute_set_logs(self, attribute_set_name: str) -> pd.DataFrame:
        return java_map_to_dataframe(
            self._java_object.attributeSetLogs(attribute_set_name),
            ["attribute"]
        )

    def environment_logs(self) -> pd.DataFrame:
        return java_map_to_dataframe(
            self._java_object.environmentLogs(),
            ["attribute_set", "attribute"]
        )
