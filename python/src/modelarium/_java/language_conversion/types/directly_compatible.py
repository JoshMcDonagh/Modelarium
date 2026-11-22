_DIRECTLY_COMPATIBLE_TYPES = frozenset({bool, int, float, str})


def is_directly_compatible_type(type_: type) -> bool:
    return type_ in _DIRECTLY_COMPATIBLE_TYPES


def is_directly_compatible_object(object_: object) -> bool:
    return type(object_) in _DIRECTLY_COMPATIBLE_TYPES
