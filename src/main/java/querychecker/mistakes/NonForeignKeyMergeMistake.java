package querychecker.mistakes;

import main.AppCore;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import observer.notifications.MistakeNotification;
import resource.DBNode;
import resource.implementation.Attribute;
import resource.implementation.Entity;
import resource.implementation.InformationResource;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

public class NonForeignKeyMergeMistake extends Mistake {
    @Override
    public List<MistakeNotification> check(String query) {
        List<MistakeNotification> list = new ArrayList<>();
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Statement statement;
        try {
            statement = parserManager.parse(new StringReader(query));
        } catch (Exception e) {
            //e.printStackTrace();
            return list;//Ova greska ne proverava sintaksu
        }
        if (!(statement instanceof Select select)) return list;
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        FromItem leftItem = plainSelect.getFromItem();
        if (!(leftItem instanceof Table leftTable)) return list;//Ne treba nam ostalo

        List<Join> joins = plainSelect.getJoins();
        if (joins == null || joins.size() != 1) return list;//Ne treba nam ostalo
        Join join = joins.get(0);
        if (!(join.getRightItem() instanceof Table rightTable)) return list;//Ne treba ostalo

        Column leftColumn = null;
        Column rightColumn = null;
        Table leftColumnTable = null;
        Table rightColumnTable = null;
        if (join.getUsingColumns() != null) {
            List<Column> columns = join.getUsingColumns();
            if (columns.size() != 1) return list; //Ne radi za slozeni strani kljuc

            Column column = columns.get(0);

            leftColumn = column;
            rightColumn = column;

            leftColumnTable = leftTable;
            rightColumnTable = rightTable;
            //Kljuc jedne u drugoj
        } else {
            Expression onExpression = join.getOnExpression();
            while (onExpression instanceof Parenthesis parenthesis) onExpression = parenthesis.getExpression();
            if (!(onExpression instanceof EqualsTo equalsTo)) return list;
            if (!(equalsTo.getLeftExpression() instanceof Column)) return list;
            leftColumn = (Column) equalsTo.getLeftExpression();
            if (!(equalsTo.getRightExpression() instanceof Column)) return list;
            rightColumn = (Column) equalsTo.getRightExpression();


            Map<String, Table> aliases = new HashMap<>();
            if (leftColumn.getTable().getName() == null && rightColumn.getTable().getName() == null) {
                //Naci kolone u tabelama
                List<Attribute> columnsOfLeftTable = AppCore.getInstance().getDatabase().getColumnsInTable(leftTable.getName().toLowerCase(Locale.ROOT));
                List<Attribute> columnsOfRightTable = AppCore.getInstance().getDatabase().getColumnsInTable(rightTable.getName().toLowerCase(Locale.ROOT));
                if (columnsOfRightTable == null || columnsOfLeftTable == null) return list;

                Column finalLeftColumn = leftColumn;
                Column finalRightColumn = rightColumn;
                boolean bothL = columnsOfRightTable.stream().map(e -> e.getName().trim()).anyMatch(e -> e.equalsIgnoreCase(finalLeftColumn.getColumnName().trim()))
                        && columnsOfLeftTable.stream().map(e -> e.getName().trim()).anyMatch(e -> e.equalsIgnoreCase(finalLeftColumn.getColumnName().trim()));
                boolean bothR = columnsOfRightTable.stream().map(e -> e.getName().trim()).anyMatch(e -> e.equalsIgnoreCase(finalRightColumn.getColumnName().trim()))
                        && columnsOfLeftTable.stream().map(e -> e.getName().trim()).anyMatch(e -> e.equalsIgnoreCase(finalRightColumn.getColumnName().trim()));

                if (bothL) {
                    list.add(new MistakeNotification("Ime kolone je dvoznacno",
                            "Kolona " + leftColumn.getColumnName() + " postoji u obe tabele i ne zna se na sta se odnosi",
                            "Dodajte alijase u upit"));
                }
                if (bothR) {
                    list.add(new MistakeNotification("Ime kolone je dvoznacno",
                            "Kolona " + rightColumn.getColumnName() + " postoji u obe tabele i ne zna se na sta se odnosi",
                            "Dodajte alijase u upit"));
                }

                for (Attribute attribute : columnsOfLeftTable) {
                    if (attribute.getName().trim().equalsIgnoreCase(leftColumn.getColumnName().trim())) {
                        leftColumnTable = leftTable;
                        if (columnsOfRightTable.stream()
                                .map(e -> e.getName().toLowerCase(Locale.ROOT).trim()).collect(Collectors.toList())
                                .contains(rightColumn.getColumnName().toLowerCase(Locale.ROOT).trim()))
                            rightColumnTable = rightTable;
                    }
                    if (attribute.getName().trim().equalsIgnoreCase(rightColumn.getColumnName().trim())) {
                        rightColumnTable = leftTable;
                        if (columnsOfRightTable.stream()
                                .map(e -> e.getName().toLowerCase(Locale.ROOT).trim()).collect(Collectors.toList())
                                .contains(leftColumn.getColumnName().toLowerCase(Locale.ROOT).trim()))
                            leftColumnTable = rightTable;
                    }
                }
                if (leftColumnTable == null || rightColumnTable == null) {
                    if (leftColumnTable == null)
                        list.add(new MistakeNotification("Kolona ne postoji",
                                "Kolona " + leftColumn.getColumnName() + " ne postoji",
                                "Dodajte je ili proverite  da li ste napravili slovnu gresku"));
                    if (rightColumnTable == null)
                        list.add(new MistakeNotification("Kolona ne postoji",
                                "Kolona " + rightColumn.getColumnName() + " ne postoji",
                                "Dodajte je ili proverite  da li ste napravili slovnu gresku"));
                    return list;
                }
            } else {
                if (leftTable.getAlias() != null &&
                        ((leftColumn.getTable().getName() != null && leftTable.getAlias().toString().trim().equalsIgnoreCase(leftColumn.getTable().getName().trim()))
                                || (rightColumn.getTable().getName() != null && leftTable.getAlias().toString().trim().equalsIgnoreCase(rightColumn.getTable().getName().trim()))))
                    aliases.put(leftTable.getAlias().toString().trim(), leftTable);
                else aliases.put("|-*+--f39ia0fl78537438", leftTable);
                if (rightTable.getAlias() != null &&
                        ((leftColumn.getTable().getName() != null && rightTable.getAlias().toString().trim().equalsIgnoreCase(leftColumn.getTable().getName().trim()))
                                || (rightColumn.getTable().getName() != null && rightTable.getAlias().toString().trim().equalsIgnoreCase(rightColumn.getTable().getName().trim()))))
                    aliases.put(rightTable.getAlias().toString().trim(), rightTable);
                else aliases.put("|-*+--f39ia0fr78537438", rightTable);
                //Ako alijas postoji, ali nije iskoriscen
                leftColumnTable = aliases.get(leftColumn.getTable().getName() == null ? "|-*+--f39ia0fl78537438" : leftColumn.getTable().getName().toLowerCase(Locale.ROOT).trim());
                rightColumnTable = aliases.get(rightColumn.getTable().getName() == null ? "|-*+--f39ia0fr78537438" : rightColumn.getTable().getName().toLowerCase(Locale.ROOT).trim());
            }


        }

        InformationResource myResource = ((InformationResource) AppCore.getInstance().getDatabase().loadResource());
        //UZIMANJE PO ALIJASU
        List<DBNode> leftTableColumns = null;// = AppCore.getInstance().getDatabase().getColumnsInTable(leftTable.getName().toLowerCase(Locale.ROOT));
        List<DBNode> rightTableColumns = null;// = AppCore.getInstance().getDatabase().getColumnsInTable(rightTable.getName().toLowerCase(Locale.ROOT));

        if (leftColumnTable == null || rightColumnTable == null) {
            if (leftColumnTable == null && leftColumn.getTable().getName() != null) {
                list.add(new MistakeNotification("Nedefinisan alijas",
                        "Alijas " + leftColumn.getTable().getName() + " ne postoji u bazi",
                        "Dodajte ga ili proverite  da li ste napravili slovnu gresku"));
            }
            if (rightColumnTable == null && rightColumn.getTable().getName() != null) {
                list.add(new MistakeNotification("Nedefinisan alijas",
                        "Alijas " + rightColumn.getTable().getName() + " ne postoji u bazi",
                        "Dodajte ga ili proverite  da li ste napravili slovnu gresku"));
            }
            return list;//Greska sa alijasima
        }

        for (DBNode dbNode : myResource.getChildren()) {
            if (!(dbNode instanceof Entity entity)) continue;
            if (entity.getName().equalsIgnoreCase(leftColumnTable.getName())) {
                leftTableColumns = entity.getChildren();
            }
            if (entity.getName().equalsIgnoreCase(rightColumnTable.getName())) {
                rightTableColumns = entity.getChildren();
            }
        }
        if (leftTableColumns == null || rightTableColumns == null) {
            if (leftTableColumns == null)
                list.add(new MistakeNotification("Tabela ne postoji",
                        "Tabela" + leftTable.getName() + " ne postoji u bazi",
                        "Dodajte je ili proverite  da li ste napravili slovnu gresku"));
            if (rightTableColumns == null)
                list.add(new MistakeNotification("Tabela ne postoji",
                        "Tabela" + rightTable.getName() + " ne postoji u bazi",
                        "Dodajte je ili proverite  da li ste napravili slovnu gresku"));
            return list;
        }

        Attribute leftAttribute = null;
        Attribute rightAttribute = null;
        for (DBNode dbNode : leftTableColumns) {
            if (!(dbNode instanceof Attribute attribute)) return list;
            if (attribute.getName().equalsIgnoreCase(leftColumn.getColumnName())) {
                leftAttribute = attribute;
            }
        }
        for (DBNode dbNode : rightTableColumns) {
            if (!(dbNode instanceof Attribute attribute)) return list;
            if (attribute.getName().equalsIgnoreCase(rightColumn.getColumnName())) {
                rightAttribute = attribute;
            }
        }
        if (leftAttribute == null || rightAttribute == null) {
            if (leftAttribute == null)
                list.add(new MistakeNotification("Kolona ne postoji",
                        "Kolona " + leftColumn.getColumnName() + " ne postoji u tabeli " + leftColumnTable.getName(),
                        "Dodajte je ili proverite  da li ste napravili slovnu gresku"));
            if (rightAttribute == null)
                list.add(new MistakeNotification("Kolona ne postoji",
                        "Kolona " + rightColumn.getColumnName() + " ne postoji u tabeli " + rightColumnTable.getName(),
                        "Dodajte je ili proverite  da li ste napravili slovnu gresku"));
            return list;
        }
        if (leftAttribute.getInRelationWith() != rightAttribute && rightAttribute.getInRelationWith() != leftAttribute) {
            list.add(new MistakeNotification(getName(),
                    String.format(getDescription(), "Ni jedna od kolona " + leftAttribute.getParent().getName() + "." +
                            leftAttribute.getName() + " i " + rightAttribute.getParent().getName() + "." +
                            rightAttribute.getName() + " nije strani kljuc koji pokazuje na drugu. "),
                    String.format(getRecommendation(), "Definisite Foreign Key pravilo u jednoj od tabela ")));
        }
        return list;

    }

    public NonForeignKeyMergeMistake(String name, String description, String recommendation) {
        super(name, description, recommendation);
    }
}
