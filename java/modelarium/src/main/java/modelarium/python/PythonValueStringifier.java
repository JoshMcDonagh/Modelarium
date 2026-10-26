package modelarium.python;

@FunctionalInterface
public interface PythonValueStringifier {
    String stringOf(PythonObject value);
}