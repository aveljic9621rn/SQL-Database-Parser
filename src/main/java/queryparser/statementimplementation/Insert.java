package queryparser.statementimplementation;

import queryparser.Pair;
import queryparser.Util;
import queryparser.statementinterface.Expression;
import queryparser.statementinterface.ItemsList;
import queryparser.statementinterface.Statement;
import queryparser.visitorInterface.StatementVisitor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Insert implements Statement {
    private String query;
    private Table table;
    private List<Column> columns;
    private ItemsList itemsList;

    public Insert(String query) {
        this.query = query.trim();
        query = query.trim().substring(6).trim().substring(4).trim();

        Pair<String, String> tableResult = Util.readWordOrQuotes(query, new Character[]{' '}, new Character[]{'`'});
        this.table = tableResult == null ? null : new Table(tableResult.getFirst().trim(), "");

        query = tableResult.getSecond().trim();
        this.columns = null;
        if (query.startsWith("(")) {
            this.columns = new ArrayList<>();
            query = query.substring(1).trim();
            while (query.charAt(0) != ')') {
                Pair<String, String> columnResult = Util.readWordOrQuotes(query, new Character[]{',', ')'}, new Character[]{'`'});
                this.columns.add(new Column(columnResult.getFirst().trim(), this.table));
                query = columnResult.getSecond();
                if (query.charAt(0) == ')') {
                    query = query.substring(1).trim();
                    break;
                } else if (query.charAt(0) != ',') return;//Greska prilikom citanja kolona
                query = query.substring(1).trim();
            }
        }
        query = query.trim();
        if (!query.toUpperCase(Locale.ROOT).startsWith("VALUES")) return;//CSV uvek ima values?
        query = query.substring(6).trim();
        List<List<Expression>> matrix = new ArrayList<>();
        while (query.charAt(0) == '(') {
            query = query.substring(1).trim();
            List<Expression> values = new ArrayList<>();
            while (query.charAt(0) != ')') {
                Pair<String, String> valueResult = Util.readWordOrQuotes(query, new Character[]{',', ')'}, new Character[]{'\''});
                String val = valueResult.getFirst();

                if (val.trim().equalsIgnoreCase("NULL")) {
                    values.add(new NullValue());
                } else if (query.startsWith("'")) {
                    values.add(new StringValue(val));
                } else {
                    try {
                        Integer.parseInt(val);
                        Float.parseFloat(val);
                        Double.parseDouble(val);
                        Long.parseLong(val);
                        new BigInteger(val);
                        values.add(new NumberValue(Integer.parseInt(val)));
                    } catch (NumberFormatException exception) {
                        //Ne rade datumi
                        values.add(new NullValue());
                    }
                }
                query = valueResult.getSecond();
                if (query.charAt(0) == ')') {
                    query = query.substring(1).trim();
                    break;
                } else if (query.charAt(0) != ',') return;//Greska prilikom citanja kolona
                query = query.substring(1).trim();
            }
            matrix.add(values);
            if (query.trim().length() == 0) break;
            query = query.trim().substring(1).trim();
        }
        if (matrix.size() == 0) {
            this.itemsList = null;
        } else if (matrix.size() == 1) {
            this.itemsList = new ExpressionList(matrix.get(0));
        } else {
            List<ExpressionList> expressionLists = new ArrayList<>();
            for (List<Expression> expressionList : matrix) {
                expressionLists.add(new ExpressionList(expressionList));
            }
            this.itemsList = new MultiExpressionList(expressionLists);
        }
    }

    @Override
    public void accept(StatementVisitor statementVisitor) {

    }

    public Table getTable() {
        return this.table;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public ItemsList getItemsList() {
        return itemsList;
    }
}
