from __future__ import annotations

import os
from importlib import resources
from pathlib import Path
from typing import cast

JARS_DIR = resources.files("modelarium._java").joinpath("jars")


def bundled_jar_path() -> Path:
    """
    Returns the path to the JAR bundled inside this Python distribution.
    """
    candidates = [
        jar for jar in JARS_DIR.iterdir()
        if jar.name.startswith("modelarium-")
           and jar.name.endswith(".jar")
           and not jar.name.endswith(("-sources.jar", "-javadoc.jar"))
           and jar.is_file()
    ]

    if len(candidates) != 1:
        raise RuntimeError(f"Expected exactly one bundled Modelarium core JAR in {JARS_DIR}, found {candidates}")

    jar = candidates[0]

    if not isinstance(jar, Path):
        raise RuntimeError(f"Bundled JAR is not available as a filesystem path")
    return jar


def get_jar_path() -> Path:
    """
    Prefer an override env var, otherwise use bundled jar.
    """
    override = os.environ.get("MODELARIUM_JAR")
    if override:
        return Path(override).expanduser().resolve()
    return bundled_jar_path()


def get_classpath() -> list[Path]:
    """
    Return the core JAR and its bundled runtime dependencies.
    """
    core = get_jar_path()

    dependencies = sorted(
        (Path(cast(str | os.PathLike[str], cast(object, jar)))
         for jar in JARS_DIR.iterdir()
         if jar.name.endswith(".jar") and Path(cast(str | os.PathLike[str], cast(object, jar))) != core)
        , key=lambda jar: jar.name
    )
    return [core, *dependencies]


def start_jvm() -> None:
    """
    Start JPype with all JARs required by Modelarium.
    """
    import jpype

    if not jpype.isJVMStarted():
        jpype.startJVM(classpath=[str(jar) for jar in get_classpath()])
