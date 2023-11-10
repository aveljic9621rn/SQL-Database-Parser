package queryparser.statementimplementation;

import queryparser.statementinterface.ItemsList;

import java.util.List;

public class MultiExpressionList implements ItemsList {
    private List<ExpressionList> exprList;

    public MultiExpressionList(List<ExpressionList> expressionLists) {
        exprList = expressionLists;
    }

    public List<ExpressionList> getExprList() {
        return exprList;
    }
}
