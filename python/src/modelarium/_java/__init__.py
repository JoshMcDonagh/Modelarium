from typing import Any, cast

import jpype


def java_class(class_name: str) -> Any:
    return cast(Any, jpype.JClass(class_name))
