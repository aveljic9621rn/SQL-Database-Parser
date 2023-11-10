package queryparser.statementimplementation;

import queryparser.Pair;
import queryparser.Util;
import queryparser.statementinterface.Expression;
import queryparser.statementinterface.Statement;
import queryparser.visitorInterface.StatementVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Select implements Statement {
    private String query;
    List<Expression> selectItems;
    Expression fromTable;
    String joins;
    Expression whereExpression;
    List<Expression> groupByExpressions;
    Expression havingExpression;
    List<Expression> orderByExpressions;

    public Select(String query) {
        fromTable = null;
        joins = null;
        whereExpression = null;
        groupByExpressions = null;
        havingExpression = null;
        orderByExpressions = null;

        this.query = query.trim();
        query = query.trim().substring(6).trim();
        query = query.replaceAll("\\s+", " ");

        selectItems = new ArrayList<>();
        String beforeItem = "";
        while (!query.toUpperCase(Locale.ROOT).startsWith("FROM")) {
            Pair<String, String> selectItemResult = Util.readWordOrQuotes(query, new Character[]{',', ' '}, new Character[]{'`'});
            String item = beforeItem + selectItemResult.getFirst().trim();
            query = selectItemResult.getSecond().trim();

            int countO = 0;
            int countC = 0;

            for (int i = 0; i < item.length(); i++) {
                if (item.charAt(i) == '(')
                    countO++;
                if (item.charAt(i) == ')')
                    countC++;
            }
            if (countO > countC) {
                beforeItem = item + ",";
                if (query.startsWith(","))
                    query = query.substring(1).trim();

                continue;
            } else {
                beforeItem = "";
            }

            if (query.trim().toUpperCase(Locale.ROOT).startsWith("AS")) {
                String queryOst = query.trim().substring(2).trim();

                if (queryOst.startsWith("\"") && queryOst.indexOf('"') != queryOst.lastIndexOf('"')) {
                    queryOst = queryOst.substring(1);

                    String alias = queryOst.substring(0, queryOst.indexOf('"'));
                    item += " AS \"" + alias + "\"";
                    queryOst = queryOst.substring(queryOst.indexOf("\"") + 1).trim();
                    query = queryOst;
                } else if (queryOst.indexOf(',') != -1 || query.toUpperCase(Locale.ROOT).indexOf("FROM") != -1) {
                    int pz = queryOst.indexOf(",");
                    int pf = queryOst.toUpperCase(Locale.ROOT).indexOf("FROM");
                    int sc;
                    if (pf == -1) return;
                    if (pz == -1) sc = pf;
                    else sc = Math.min(pf, pz);
                    item += " AS " + queryOst.substring(0, sc);
                    queryOst = queryOst.substring(sc);
                    query = queryOst;
                }
            } else if (query.trim().toUpperCase(Locale.ROOT).startsWith("\"")) {
                String queryOst = query.trim();
                if (queryOst.startsWith("\"") && queryOst.indexOf('"') != queryOst.lastIndexOf('"')) {
                    queryOst = queryOst.substring(1);

                    String alias = queryOst.substring(0, queryOst.indexOf('"'));
                    item += " AS \"" + alias + "\"";
                    queryOst = queryOst.substring(queryOst.indexOf("\"") + 1).trim();
                    query = queryOst;
                }
            } else if (!query.trim().startsWith(",") && !query.trim().toUpperCase(Locale.ROOT).startsWith("FROM")) {
                int pz = query.indexOf(",");
                int pf = query.toUpperCase(Locale.ROOT).indexOf("FROM");
                int sc;
                if (pf == -1) return;
                if (pz == -1) sc = pf;
                else sc = Math.min(pf, pz);
                item += " AS " + query.substring(0, sc);
                query = query.substring(sc);
            }

            if (item != null)
                selectItems.add(new GeneralExpressionImplementation(item));
            if (query.length() == 0) {
                if (selectItems.size() == 0) selectItems = null;
                return;
            }
            if (query.startsWith(","))
                query = query.substring(1).trim();
        }
        if (selectItems.size() == 0) selectItems = null;

        query = query.substring(4).trim();
        String last = "FROM";
        if (query.toUpperCase(Locale.ROOT).contains("JOIN")) {
            String untilJoin = query.substring(0, query.toUpperCase(Locale.ROOT).indexOf("JOIN")).trim();
            if (untilJoin.length() > 0 && last.equalsIgnoreCase("FROM"))
                fromTable = new GeneralExpressionImplementation(untilJoin.trim());
            last = "JOIN";
        }
        if (query.toUpperCase(Locale.ROOT).contains("WHERE")) {
            String untilWhere = query.substring(0, query.toUpperCase(Locale.ROOT).indexOf("WHERE")).trim();
            query = query.substring(query.toUpperCase(Locale.ROOT).indexOf("WHERE")).trim().substring(5).trim();

            if (untilWhere.length() > 0 && last.equals("JOIN"))
                joins = untilWhere.substring(fromTable.toString().length()).trim();

            if (untilWhere.length() > 0 && last.equalsIgnoreCase("FROM"))
                fromTable = new GeneralExpressionImplementation(untilWhere.trim());

            last = "WHERE";
        }
        if (query.toUpperCase(Locale.ROOT).contains("GROUP BY")) {
            String untilGroupBy = query.substring(0, query.toUpperCase(Locale.ROOT).indexOf("GROUP BY")).trim();
            query = query.substring(query.toUpperCase(Locale.ROOT).indexOf("GROUP BY")).trim().substring(8).trim();

            if (untilGroupBy.length() > 0 && last.equals("WHERE"))
                whereExpression = new GeneralExpressionImplementation(untilGroupBy);

            if (untilGroupBy.length() > 0 && last.equals("JOIN"))
                joins = untilGroupBy.substring(fromTable.toString().length()).trim();
            if (untilGroupBy.length() > 0 && last.equalsIgnoreCase("FROM"))
                fromTable = new GeneralExpressionImplementation(untilGroupBy.trim());

            last = "GROUP BY";
        }
        if (query.toUpperCase(Locale.ROOT).contains("HAVING")) {
            String untilHaving = query.substring(0, query.toUpperCase(Locale.ROOT).indexOf("HAVING")).trim();
            query = query.substring(query.toUpperCase(Locale.ROOT).indexOf("HAVING")).trim().substring(6).trim();

            if (untilHaving.length() > 0 && last.equals("GROUP BY"))
                groupByExpressions = GeneralExpressionImplementation.toList(untilHaving);

            if (untilHaving.length() > 0 && last.equals("WHERE"))
                whereExpression = new GeneralExpressionImplementation(untilHaving);
            if (untilHaving.length() > 0 && last.equals("JOIN"))
                joins = untilHaving.substring(fromTable.toString().length()).trim();
            if (untilHaving.length() > 0 && last.equalsIgnoreCase("FROM"))
                fromTable = new GeneralExpressionImplementation(untilHaving.trim());

            last = "HAVING";
        }
        if (query.toUpperCase(Locale.ROOT).contains("ORDER BY")) {
            String untilOrderBy = query.substring(0, query.toUpperCase(Locale.ROOT).indexOf("ORDER BY")).trim();
            query = query.substring(query.toUpperCase(Locale.ROOT).indexOf("ORDER BY")).trim().substring(8).trim();
            if (untilOrderBy.length() > 0 && last.equals("HAVING"))
                havingExpression = new GeneralExpressionImplementation(untilOrderBy);

            if (untilOrderBy.length() > 0 && last.equals("GROUP BY"))
                groupByExpressions = GeneralExpressionImplementation.toList(untilOrderBy);
            if (untilOrderBy.length() > 0 && last.equals("WHERE"))
                whereExpression = new GeneralExpressionImplementation(untilOrderBy);
            if (untilOrderBy.length() > 0 && last.equals("JOIN"))
                joins = untilOrderBy.substring(fromTable.toString().length()).trim();
            if (untilOrderBy.length() > 0 && last.equalsIgnoreCase("FROM"))
                fromTable = new GeneralExpressionImplementation(untilOrderBy.trim());

            last = "ORDER BY";
        }

        query = query.trim();
        if (query.length() > 0 && last.equals("ORDER BY"))
            orderByExpressions = GeneralExpressionImplementation.toList(query);

        if (query.length() > 0 && last.equals("HAVING")) havingExpression = new GeneralExpressionImplementation(query);

        if (query.length() > 0 && last.equals("GROUP BY"))
            groupByExpressions = GeneralExpressionImplementation.toList(query);

        if (query.length() > 0 && last.equals("WHERE")) whereExpression = new GeneralExpressionImplementation(query);

        if (query.length() > 0 && last.equals("JOIN")) joins = query.substring(fromTable.toString().length()).trim();

        if (query.length() > 0 && last.equalsIgnoreCase("FROM"))
            fromTable = new GeneralExpressionImplementation(query.trim());
    }

    @Override
    public void accept(StatementVisitor statementVisitor) {

    }

    public List<Expression> getSelectItems() {
        return selectItems;
    }

    public Expression getFromTable() {
        return fromTable;
    }

    public String getJoins() {
        return joins;
    }

    public Expression getWhereExpression() {
        return whereExpression;
    }

    public String getQuery() {
        return query;
    }
}
