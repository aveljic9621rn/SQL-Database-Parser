package queryparser.statementimplementation;

import queryparser.statementinterface.Expression;
import queryparser.visitorInterface.ExpressionVisitor;

public class NumberValue implements Expression {
    private Integer value = 0;

    public NumberValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public void setValue(Integer integer) {
        this.value = integer;
    }

    public String toString() {
        return this.value.toString();
    }

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
}
