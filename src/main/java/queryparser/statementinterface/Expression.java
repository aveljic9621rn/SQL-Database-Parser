package queryparser.statementinterface;

import queryparser.visitorInterface.ExpressionVisitor;

public interface Expression {
    void accept(ExpressionVisitor expressionVisitor);
}
