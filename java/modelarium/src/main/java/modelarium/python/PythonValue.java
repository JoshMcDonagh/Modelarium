package modelarium.python;

public final class PythonValue {
    private final PythonObject payload;
    private final PythonValueCopier copier;
    private final PythonValueStringifier stringifier;

    public PythonValue(PythonObject payload, PythonValueCopier copier, PythonValueStringifier stringifier) {
        this.payload = payload;
        this.copier = copier;
        this.stringifier = stringifier;
    }

    public PythonObject payload() {
        return payload;
    }

    public PythonValue deepCopy() {
        return new PythonValue(copier.copy(payload), copier, stringifier);
    }

    @Override
    public String toString() {
        return stringifier.stringOf(payload);
    }
}
