import base64
import pickle
from typing import cast, Any

import jpype

from modelarium._java.entities.attributes.properties import python_to_java


def encode(java_value: object) -> str:
    serialised = pickle.dumps(cast(Any, java_value).payload().value, protocol=pickle.HIGHEST_PROTOCOL)
    return base64.b64encode(serialised).decode("ascii")


def decode(encoded: str) -> object:
    value = pickle.loads(base64.b64decode(str(encoded), validate=True))
    return python_to_java(type(value), value)


def create_python_value_codec() -> object:
    return jpype.JProxy("modelarium.python.PythonValueCodex", dict = {"encode": encode, "decode": decode})
