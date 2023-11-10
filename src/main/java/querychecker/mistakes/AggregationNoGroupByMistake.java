package querychecker.mistakes;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import observer.notifications.MistakeNotification;
import utils.StatementParser;

import java.util.*;

public class AggregationNoGroupByMistake extends Mistake {
    List<MistakeNotification> list;

    @Override
    public List<MistakeNotification> check(String query) {
        list = new ArrayList<>();
        Statement statement = StatementParser.parseStatement(query);
        if (statement == null) return list;
        if (!(statement instanceof Select select)) return list;

        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        visitPlainSelect(plainSelect);

        return list;
    }

    public static List<String> aggregate = List.of("SUM", "AVG", "MIN", "MAX");

    private Map<PlainSelect, Boolean> memo = new HashMap<>();

    private boolean doesAggregationFunctionRequireGroupBy(PlainSelect plainSelect) {
        if (memo.containsKey(plainSelect)) return memo.get(plainSelect);
        //Trazimo nesto bez funkcije agregacije, ako nadjemo vratimo true, ako ne nista
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        final boolean[] trueFlag = {false};
        for (SelectItem selectItem : selectItems) {
            SelectItemVisitor selectItemVisitor = new SelectItemVisitor() {
                @Override
                public void visit(AllColumns allColumns) {
                    trueFlag[0] = true;
                }

                @Override
                public void visit(AllTableColumns allTableColumns) {
                    trueFlag[0] = true;//Vrv treba da proverim da li se na nekom polju te tabele radila funkcija agregacije
                }

                @Override
                public void visit(SelectExpressionItem selectExpressionItem) {
                    Expression expression = selectExpressionItem.getExpression();
                    final boolean[] aggregationNotFound = {true};
                    ExpressionVisitor expressionVisitor = new ExpressionVisitorAdapter() {
                        @Override
                        public void visit(Function function) {
                            if (aggregate.contains(function.getName().trim().toUpperCase(Locale.ROOT))) {
                                aggregationNotFound[0] = false;
                            }
                        }

                        @Override
                        public void visit(MySQLGroupConcat mySQLGroupConcat) {
                            aggregationNotFound[0] = false;
                        }
                    };
                    expression.accept(expressionVisitor);
                    if (aggregationNotFound[0]) {
                        trueFlag[0] = true;
                    }
                }
            };
            selectItem.accept(selectItemVisitor);
        }
        if (trueFlag[0]) {
            memo.put(plainSelect, true);
            return true;
        }
        memo.put(plainSelect, false);
        return false;
    }

    public void visitPlainSelect(PlainSelect plainSelect) {

        //Funkcija agregacije zahteva da se uradi group by po necemu sto nije njen parametar, ukoliko
        //Ukoliko je samo jedan group by parametar, nad njim ne smeju da se rade funkcije agregacije

        List<SelectItem> selectItems = plainSelect.getSelectItems();
        SelectItemVisitor selectItemVisitor = new SelectItemVisitorAdapter() {
            @Override
            public void visit(SelectExpressionItem selectExpressionItem) {
                Expression expression = selectExpressionItem.getExpression();
                ExpressionVisitor expressionVisitor = new ExpressionVisitorAdapter() {
                    @Override
                    public void visit(Function function) {
                        if (aggregate.contains(function.getName().trim().toUpperCase(Locale.ROOT))
                                && plainSelect.getGroupByColumnReferences() == null
                                && doesAggregationFunctionRequireGroupBy(plainSelect)) {
                            list.add(new MistakeNotification(
                                    getName(),
                                    String.format(getDescription(), function.getName().toUpperCase(Locale.ROOT).trim()),
                                    getRecommendation()));
                        }
                    }

                    @Override
                    public void visit(MySQLGroupConcat mySQLGroupConcat) {

                        if (plainSelect.getGroupByColumnReferences() == null && doesAggregationFunctionRequireGroupBy(plainSelect)) {

                            list.add(new MistakeNotification(
                                    getName(),
                                    String.format(getDescription(), "GROUP_CONCAT"),
                                    getRecommendation()));
                        }
                    }
                };
                expression.accept(expressionVisitor);
            }
        };
        for (SelectItem selectItem : selectItems) {
            selectItem.accept(selectItemVisitor);
        }

        //WHERE provera, samo gledati da li nekom podupitu treba group by
        Expression where = plainSelect.getWhere();
        ExpressionVisitor expressionVisitor = new ExpressionVisitorAdapter() {
            @Override
            public void visit(SubSelect subSelect) {
                visitPlainSelect((PlainSelect) subSelect.getSelectBody());
            }
        };
        if (where != null)
            where.accept(expressionVisitor);
    }

    public AggregationNoGroupByMistake(String name, String description, String recommendation) {
        super(name, description, recommendation);
    }
}
