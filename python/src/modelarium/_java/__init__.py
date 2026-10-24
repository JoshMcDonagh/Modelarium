from typing import Any, cast


def java_class(class_name: str) -> Any:
    return cast(Any, java_class(class_name))
