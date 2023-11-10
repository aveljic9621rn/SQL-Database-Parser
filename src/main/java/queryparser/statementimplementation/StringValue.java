package queryparser.statementimplementation;

import queryparser.statementinterface.Expression;
import queryparser.visitorInterface.ExpressionVisitor;

public class StringValue implements Expression {
    private String value = "";

    public StringValue(String escapedValue) {
        if (escapedValue.startsWith("'") && escapedValue.endsWith("'")) {
            this.value = escapedValue.substring(1, escapedValue.length() - 1);
        } else {
            this.value = escapedValue;
        }

    }

    public String getValue() {
        return this.value;
    }

    public String getNotExcapedValue() {
        StringBuilder buffer = new StringBuilder(this.value);
        int index = 0;

        for (int deletesNum = 0; (index = this.value.indexOf("''", index)) != -1; ++deletesNum) {
            buffer.deleteCharAt(index - deletesNum);
            index += 2;
        }

        return buffer.toString();
    }

    public void setValue(String string) {
        this.value = string;
    }

    public String toString() {
        return "'" + this.value + "'";
    }

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
}
