package queryparser.statementimplementation;

import queryparser.statementinterface.Expression;
import queryparser.visitorInterface.ExpressionVisitor;

public class NullValue implements Expression {
    public NullValue() {
    }

    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    public String toString() {
        return "NULL";
    }
}
