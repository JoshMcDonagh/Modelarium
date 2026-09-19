"""Verify that every subproject targets the same Modelarium Java core version."""

from __future__ import annotations

import sys
import tomllib
import xml.etree.ElementTree as ET
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
MAVEN_NAMESPACE = {"m": "http://maven.apache.org/POM/4.0.0"}


def xml_text(path: Path, expression: str) -> str:
    value = ET.parse(path).getroot().findtext(expression, namespaces=MAVEN_NAMESPACE)
    if value is None or not value.strip():
        raise RuntimeError(f"Could not read {expression!r} from {path}")
    return value.strip()


def main() -> int:
    core_version = xml_text(ROOT / "java" / "modelarium" / "pom.xml", "m:version")
    examples_version = xml_text(
        ROOT / "java" / "modelarium-examples" / "pom.xml",
        "m:properties/m:modelarium.version",
    )

    with (ROOT / "python" / "pyproject.toml").open("rb") as file:
        python_configuration = tomllib.load(file)
    python_java_version = python_configuration["tool"]["modelarium"]["java_version"]

    versions = {
        "Java core": core_version,
        "Java examples dependency": examples_version,
        "Python bundled Java core": python_java_version,
    }

    for label, version in versions.items():
        print(f"{label}: {version}")

    if len(set(versions.values())) != 1:
        print("Modelarium Java core versions are not aligned.", file=sys.stderr)
        return 1

    print("Modelarium Java core versions are aligned.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

