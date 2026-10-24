package modelarium.python;

public interface PythonValueCodec {
    String encode(PythonValue value);
    PythonValue decode(String encoded);
}
