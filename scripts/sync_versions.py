"""
Synchronise Java and Python versions with VERSION.

Run with: python scripts/sync_versions.py

All Java project versions, the Python distribution version and the Python
configuration's bundled-Java version follow the same VERSION value.
"""

from __future__ import annotations

import re
import tomllib
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def replace_once(text: str, pattern: str, version: str, file: Path) -> str:
    matches = list(re.finditer(pattern, text, flags=re.MULTILINE))
    if len(matches) != 1:
        raise ValueError(
            f"Expected one matching version declaration in {file}, "
            f"found {len(matches)}"
        )
    match = matches[0]
    return (
        text[:match.start()]
        + match.group(1)
        + version
        + match.group(3)
        + text[match.end():]
    )


def update_pom(path: Path, artifact_id: str, version: str, *, examples: bool = False) -> str:
    original = path.read_text(encoding="utf-8")
    pattern = (
        rf"(<artifactId>\s*{re.escape(artifact_id)}\s*</artifactId>\s*<version>\s*)"
        r"([^<\s]+)(\s*</version>)"
    )
    updated = replace_once(original, pattern, version, path)
    if examples:
        updated = replace_once(
            updated,
            r"(<modelarium\.version>\s*)([^<\s]+)(\s*</modelarium\.version>)",
            version,
            path,
        )
    ET.fromstring(updated)
    return updated


def replace_toml_key_in_section(
    document: str, section_name: str, key: str, version: str, path: Path
) -> str:
    section = re.search(
        rf"(?ms)^(\[{re.escape(section_name)}\][ \t]*\r?\n)(.*?)(?=^\[|\Z)",
        document,
    )
    if section is None:
        raise ValueError(f"Missing [{section_name}] table in {path}")

    body = replace_once(
        section.group(2),
        rf'''^([ \t]*{re.escape(key)}[ \t]*=[ \t]*["'])([^"'\r\n]+)(["'][ \t]*(?:\#.*)?)$''',
        version,
        path,
    )
    return document[:section.start(2)] + body + document[section.end(2):]


def update_pyproject(path: Path, version: str) -> str:
    updated = path.read_text(encoding="utf-8")
    updated = replace_toml_key_in_section(updated, "project", "version", version, path)
    updated = replace_toml_key_in_section(
        updated, "tool.modelarium", "java_version", version, path
    )
    parsed = tomllib.loads(updated)  # Validate TOML before writing.
    if parsed["project"]["version"] != version:
        raise ValueError("Python project version was not updated")
    if parsed["tool"]["modelarium"]["java_version"] != version:
        raise ValueError("Python bundled-Java version was not updated")
    return updated


def main() -> None:
    version = (ROOT / "VERSION").read_text(encoding="utf-8").strip()
    if not re.fullmatch(r"\d+\.\d+\.\d+", version):
        raise ValueError(
            f"Invalid Modelarium version in VERSION: {version!r}; "
            "expected a stable MAJOR.MINOR.PATCH version"
        )

    targets = {
        ROOT / "pom.xml": update_pom(ROOT / "pom.xml", "modelarium-aggregator", version),
        ROOT / "java/modelarium/pom.xml": update_pom(
            ROOT / "java/modelarium/pom.xml", "modelarium", version
        ),
        ROOT / "java/modelarium-examples/pom.xml": update_pom(
            ROOT / "java/modelarium-examples/pom.xml", "modelarium-examples", version, examples=True
        ),
        ROOT / "python/pyproject.toml": update_pyproject(
            ROOT / "python/pyproject.toml", version
        ),
    }

    changed = 0
    for path, updated in targets.items():
        if path.read_text(encoding="utf-8") != updated:
            path.write_text(updated, encoding="utf-8")
            print(f"Updated {path.relative_to(ROOT)} -> {version}")
            changed += 1

    print(f"Finished: {changed} file(s) updated; Modelarium version {version}.")


if __name__ == "__main__":
    main()
