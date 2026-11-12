"""
Check that the Java projects and Python package agree with VERSION.

Run with: python scripts/check_version_alignment.py
"""

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
    version = (ROOT / "VERSION").read_text(encoding="utf-8").strip()

    with (ROOT / "python" / "pyproject.toml").open("rb") as handle:
        python_config = tomllib.load(handle)

    versions = {
        "VERSION": version,
        "Maven aggregator": xml_text(ROOT / "pom.xml", "m:version"),
        "Java core": xml_text(ROOT / "java/modelarium/pom.xml", "m:version"),
        "Java examples": xml_text(ROOT / "java/modelarium-examples/pom.xml", "m:version"),
        "Java examples dependency": xml_text(
            ROOT / "java/modelarium-examples/pom.xml", "m:properties/m:modelarium.version"
        ),
        "Python package": python_config["project"]["version"],
        "Python bundled Java core": python_config["tool"]["modelarium"]["java_version"],
    }

    for label, found in versions.items():
        print(f"{label}: {found}")

    if any(found != version for found in versions.values()):
        print("Modelarium versions are not aligned.", file=sys.stderr)
        return 1

    print("All Modelarium versions are aligned.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
