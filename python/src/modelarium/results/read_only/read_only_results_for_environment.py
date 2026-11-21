import pandas as pd

from modelarium._java.objects.protected_constructor_wrapper_object import ProtectedConstructorWrapperObject


class ReadOnlyResultsForEnvironment(ProtectedConstructorWrapperObject):
    @property
    def attribute_set_log_count(self) -> int:
        return self._java_object.attributeSetLogCount()

    def attribute_log_count(self, attribute_set_name: str) -> int:
        return self._java_object.attributeLogCount(attribute_set_name)

    def attribute_logs(self, attribute_set_name: str, attribute_name: str) -> list[object]:
        from modelarium._java import to_python
        return to_python(
            list[object],
            self._java_object.attributeLogs(attribute_set_name, attribute_name)
        )

    def attribute_set_logs(self, attribute_set_name: str) -> pd.DataFrame:
        from modelarium._java import to_python
        return pd.DataFrame(to_python(
            dict[str, list[object]],
            self._java_object.attributeSetLogs(attribute_set_name)
        ))

    def environment_logs(self) -> pd.DataFrame:
        from modelarium._java import to_python
        return pd.DataFrame(to_python(
            dict[str, dict[str, list[object]]],
            self._java_object.environmentLogs()
        ))
