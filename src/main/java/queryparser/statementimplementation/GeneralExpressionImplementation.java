package queryparser.statementimplementation;

import queryparser.statementinterface.Expression;
import queryparser.visitorInterface.ExpressionVisitor;

import java.util.ArrayList;
import java.util.List;

public class GeneralExpressionImplementation implements Expression {
    String text;

    public static List<Expression> toList(String data) {
        List<Expression> list = new ArrayList<>();

        String[] splits = data.split("\\s,\\s");
        for (String a : splits) {
            list.add(new GeneralExpressionImplementation(a));
        }

        return list;
    }

    public GeneralExpressionImplementation(String text) {
        this.text = text;
    }

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {

    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
