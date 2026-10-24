package modelarium.python;

@FunctionalInterface
public interface PythonValueCopier {
    PythonObject copy(PythonObject value);
}
