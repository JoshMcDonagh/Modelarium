import base64
import pickle
from typing import cast, Any


def encode(java_value: object) -> str:
    serialised = pickle.dumps(cast(Any, java_value).payload().value, protocol=pickle.HIGHEST_PROTOCOL)
    return base64.b64encode(serialised).decode("ascii")


def decode(encoded: str) -> object:
    value = pickle.loads(base64.b64decode(str(encoded), validate=True))
    from modelarium._java import to_java
    return to_java(value)
