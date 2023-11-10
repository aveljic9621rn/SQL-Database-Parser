package querychecker.mistakes;

import main.AppCore;
import observer.notifications.MistakeNotification;
import queryparser.StatementParser;
import queryparser.statementimplementation.*;
import queryparser.statementinterface.Expression;
import queryparser.statementinterface.ItemsList;
import queryparser.statementinterface.Statement;
import resource.enums.AttributeType;
import resource.implementation.Attribute;

import java.util.*;

public class InsertWrongColumnsMistake extends Mistake {
    @Override
    public List<MistakeNotification> check(String query) {
        List<MistakeNotification> list = new ArrayList<>();
        StatementParser parserManager = new StatementParser();
        Statement statement = parserManager.parseQuery(query);
        if (statement == null) return list;
        if (!(statement instanceof Insert insert)) return list;
        Table table = insert.getTable();
        if (table == null) return null;//Nema tabele

        List<Column> columns = insert.getColumns();
        if (columns == null) return null;//nema kolona
        List<Attribute> columnList = AppCore.getInstance().getDatabase().getColumnsInTable(table.getName());
        if (columnList == null)
            return list;//Tabela ne postoji, nece se desiti u bulk importu, ali mozda treba da se doda nesto za obican insert
        List<String> columnNamesInQuery = columns.stream().map(e -> e.getColumnName().toLowerCase(Locale.ROOT)).toList();
        List<String> columnNamesInTable = columnList.stream().map(e -> e.getName().toLowerCase(Locale.ROOT)).toList();
        List<String> notFoundColumns = new ArrayList<>(columnNamesInQuery);
        notFoundColumns.removeAll(columnNamesInTable);
        for (String notFoundColumn : notFoundColumns) {
            list.add(new MistakeNotification(getName(),
                    String.format(getDescription(), "Column " + notFoundColumn + " exists in the insert statement, but not in the table. "),
                    String.format(getRecommendation(), "Remove that column from the statement (or the CSV file if you used import option). ")));
        }
        for (Attribute attribute : columnList) {
            if (!attribute.isNullable() && !attribute.isAutoIncrement() && !columnNamesInQuery.contains(attribute.getName().toLowerCase(Locale.ROOT))) {
                list.add(new MistakeNotification(getName(),
                        String.format(getDescription(), "Attribute " + attribute.getName() + " has to be specified in the query. "),
                        String.format(getRecommendation(), "Add that attribute to the statement (or the CSV file if you used import option). ")));
            }
        }
        //Da li je neka od ubacenih vrednosti u tabeli null, tamo gde je stavljeno not null?

        ItemsList itemsList = insert.getItemsList();
        List<List<Expression>> expressionMatrix = new ArrayList<>();
        if (itemsList instanceof ExpressionList expressionList) {
            expressionMatrix.add(expressionList.getExpressions());
        } else if (itemsList instanceof MultiExpressionList multiExpressionList) {
            multiExpressionList.getExprList().forEach(e -> {
                expressionMatrix.add(e.getExpressions());
            });

        }
        List<AttributeType> numberAttributeTypes = Arrays.asList(AttributeType.BIGINT, AttributeType.SMALLINT, AttributeType.INT, AttributeType.INT_UNSIGNED, AttributeType.NUMERIC, AttributeType.FLOAT, AttributeType.DECIMAL, AttributeType.DECIMAL_UNSIGNED);

        for (List<Expression> expressions : expressionMatrix) {
            int counter = 0;
            for (Expression expression : expressions) {
                int finalCounter = counter;
                Attribute attribute = columnList.stream().filter(e -> e.getName().equalsIgnoreCase(columns.get(finalCounter).getColumnName())).toList().get(0);
                if (!attribute.isNullable() && !attribute.isAutoIncrement() && expression instanceof NullValue) {
                    String pointer = expressions.stream().map(e -> e.toString()).reduce("", (prev, nov) -> prev + nov + ", ");
                    pointer = pointer.substring(0, pointer.length() - 2);
                    list.add(new MistakeNotification(getName(),
                            String.format(getDescription(), "Attribute " + attribute.getName() + " can not be null at line (" + pointer + "). "),
                            String.format(getRecommendation(), "Add value to that attribute to the row in the statement or the CSV file. ")));
                }
                if (numberAttributeTypes.contains(attribute.getAttributeType()) && expression instanceof StringValue) {
                    String pointer = expressions.stream().map(e -> e.toString()).reduce("", (prev, nov) -> prev + nov + ", ");
                    pointer = pointer.substring(0, pointer.length() - 2);
                    list.add(new MistakeNotification(getName(),
                            String.format(getDescription(), "Attribute " + attribute.getName() + " is a number, but it is provided with a string value " + expression.toString() + " at line (" + pointer + "). "),
                            String.format(getRecommendation(), "Do not assign strings to the numeric columns. ")));
                }
                counter++;
            }
        }

        return list;
    }

    public InsertWrongColumnsMistake(String name, String description, String recommendation) {
        super(name, description, recommendation);
    }
}
