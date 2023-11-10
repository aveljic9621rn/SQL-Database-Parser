package queryparser.statementimplementation;

public class Parameter extends Variable {
    private boolean output;

    public Parameter(String name, boolean output, String type) {
        super(name, type);
        this.output = output;
    }

    public boolean isOutput() {
        return output;
    }
}
