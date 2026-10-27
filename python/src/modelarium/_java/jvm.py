from __future__ import annotations

import os
from importlib import resources
from pathlib import Path
from typing import cast


def bundled_jar_path() -> Path:
    """
    Returns the path to the JAR bundled inside this Python distribution.
    """
    modelarium_jar_name = "modelarium-2.1.0.jar"
    jar = resources.files("modelarium").joinpath("../_jars", modelarium_jar_name)

    if not jar.is_file():
        raise FileNotFoundError("Bundled Modelarium JAR ("+ modelarium_jar_name + ") not found.")

    if not isinstance(jar, Path):
        raise RuntimeError("The bundled JAR is not available as a filesystem path.")

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
    jars_dir = resources.files("modelarium") / "_jars"

    dependencies = sorted(
        (Path(cast(str | os.PathLike[str], cast(object, jar)))
         for jar in jars_dir.iterdir()
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
