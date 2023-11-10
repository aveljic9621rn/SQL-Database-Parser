package queryparser.statementimplementation;

import queryparser.statementinterface.Expression;
import queryparser.statementinterface.ItemsList;

import java.util.List;

public class ExpressionList implements ItemsList {
    private List<Expression> expressions;

    public ExpressionList(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }
}
