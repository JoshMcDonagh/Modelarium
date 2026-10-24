package modelarium.python;

public final class PythonValue {
    private final PythonObject payload;
    private final PythonValueCopier copier;

    public PythonValue(PythonObject payload, PythonValueCopier copier) {
        this.payload = payload;
        this.copier = copier;
    }

    public PythonObject payload() {
        return payload;
    }

    public PythonValue deepCopy() {
        return new PythonValue(copier.copy(payload), copier);
    }
}
